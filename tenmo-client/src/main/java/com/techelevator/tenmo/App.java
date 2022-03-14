package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.util.BasicLogger;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendTeBuckSubMenu();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void sendTeBuckSubMenu() {
        Principal principal;
                System.out.println("\n"+accountService.getListOfUsers(currentUser.getToken()));
        Map<Integer,String> userMap = accountService.getListOfUsers(currentUser.getToken()); //our usermap defined for shorter reference
        int menuSelection = -1;
        while (menuSelection != 0 ) {
            int usernameSelection = consoleService.promptForTransferToUserId();
            if(userMap.get(usernameSelection).equalsIgnoreCase(currentUser.getUser().getUsername())){
                System.out.println("\nCant select yourself: please try again");
                sendTeBuckSubMenu();
            };

            String userSelected = userMap.get(usernameSelection);

            BigDecimal amountToSend = consoleService.promptForBigDecimal("\nHow much money do you want to send to "+userSelected.substring(0, 1).toUpperCase() + userSelected.substring(1)+"?\n$");
               // handleRegister(); continue with transfer to selected user id

                TransferDTO newTransfer = new TransferDTO();
                newTransfer.setSendToUsername(currentUser.getUser().getUsername());
                newTransfer.setSendToUsername(userSelected);
                newTransfer.setAmount(amountToSend);
                newTransfer.setTransferType(2);
                newTransfer.setTransferStatus(2);

                TransferDTO returnedDto = accountService.sendTransfer(newTransfer);
                //System.out.println(returnedDto.getSendToUsername() +" " + returnedDto.getAmount());
           // System.out.println("\nYou've successfully sent $"+amountToSend +" to "+ userSelected+". \nThanks for using TEnmo!");

           // System.out.println("\nYou've successfully sent $"+amountToSend +" to "+ userSelected.substring(0, 1).toUpperCase() + userSelected.substring(1)+" \nThanks for using TEnmo!");
            System.out.println("\nYou've successfully sent $"+amountToSend +" to "+ userSelected.substring(0, 1).toUpperCase() + userSelected.substring(1)+" \nThanks for using TEnmo!");
            consoleService.pause();
               mainMenu();



//            } else if (menuSelection != 0) {
//                System.out.println("Invalid Selection");
//                consoleService.pause();
//            }
        }

        //ask how much $
        //check if i have enough, subtract from my account
        //add to account (mapOfUsers.get(menuselection))


    }

	private void viewCurrentBalance() {

        System.out.println("$"+accountService.getBalance(currentUser.getToken()));

		
	}

	private void viewTransferHistory() {

        Transfer[]allTransfers=null;
        try{
            allTransfers=accountService.getTransfers(currentUser.getToken());
        } catch (RestClientException e){
            BasicLogger.log(e.getMessage());
        }

        for(Transfer t:allTransfers){
//            Principal principal;
//            String typeOfTransfer = "SENT";
//
//            if(principal.getName()==t.getToName){
//                typeOfTransfer="RECEIVED";
//            }

            System.out.println("Id: "+t.getTransferId()+" Amount:$"+t.getTransferAmt()+" From: "+ t.getFromAccount()+" To: "+t.getToAccount());
        }

        // input a try catch for .NumberFormatException here to catch string input
        int userInput = Integer.parseInt(consoleService.promptForString("Enter the Transfer ID for more details or enter 0 to exit. \n"));
        if(userInput!=0){
            Transfer t = accountService.transferDetail(userInput);
            System.out.println("Id: "+t.getTransferId()+" Amount:$"+t.getTransferAmt()+" From: "+ t.getFromAccount()+" To: "+t.getToAccount());
            //System.out.println(transactionId);
         }mainMenu();
	}

    //readme#5
	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

    //readme #4
	private void sendBucks() {
		// TODO Auto-generated method stub
        //display list of users to send $ to (name,acct#?) --list?
        //cant send more than in my account
        //cant send 0 or negative
        //my (senders) account decreased by amount
        //receivers account increased by amount
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
