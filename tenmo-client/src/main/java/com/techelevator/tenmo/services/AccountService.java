package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {


    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    //map here is global
    Map<Integer, String> mapOfUsers = new HashMap<>();

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }




    public BigDecimal getBalance(String token) {
        //Account account = new Account();
        setAuthToken(token);
        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(API_BASE_URL + "transaction" ,
                            HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            return response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public Transfer[] getTransfers(String token){
        setAuthToken(token);
          Transfer[] transfers = null;
        try{
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "transaction/gettransfers",
                            HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }return transfers;
    }

    public Map<Integer, String> getListOfUsers(String token) {
        setAuthToken(token);
        Integer counter = 1;
        try {
            ResponseEntity<String[]> response =
                    restTemplate.exchange(API_BASE_URL + "transaction/list" ,
                            HttpMethod.GET, makeAuthEntity(), String[].class);
            String[] userName = response.getBody();

            for(int i=0;i<userName.length;i++) {
                mapOfUsers.put(counter,userName[i]);
                counter++;
            }

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return mapOfUsers;
    }

    public Transfer transferDetail(int id) {
        Transfer transfer = new Transfer();
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(API_BASE_URL + "transaction/gettransfers/" + id,
                            HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public TransferDTO sendTransfer(TransferDTO transferDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferDTO> entity = new HttpEntity<>(transferDTO, headers);

        TransferDTO returnedTransfer = null;
        try {
           returnedTransfer = restTemplate.postForObject(API_BASE_URL + "/transaction/transfer", entity, TransferDTO.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

//    public Auction add(Auction newAuction) {
//        HttpEntity<Auction> entity = makeAuctionEntity(newAuction);
//        Auction returnedAuction = null;
//        try {
//            returnedAuction = restTemplate.postForObject(API_BASE_URL, entity, Auction.class);
//        } catch (RestClientResponseException | ResourceAccessException e) {
//            BasicLogger.log(e.getMessage());
//        }
//        return returnedAuction;
//    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
