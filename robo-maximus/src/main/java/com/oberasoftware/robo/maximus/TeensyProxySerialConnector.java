package com.oberasoftware.robo.maximus;

import com.oberasoftware.robo.dynamixel.SerialDynamixelConnector;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;
import static org.slf4j.LoggerFactory.getLogger;

@Component
@Primary
public class TeensyProxySerialConnector extends SerialDynamixelConnector {
    private static final Logger LOG = getLogger(TeensyProxySerialConnector.class);

    private static final String DXL_MSG = "{\"command\":\"dynamixel\",\"wait\":\"%s\",\"dxldata\":\"%s\"}";

    public TeensyProxySerialConnector() {
        super();

        baudRate = 57600;
        responseDelayTime = 200;
    }

    @Override
    public void sendNoReceive(byte[] bytes) {
        send(bytes, false);
    }

    @Override
    public synchronized byte[] sendAndReceive(byte[] bytes) {
        return send(bytes, true);
    }

    protected byte[] send(byte[] bytes, boolean wait) {
        LOG.debug("Sending Dynamixel package to Teensy: {}", bb2hex(bytes));

        String msg = String.format(DXL_MSG, Boolean.toString(wait), bb2hex(bytes, false));
        LOG.debug("Sending message to Teensy: {}", msg);
        byte[] recvd = super.send(msg.getBytes(Charset.defaultCharset()), wait);
        if(recvd.length > 0 && wait) {
            LOG.debug("Received: {}", new String(recvd));

            JSONObject jo = new JSONObject(new String(recvd));
            String hexData = jo.getString("feedback");
            if(hexData.length() > 0) {
                String[] hs = hexData.split(" ");
                byte[] converted = new byte[hs.length];
                for (int i = 0; i < hs.length; i++) {
                    String element = hs[i];
                    if (element.trim().length() > 0) {
                        converted[i] = (byte) Integer.parseInt(hs[i], 16);
                    }
                }

                return converted;
            }
        }

        return new byte[]{};
    }
}
