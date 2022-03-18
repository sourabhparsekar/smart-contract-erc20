package com.ethereum.smartcontract.controller;

import com.ethereum.smartcontract.exception.Web3JException;
import com.ethereum.smartcontract.model.ContractDeploy;
import com.ethereum.smartcontract.model.TransactionResponse;
import com.ethereum.smartcontract.utils.Constants;
import com.ethereum.smartcontract.utils.Web3JUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.token.Token;
import org.web3j.tx.gas.ContractGasProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class DeploymentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @PostMapping("/deploy")
    @Operation(summary = "Deploy The Smart Contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract deployed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> deploySmartContract(@RequestBody ContractDeploy contractDeploy, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Web3j web3j = null;

        Token token;

        Pair<HttpStatus, TransactionResponse> transactionResponseData = Pair.of(HttpStatus.BAD_REQUEST, new TransactionResponse(null, null));

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

            transactionResponseData = Pair.of(
                    HttpStatus.OK,
                    new TransactionResponse(
                            "Smart Contract deployed",
                            token.getContractAddress()
                    )
            );

        } catch (Web3JException e) {
            logger.error("Web3J Exception. Message : {}", e.getMessage());
            transactionResponseData = Pair.of(
                    HttpStatus.UNAUTHORIZED,
                    new TransactionResponse(
                            "Web3J Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } catch (Exception e) {
            logger.error("Deployment Error.", e);
            transactionResponseData = Pair.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    new TransactionResponse(
                            "Deployment Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }

        return ResponseEntity.status(transactionResponseData.getLeft()).body(transactionResponseData.getRight());

    }

    @GetMapping("/contract-details")
    @Operation(summary = "Get Smart Contract Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract Details retrieved.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> getSmartContractDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Web3j web3j = null;

        Token token;

        Pair<HttpStatus, TransactionResponse> transactionResponseData = Pair.of(HttpStatus.BAD_REQUEST, new TransactionResponse(null, null));

        try {

            String contractAddress = Web3JUtils.getHeader(httpServletRequest, Constants.CONTRACT_ADDRESS);

            web3j = Web3j.build(new HttpService(Web3JUtils.getHeader(httpServletRequest, Constants.ETHEREUM_NODE_URL)));

            Credentials credentials = Credentials.create(Web3JUtils.getHeader(httpServletRequest, Constants.PRIVATE_KEY));

            ContractGasProvider contractGasProvider = Web3JUtils.getGasProvider(web3j);

            token = Token.load(
                    contractAddress,
                    web3j,
                    credentials,
                    contractGasProvider
            );

            logger.info("Connected to Smart Contract deployed @ {}", token.getContractAddress());

            Map<String, String> contractData = new HashMap<>();
            contractData.put("Name", token.name().send());
            contractData.put("Symbol", token.symbol().send());
            contractData.put("Decimals", String.valueOf(token.decimals().send()));
            contractData.put("Total Supply", String.valueOf(token.totalSupply().send()));

            transactionResponseData = Pair.of(
                    HttpStatus.OK,
                    new TransactionResponse(
                            "Smart Contract details",
                            token.getContractAddress(),
                            contractData
                    )
            );

        } catch (Web3JException e) {
            logger.error("Web3J Exception. Message : {}", e.getMessage());
            transactionResponseData = Pair.of(
                    HttpStatus.UNAUTHORIZED,
                    new TransactionResponse(
                            "Web3J Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } catch (Exception e) {
            logger.error("Error.", e);
            transactionResponseData = Pair.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    new TransactionResponse(
                            "Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }

        return ResponseEntity.status(transactionResponseData.getLeft()).body(transactionResponseData.getRight());

    }

    @GetMapping("/contract-binary")
    @Operation(summary = "Deploy The Smart Contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract Binary Data", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> getSmartContractBinary(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Web3j web3j = null;

        Token token;

        Pair<HttpStatus, TransactionResponse> transactionResponseData = Pair.of(HttpStatus.BAD_REQUEST, new TransactionResponse(null, null));

        try {

            String contractAddress = Web3JUtils.getHeader(httpServletRequest, Constants.CONTRACT_ADDRESS);

            web3j = Web3j.build(new HttpService(Web3JUtils.getHeader(httpServletRequest, Constants.ETHEREUM_NODE_URL)));

            Credentials credentials = Credentials.create(Web3JUtils.getHeader(httpServletRequest, Constants.PRIVATE_KEY));

            ContractGasProvider contractGasProvider = Web3JUtils.getGasProvider(web3j);

            token = Token.load(
                    contractAddress,
                    web3j,
                    credentials,
                    contractGasProvider
            );

            logger.info("Connected to Smart Contract deployed @ {}", token.getContractAddress());

            transactionResponseData = Pair.of(
                    HttpStatus.OK,
                    new TransactionResponse(
                            "Smart Contract Binary Data",
                            token.getContractAddress(),
                            token.getContractBinary()
                    )
            );

        } catch (Web3JException e) {
            logger.error("Web3J Exception. Message : {}", e.getMessage());
            transactionResponseData = Pair.of(
                    HttpStatus.UNAUTHORIZED,
                    new TransactionResponse(
                            "Web3J Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } catch (Exception e) {
            logger.error("Error.", e);
            transactionResponseData = Pair.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    new TransactionResponse(
                            "Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }

        return ResponseEntity.status(transactionResponseData.getLeft()).body(transactionResponseData.getRight());

    }

    @GetMapping("/transaction")
    @Operation(summary = "Validate Transaction status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction details found.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> getTransactionDetails(@RequestParam(required = true, name = "transaction-hash") String transactionHash, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Web3j web3j = null;

        Pair<HttpStatus, TransactionResponse> transactionResponseData = Pair.of(HttpStatus.BAD_REQUEST, new TransactionResponse(null, null));

        try {
            web3j = Web3j.build(new HttpService(Web3JUtils.getHeader(httpServletRequest, Constants.ETHEREUM_NODE_URL)));

            Optional<TransactionReceipt> transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();

            transactionResponseData = Pair.of(
                    HttpStatus.OK,
                    new TransactionResponse(
                            "Transaction request details",
                            transactionHash,
                            transactionReceipt.isPresent() ? transactionReceipt.get() : "No Data Found for Transaction Hash."
                    )
            );

        } catch (Web3JException e) {
            logger.error("Web3J Exception. Message : {}", e.getMessage());
            transactionResponseData = Pair.of(
                    HttpStatus.UNAUTHORIZED,
                    new TransactionResponse(
                            "Web3J Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } catch (Exception e) {
            logger.error("Error.", e);
            transactionResponseData = Pair.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    new TransactionResponse(
                            "Exception. Message : " + e.getMessage(),
                            null
                    )
            );
        } finally {
            if (web3j != null)
                web3j.shutdown();
        }

        return ResponseEntity.status(transactionResponseData.getLeft()).body(transactionResponseData.getRight());

    }


}
