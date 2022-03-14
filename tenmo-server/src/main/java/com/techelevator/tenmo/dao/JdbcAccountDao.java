package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(String userName) {
        String sql = "select  balance\n" +
                "from account\n" +
                "inner join tenmo_user on tenmo_user.user_id = account.user_id\n" +
                "where username = ?;";
        //Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userName);
        return balance;
    }

    @Override
    public List<String> listUsers() {
        List<String> returnedListOfAccounts = new ArrayList<>();
        String sql = "select username from tenmo_user;";
        return returnedListOfAccounts = jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public void transfer(String fromUsername, String toUsername, BigDecimal amount) {

        //possible if statement here based on what type of transfer... if 2(send)continue
        String sql = "UPDATE account\n" +
                "SET balance = balance - ?\n" +
                "from tenmo_user\n" +
                "WHERE tenmo_user.user_id = account.user_id AND username= ?;"; // may need to remove quotes
        jdbcTemplate.update(sql, amount, fromUsername);

        String sql2 = "UPDATE account\n" +
                "SET balance = balance + ?\n" +
                "from tenmo_user\n" +
                "WHERE tenmo_user.user_id = account.user_id AND username= ?;"; // may need to remove quotes
        jdbcTemplate.update(sql2, amount, toUsername);
    }

    @Override
    public int processTransferFromUsername(String fromUsername, int transferType, int transferStatus, BigDecimal amount) {
        int returnValue =0;
        String sqlFromUserId = "SELECT account_id " +
                "FROM account " +
                "JOIN tenmo_user on tenmo_user.user_id = account.user_id " +
                "where username = ? ;";
        SqlRowSet fromAccountId = jdbcTemplate.queryForRowSet(sqlFromUserId, fromUsername);
        if (fromAccountId.next()) {
            Transfer transfer = mapRowToUser(fromAccountId);
            returnValue =transfer.getToAccount();
        }return returnValue;
    }

    @Override
    public int processTransferToUsername(String toUsername, int transferType, int transferStatus, BigDecimal amount) {
        int returnValue = 0;
        String sqlToUserId = "SELECT account_id " +
                "FROM account " +
                "JOIN tenmo_user on tenmo_user.user_id = account.user_id " +
                "where username = ? ;";
        SqlRowSet toAccountId = jdbcTemplate.queryForRowSet(sqlToUserId, toUsername);
        if (toAccountId.next()) {
            Transfer transfer = mapRowToUser(toAccountId);
            returnValue = transfer.getToAccount();
        }return returnValue;
    }

    @Override
    public void updateTransferTable(int fromAccountId, int toAccountId, int transferStatus,BigDecimal amount,int transferType){
        String sql = "INSERT INTO transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount)\n" +
                "VALUES (?,?,?,?,?);";
        jdbcTemplate.update(sql, transferType,transferStatus,fromAccountId,toAccountId,amount);
    }

    @Override
    public List<Transfer> getTransfers(int accountNum) {
        List<Transfer> returnList = new ArrayList<>();
        //String sql = "select username, account.account_id, transfer_type_desc, transfer_status_desc, amount, account_from, account_to\n" +
        String sql = "select transfer_id, transfer_status_desc, amount, account_from, account_to\n" +
                "from transfer\n" +
                "join transfer_type on transfer_type.transfer_type_id = transfer.transfer_type_id\n" +
                "join transfer_status on transfer_status.transfer_status_id = transfer.transfer_status_id\n" +
                "join account on account.account_id = transfer.account_from\n" +
                "Join tenmo_user on tenmo_user.user_id = account.user_id\n" +
                "where account_from = ? or account_to = ?\n"+
                "order by transfer_id;";
         SqlRowSet results =jdbcTemplate.queryForRowSet(sql, accountNum, accountNum); //transfer - nothing - string
         while (results.next()) {
             returnList.add(mapRowToTransfer(results));
         }
         return returnList;
    }


    @Override
    //public Transfer getTransferById(int accountNum, int id) {
    public Transfer getTransferById(int accountNum, int id) {
        Transfer returnedTransfer =null;
        //String sql = "select username, account.account_id, transfer_type_desc, transfer_status_desc, amount, account_from, account_to\n" +
        String sql = "select transfer_id, transfer_status_desc, amount, account_from, account_to\n" +
                "from transfer\n" +
                "join transfer_type on transfer_type.transfer_type_id = transfer.transfer_type_id\n" +
                "join transfer_status on transfer_status.transfer_status_id = transfer.transfer_status_id\n" +
                "join account on account.account_id = transfer.account_from\n" +
                "Join tenmo_user on tenmo_user.user_id = account.user_id\n" +
                "where transfer_id = ?;";
        SqlRowSet results =jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            returnedTransfer= mapRowToTransfer(results);
        }
        return returnedTransfer;
    }

    private Transfer mapRowToUser(SqlRowSet rowset) {
        Transfer transfer = new Transfer();
        transfer.setToAccount(rowset.getInt("account_id"));
        return transfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferStatus(rowSet.getString("transfer_status_desc"));
        transfer.setTransferAmt(rowSet.getBigDecimal("amount"));
        transfer.setFromAccount(rowSet.getInt("account_from"));
        transfer.setToAccount(rowSet.getInt("account_to"));
        return transfer;
    }
/**
 * Step number 5 is saying sent instead of received
 * if toUser == principle user ---> change send to received
 * OR
 * Create 2 tables, 1 for sent and 1 for received
 *OR
 * Take send out of table and change string based on who's logged in
 * Finish step 5 in Intellij
 * Step 6
 * Sql query where we select * from transfer where transfer_id = ?
 * OR
 * put transfer_id in the path 
 */

//        String sql = "INSERT INTO transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount)\n" +
//                "VALUES (?,?,2001,2002,?);";
//        jdbcTemplate.update(sql, transferType,transferStatus,amount);
//        }

////          , int transferType, int transferStatus
//        String sql3 = "INSERT INTO transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount)\n" +
//                "VALUES (?,?,2001,2002,?);";
//        jdbcTemplate.update(sql, transferType,transferStatus,amount);
////        //how to get account_id instead of name





    @Override
    public void transferSubtract(String fromUsername, BigDecimal amount) {
        String sql = "UPDATE account\n" +
                "SET balance = balance - ?\n" +
                "from tenmo_user\n" +
                "WHERE tenmo_user.user_id = account.user_id AND username= ?;"; // may need to remove quotes
        jdbcTemplate.update(sql, amount,fromUsername);

    }

    @Override
    public void transferAdd(String toUsername, BigDecimal amount){
        String sql = "UPDATE account\n" +
                "SET balance = balance + ?\n" +
                "from tenmo_user\n" +
                "WHERE tenmo_user.user_id = account.user_id AND username= ?;"; // may need to remove quotes
        jdbcTemplate.update(sql, amount,toUsername);

    }

}
//
//    select username, account.account_id, transfer_type_desc, transfer_status_desc, amount, account_from, account_to
//        from transfer
//        join transfer_type on transfer_type.transfer_type_id = transfer.transfer_type_id
//        join transfer_status on transfer_status.transfer_status_id = transfer.transfer_status_id
//        join account on account.account_id = transfer.account_from
//        Join tenmo_user on tenmo_user.user_id = account.user_id
//        where account_from = 2001 or account_to = 2001
