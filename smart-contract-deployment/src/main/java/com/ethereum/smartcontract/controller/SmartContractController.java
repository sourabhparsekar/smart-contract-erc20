package com.ethereum.smartcontract.controller;

import com.ethereum.smartcontract.exception.Web3JException;
import com.ethereum.smartcontract.model.ContractDeploy;
import com.ethereum.smartcontract.utils.Constants;
import com.ethereum.smartcontract.utils.Web3JUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.token.Token;
import org.web3j.tx.gas.ContractGasProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@RestController
public class SmartContractController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @PostMapping("/deploy")
    @Operation(summary = "Deploy The Smart Contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract deployed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ContractDeploy.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<String> deploySmartContract(@RequestBody ContractDeploy contractDeploy, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Web3j web3j = null;
        Token token;

        try {
            web3j = Web3j.build(new HttpService(Web3JUtils.getHeader(httpServletRequest, Constants.ETHEREUM_NODE_URL)));

            Credentials credentials = Credentials.create(Web3JUtils.getHeader(httpServletRequest, Constants.PRIVATE_KEY));

            ContractGasProvider contractGasProvider = Web3JUtils.getGasProvider(web3j);

            token = Token.deploy(
                    web3j,
                    credentials,
                    contractGasProvider,
                    BigInteger.valueOf(contractDeploy.getInitialAmount())
            ).send();

            logger.info("Smart Contract deployed @ {}", token.getContractAddress());

            return ResponseEntity.ok("Smart Contract deployed @" + token.getContractAddress());

        } catch (Web3JException e) {
            logger.error("Web3J Exception. Message : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Web3J Exception. Message : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Deployment Error.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deployment Error. Message: " + e.getMessage());
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }
    }


}
