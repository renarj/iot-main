package com.oberasoftware.iot.core.legacymodel;

import java.io.InputStream;

/**
 * @author renarj
 */
public interface ExtensionResource {

    String getMediaType();

    InputStream getStream();
}
