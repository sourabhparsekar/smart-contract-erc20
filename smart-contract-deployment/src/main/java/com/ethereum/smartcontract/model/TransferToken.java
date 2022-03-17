package com.ethereum.smartcontract.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class TransferToken {

    @JsonProperty(value = "to-wallet-address")
    private String walletAddress;

    @JsonProperty(value = "token-amount")
    private BigInteger amount;

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
