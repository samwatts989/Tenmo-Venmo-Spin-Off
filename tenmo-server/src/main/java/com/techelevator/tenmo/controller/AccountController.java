package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
    @RestController
    @RequestMapping("/transaction")
    public class AccountController {

        private AccountDao dao;
        private UserDao userDao;
        private TransferDTO transferDTO;

        //never put TransferDTO in Controller
        //was returning a bean error

        public AccountController(AccountDao dao, UserDao userDao) {
            this.dao = dao;
            this.userDao = userDao;
        }


        //1check my balance
        @RequestMapping(path = "", method = RequestMethod.GET)
        public BigDecimal get(Principal principal) {
            return dao.getBalance(principal.getName());
        }


//        //gets a list of all accounts
        @RequestMapping(path = "/list", method = RequestMethod.GET)
        public List<String> usersList(){
            return dao.listUsers();}


    //get all transfers
    @RequestMapping(path = "/gettransfers", method = RequestMethod.GET)
    public List<Transfer> transferList(TransferDTO transferDTO, Principal principal) {

       int userFromAcctId = dao.processTransferFromUsername(principal.getName(), transferDTO.getTransferType(), transferDTO.getTransferStatus(), transferDTO.getAmount());

        return dao.getTransfers(userFromAcctId);
       // return dao.getTransfers(2001);
    }

    @RequestMapping(path = "/gettransfers/{id}", method = RequestMethod.GET)
    public Transfer transferDetails(TransferDTO transferDTO, Principal principal, @PathVariable int id) {

        int userFromAcctId = dao.processTransferFromUsername(principal.getName(), transferDTO.getTransferType(), transferDTO.getTransferStatus(), transferDTO.getAmount());

        return dao.getTransferById(userFromAcctId,id); // do we need user acct id and id
        // return dao.getTransfers(2001);
    }



    //    @RequestMapping(path = "/list", method = RequestMethod.GET)
//    public List<User> findAll(){
//       List<User> userList =  userDao.findAll();
//       return userList;
//   }
//
//        //2 send transfers
        //@ResponseStatus(HttpStatus.CREATED)
        @RequestMapping(path = "/transfer", method = RequestMethod.POST)
        public void transfer(@Valid @RequestBody TransferDTO transferDTO, Principal principal) throws Exception {
            //find the balance of the principal account
            //if username does not = principal && if amount < account balance then
            if(principal.getName().equals(transferDTO.getSendToUsername()) || (transferDTO.getAmount().compareTo(dao.getBalance(principal.getName()))>0) || (transferDTO.getAmount().compareTo(BigDecimal.ZERO)<=0) || !dao.listUsers().contains(transferDTO.getSendToUsername())){
                throw new Exception("Can't send money");
            }//method for updating balance for sender and receiver
            dao.transfer(principal.getName(), transferDTO.getSendToUsername(),transferDTO.getAmount());
            //dao.transferSubtract(principal.getName(), transferDTO.getAmount());
            int userFromAcctId = dao.processTransferFromUsername(principal.getName(), transferDTO.getTransferType(), transferDTO.getTransferStatus(), transferDTO.getAmount());
            int userToAcctId = dao.processTransferToUsername(transferDTO.getSendToUsername(), transferDTO.getTransferType(), transferDTO.getTransferStatus(), transferDTO.getAmount());
            dao.updateTransferTable(userFromAcctId, userToAcctId,transferDTO.getTransferStatus(), transferDTO.getAmount(),transferDTO.getTransferType());
       }




    //display list of users to send $ to (name,acct#?) --list?
    //cant send more than in my account
    //cant send 0 or negative
    //my (senders) account decreased by amount
    //receivers account increased by amount






}
