package com.ethereum.smartcontract.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstantsTest {

    @Test
    void test_Constructor_Is_Private() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException invocationTargetException) {
            Assertions.assertEquals(new IllegalStateException("com.ethereum.smartcontract.utils.Constants cannot be instantiated.").getMessage(), invocationTargetException.getCause().getMessage());
        }
    }
}
