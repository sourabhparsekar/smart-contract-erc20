package com.ethereum.smartcontract.controller;

import com.ethereum.smartcontract.utils.Constants;
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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.token.Token;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@RestController
public class SmartContractController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @PostMapping("/deploy")
    @Operation(summary = "Deploy The Smart Contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Smart Contract deployed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request / Node URL not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<String> deploySmartContract(@RequestBody Long tokenValue, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        String nodeUrl = httpServletRequest.getHeader(Constants.ETHEREUM_NODE_URL);

        if (nodeUrl == null || nodeUrl.isEmpty()) {
            logger.error("{} is not available in Header.", Constants.ETHEREUM_NODE_URL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Constants.ETHEREUM_NODE_URL + " is not available in Header.");
        }

        String privateKey = httpServletRequest.getHeader(Constants.PRIVATE_KEY);

        if (privateKey == null || privateKey.isEmpty()) {
            logger.error("{} is not available in Header.", Constants.PRIVATE_KEY);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.PRIVATE_KEY + " is not available in Header.");
        }

        Web3j web3j = null;
        Token token;

        try {
            web3j = Web3j.build(new HttpService(nodeUrl));

            Credentials credentials = Credentials.create(privateKey);

            EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();

            ContractGasProvider contractGasProvider = new StaticGasProvider(
                    web3j.ethGasPrice().send().getGasPrice(),
                    block.getBlock().getGasLimit()
            );

            token = Token.deploy(
                    web3j,
                    credentials,
                    contractGasProvider,
                    BigInteger.valueOf(tokenValue)
            ).send();

            logger.info("Smart Contract deployed @ {}", token.getContractAddress());

            return ResponseEntity.ok("Smart Contract deployed @ " + token.getContractAddress());

        } catch (Exception e) {
            logger.error("Deployment Error.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deployment Error. Message: " + e.getMessage());
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }
    }
}
