package com.ethereum.smartcontract.utils;

import com.ethereum.smartcontract.exception.Web3JException;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.servlet.http.HttpServletRequest;

public class Web3JUtils {

    private Web3JUtils() throws InstantiationException {
        throw new InstantiationException(this.getClass().getName() + " cannot be instantiated.");
    }

    /**
     * Method returns Gas Provider based on the Gas Limit and Gas Price
     *
     * @param web3j
     * @return StaticGasProvider
     * @throws java.io.IOException
     */
    @NotNull
    public static ContractGasProvider getGasProvider(Web3j web3j) throws java.io.IOException {
        EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();

        return new StaticGasProvider(
                web3j.ethGasPrice().send().getGasPrice(),
                block.getBlock().getGasLimit()
        );

    }

    @NotNull
    public static String getHeader(HttpServletRequest httpServletRequest, String constant) throws Web3JException {

        String requestHeader = httpServletRequest.getHeader(constant);

        if (requestHeader != null && !requestHeader.isEmpty()) {
            return requestHeader;
        } else {
            throw new Web3JException(constant + " is not available in Header.");
        }
    }

}
