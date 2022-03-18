package com.ethereum.smartcontract.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContractDeployTest {

    private ContractDeploy contractDeploy;

    @BeforeEach
    void init() {
        contractDeploy = new ContractDeploy();
    }

    @Test
    void getInitialAmount() {
        contractDeploy.setInitialAmount(100L);
        Assertions.assertEquals(100L, contractDeploy.getInitialAmount());
    }

    @Test
    void setInitialAmount() {
        Assertions.assertDoesNotThrow(() -> contractDeploy.setInitialAmount(100L));
    }
}