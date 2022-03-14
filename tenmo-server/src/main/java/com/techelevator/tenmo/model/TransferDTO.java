package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDTO {
    private String sendToUsername;
    private BigDecimal amount;
    private int transferType; //1-request 2-send
    private int transferStatus; //1-pending 2-approved 3-rejected

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    public void setSendToUsername(String sendToUsername) {
        this.sendToUsername = sendToUsername;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSendToUsername() {
        return sendToUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
