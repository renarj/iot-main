package com.oberasoftware.iot.core.util.crypto;

/**
 * @author Renze de Vries
 */
public interface CryptoEngine {
    String getDescriptor();

    String encrypt(String salt, String password, String text) throws java.lang.SecurityException;

    String decrypt(String salt, String password, String encrypted) throws java.lang.SecurityException;

    String generateSalt() throws java.lang.SecurityException;

    String hash(String salt, String password) throws SecurityException;
}
