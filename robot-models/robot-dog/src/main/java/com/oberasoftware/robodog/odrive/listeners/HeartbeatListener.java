package com.oberasoftware.robodog.odrive.listeners;

import com.oberasoftware.robodog.odrive.ODriveCommands;
import com.oberasoftware.robodog.odrive.ODriveErrorDecoder;
import com.oberasoftware.robodog.odrive.ODriveEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.CanFrame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.oberasoftware.robodog.odrive.can.ODriveCANFrameUtil.bb2hex;

public class HeartbeatListener implements ODriveEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatListener.class);

    @Override
    public ODriveCommands getCommandId() {
        return ODriveCommands.HEARTBEAT;
    }

    @Override
    public void onEvent(int nodeId, int cmdId, CanFrame frame) {
        LOG.debug("Received heartbeat command from node: {}", nodeId);
        var b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        frame.getData(b);

        var b1 = b.get(0);
        var b2 = b.get(1);
        var b3 = b.get(2);
        var b4 = b.get(3);
        var b5 = b.get(4);
        var b6 = b.get(5);
        var b7 = b.get(6);
        int err = ByteBuffer.wrap(new byte[]{b4, b3, b2, b1}).getInt();
        LOG.debug("Heartbeat Data received: {} errors: {}", bb2hex(new byte[]{b1, b2, b3, b4, b5, b6, b7}), ODriveErrorDecoder.printErrors(err));

        int state = b5;
        int result = b6;
        int done = b7;
        LOG.debug("HB  err=0x{} state={} result={} done={}", err, state, result, done);

        if(err != 0x0) {
            LOG.warn("Heartbeat or drive: {} bytes: {} error: {}", bb2hex(new byte[]{b4, b3, b2, b1}), nodeId, ODriveErrorDecoder.printErrors(err));
        }
    }
}
