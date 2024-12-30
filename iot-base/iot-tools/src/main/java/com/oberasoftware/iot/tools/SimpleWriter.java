package com.oberasoftware.iot.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class SimpleWriter {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleWriter.class);

    private final String targetFile;

    private FileChannel channel;
    private RandomAccessFile randomAccess;

    public SimpleWriter(String targetFile) {
        this.targetFile = targetFile;
    }

    public void open() throws IOException {
        LOG.info("Opening file access to file: {}", targetFile);
        this.randomAccess = new RandomAccessFile(targetFile, "rw");
        this.randomAccess.setLength(0);
        this.channel = randomAccess.getChannel();
    }

    public void close() throws IOException {
        LOG.info("Closing file access to file: {}", targetFile);
        this.channel.close();
        this.randomAccess.close();
    }

    public boolean write(String content) throws IOException {
        LOG.debug("Writing contents: {} to file: {}", content, targetFile);
        byte[] b = content.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(b);

        return this.channel.write(byteBuffer) > 0;
    }
}
