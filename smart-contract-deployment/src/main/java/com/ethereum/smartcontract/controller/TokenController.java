package com.ethereum.smartcontract.controller;

import com.ethereum.smartcontract.exception.Web3JException;
import com.ethereum.smartcontract.model.TransactionResponse;
import com.ethereum.smartcontract.model.TransferToken;
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

@RestController
public class TokenController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @GetMapping("/balance")
    @Operation(summary = "Get balance of address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract balance details retrieved.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> getBalanceOf(@RequestParam(required = true, name = "wallet-address") String walletAddress,
                                                            HttpServletRequest httpServletRequest,
                                                            HttpServletResponse httpServletResponse) {

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
                            "Smart Contract balance",
                            token.getContractAddress(),
                            Pair.of(walletAddress, token.balanceOf(walletAddress).send())
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

    @PostMapping("/transfer")
    @Operation(summary = "Transfer balance to address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Smart Contract Transfer was successful.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Private Key/Node URL is not available", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema(hidden = true))})
    })
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferToken transferToken,
                                                        HttpServletRequest httpServletRequest,
                                                        HttpServletResponse httpServletResponse) {

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

            logger.info("Connected to Smart Contract deployed @ {}", contractAddress);

            BigInteger balanceReceiver = token.balanceOf(transferToken.getWalletAddress()).send();
            BigInteger balanceSender = token.balanceOf(credentials.getAddress()).send();

            TransactionReceipt transactionReceipt = token.transfer(transferToken.getWalletAddress(),
                    transferToken.getAmount()
            ).send();

            Map<String, String> transferData = new HashMap<>();
            transferData.put("Block Hash", transactionReceipt.getBlockHash());
            transferData.put("Block Number", String.valueOf(transactionReceipt.getBlockNumber()));
            transferData.put("Transaction Hash", transactionReceipt.getTransactionHash());
            transferData.put("Gas Used", String.valueOf(transactionReceipt.getGasUsed()));
            transferData.put("Status", transactionReceipt.getStatus());
            transferData.put("Revert Reason if any", transactionReceipt.getRevertReason());
            transferData.put("New Receiver Balance", String.valueOf(token.balanceOf(transferToken.getWalletAddress()).send()));
            transferData.put("Old Receiver Balance", String.valueOf(balanceReceiver));
            transferData.put("New Sender Balance", String.valueOf(token.balanceOf(credentials.getAddress()).send()));
            transferData.put("Old Sender Balance", String.valueOf(balanceSender));
            transferData.put("Sender Address", credentials.getAddress());
            transferData.put("Receiver Address", transferToken.getWalletAddress());

            transactionResponseData = Pair.of(
                    HttpStatus.OK,
                    new TransactionResponse(
                            "Smart Contract Transfer",
                            token.getContractAddress(),
                            transferData
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
