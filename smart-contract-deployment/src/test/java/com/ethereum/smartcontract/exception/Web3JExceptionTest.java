package com.ethereum.smartcontract.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class Web3JExceptionTest {

    @Test
    void test_Exception() {
        String exceptionMessage = "Exception Message";
        try {
            throw new Web3JException(exceptionMessage);
        } catch (Web3JException e) {
            Assertions.assertEquals(exceptionMessage, e.getMessage());
        } catch (Exception e) {
            fail("Web3J exception not thrown.");
        }


    }


}