package com.ethereum.smartcontract.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Web3JUtilsTest {

    @Test
    void test_Constructor_Is_Private() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Constructor<Web3JUtils> constructor = Web3JUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException invocationTargetException) {
            Assertions.assertEquals(new IllegalStateException("com.ethereum.smartcontract.utils.Web3JUtils cannot be instantiated.").getMessage(), invocationTargetException.getCause().getMessage());
        }
    }

}