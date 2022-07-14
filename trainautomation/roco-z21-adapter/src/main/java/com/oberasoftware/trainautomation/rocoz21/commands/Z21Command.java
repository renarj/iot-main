package com.oberasoftware.trainautomation.rocoz21.commands;

import com.oberasoftware.robo.core.ConverterUtil;
import org.slf4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class Z21Command {
    private static final Logger LOG = getLogger( Z21Command.class );
    private static final int FIXED_PARAM_LENGTH = 2;

    private List<Byte> parameters = new ArrayList<>();

    private String host;
    private int port;

    public DatagramPacket build() throws RuntimeHomeAutomationException {
        ByteBuffer buffer = ByteBuffer.allocate(parameters.size() + FIXED_PARAM_LENGTH);

        int length = FIXED_PARAM_LENGTH + parameters.size();
        byte[] lenghtBytes = ConverterUtil.intTo16BitByte(length);
        buffer.put(lenghtBytes[0]);
        buffer.put(lenghtBytes[1]);

        parameters.forEach(buffer::put);

        var data = buffer.array();
        LOG.info("Sending package: {}", bytesToHex(data));

        var dg = new DatagramPacket(data, data.length);
        try {
            dg.setAddress(InetAddress.getByName(host));
            dg.setPort(port);

            return dg;
        } catch(UnknownHostException e) {
            LOG.error("Could not send to host: {}", host);
            throw new RuntimeHomeAutomationException("Could not send to host: " + host, e);
        }
    }

    public Z21Command addHost(String host) {
        this.host = host;
        return this;
    }

    public Z21Command addPort(int port) {
        this.port = port;
        return this;
    }

    protected void addHeader(int header) {
        add(header);
    }

    protected void addXHeader(int xHeader, int DB0) {
        this.parameters.add((byte)xHeader);
        this.parameters.add((byte)DB0);
        this.parameters.add((byte)(xHeader ^ DB0));
    }

    protected void add(int param) {
        byte[] a = ConverterUtil.intTo16BitByte(param);
        this.parameters.add(a[0]);
        this.parameters.add(a[1]);
    }

    protected void addSingleByte(int param) {
        this.parameters.add((byte)param);
    }

    public static String bytesToHex(byte[] buffer) {
        return bytesToHex(buffer, true);
    }

    public static String bytesToHex(byte[] buffer, boolean formatSpaced) {
        StringBuilder result = new StringBuilder();
        for (byte b : buffer) {
            result.append(String.format("%02X", b));
            if(formatSpaced) {
                result.append(" ");
            }
        }
        return result.toString().trim();
    }

    public static String bytesToHex(List<Byte> buffer) {
        return buffer.stream().map(b -> String.format("%02X", b))
                .collect(Collectors.joining(" "));
    }

}
