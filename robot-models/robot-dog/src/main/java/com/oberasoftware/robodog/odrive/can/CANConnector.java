package com.oberasoftware.robodog.odrive.can;

import tel.schich.javacan.CanFrame;

public interface CANConnector {
    void open();

    boolean isOpen();

    void send(CanFrame frame);

    CanFrame receive();

    void close();
}
