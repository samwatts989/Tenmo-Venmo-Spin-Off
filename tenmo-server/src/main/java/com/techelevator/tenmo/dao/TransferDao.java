package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface TransferDao {

    Transfer findByToUser(User user);

    Transfer findByFromUser(User user);

    Transfer findByUserStatus(User user, String status);

}
