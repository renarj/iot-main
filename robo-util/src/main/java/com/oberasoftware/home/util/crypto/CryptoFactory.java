package com.oberasoftware.home.util.crypto;

import com.oberasoftware.robo.api.exceptions.SecurityException;

/**
 * @author Renze de Vries
 */
public class CryptoFactory {
    public static CryptoEngine getEngine() {
        return new BasicCryptoEngine();
    }

    public static CryptoEngine getEngine(String engineDescriptor) throws SecurityException {
        return new BasicCryptoEngine();
    }
}
