package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    BigDecimal getBalance(String username);

    List<String > listUsers();

    void transfer(String fromUsername, String toUsername, BigDecimal amount);

    void transferAdd(String toUsername, BigDecimal amount);

    void transferSubtract(String fromUsername, BigDecimal amount);

    int processTransferToUsername(String toUsername, int transferType, int transferStatus, BigDecimal amount);

    int processTransferFromUsername(String fromUsername, int transferType, int transferStatus, BigDecimal amount);

    void updateTransferTable(int fromAccountId, int toAccountId, int transferStatus,BigDecimal amount,int transferType);

    List<Transfer> getTransfers(int accountNum);

   Transfer getTransferById(int accountNum, int id);
}
