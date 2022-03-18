package com.ethereum.smartcontract.controller;

import com.ethereum.smartcontract.model.ContractDeploy;
import com.ethereum.smartcontract.model.TransactionResponse;
import com.ethereum.smartcontract.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class DeploymentControllerTest {

    @InjectMocks
    private DeploymentController deploymentController;

    private MockHttpServletRequest httpServletRequest;

    private MockHttpServletResponse httpServletResponse;

    //FIXME - this test needs a node to be running

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader(Constants.PRIVATE_KEY, "a9483281917a1d2b45296bc429270f2e15ef1e5b2657062eecac276c750a4dec");
        httpServletRequest.addHeader(Constants.ETHEREUM_NODE_URL, "http://localhost:8545");
        httpServletResponse = new MockHttpServletResponse();

    }

    @Test
    void deploySmartContract() {

        ContractDeploy contractDeploy = new ContractDeploy();
        contractDeploy.setInitialAmount(100L);

        ResponseEntity<TransactionResponse> data = deploymentController.deploySmartContract(contractDeploy, httpServletRequest, httpServletResponse);


    }
}