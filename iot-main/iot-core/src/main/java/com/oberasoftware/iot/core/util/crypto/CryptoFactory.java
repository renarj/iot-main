package com.oberasoftware.iot.core.util.crypto;

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
