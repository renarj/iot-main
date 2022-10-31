package com.oberasoftware.home.hue;

import java.net.URI;

public class UriUtils {
    public static URI createUri(HueBridge bridge, String resourcePath) {
        return URI.create("https://" + bridge.getBridgeIp() + "/api/" + bridge.getBridgeToken() + "/" + resourcePath);
    }
}
