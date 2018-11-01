package com.carpooling.service;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Security {
    public static String encryptPassword(String inputPassword) {
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm("SHA-256");
        passwordEncryptor.setPlainDigest(true);
        String encryptedPassword = passwordEncryptor.encryptPassword(inputPassword);
        return encryptedPassword;
    }

    public static Boolean doPasswordsMatch(String inputPassword, String encryptedPassword) {
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm("SHA-256");
        passwordEncryptor.setPlainDigest(true);
        return passwordEncryptor.checkPassword(inputPassword, encryptedPassword);
    }

    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}