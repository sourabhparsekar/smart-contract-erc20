package com.ethereum.smartcontract.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContractDeploy {

    @JsonProperty(value = "initial-amount")
    private Long initialAmount;

    public Long getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Long initialAmount) {
        this.initialAmount = initialAmount;
    }
}
