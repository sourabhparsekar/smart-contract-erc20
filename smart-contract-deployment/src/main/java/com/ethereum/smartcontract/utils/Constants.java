package com.ethereum.smartcontract.utils;

/**
 * @implSpec COnstants class
 */
public class Constants {

    public static final String ETHEREUM_NODE_URL = "X_NODE_URL";
    public static final String PRIVATE_KEY = "X_PRIVATE_KEY";
    public static final String CONTRACT_ADDRESS = "X_CONTRACT_ADDRESS";


    private Constants() throws InstantiationException {
        throw new InstantiationException(this.getClass().getName() + " cannot be instantiated.");
    }

}
