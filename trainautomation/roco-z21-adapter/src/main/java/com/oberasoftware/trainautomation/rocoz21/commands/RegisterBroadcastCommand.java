package com.oberasoftware.trainautomation.rocoz21.commands;

import com.google.common.collect.Sets;

import java.util.Set;

public class RegisterBroadcastCommand extends Z21Command {

    private static final int BROADCAST_SET = 0x50;


    public RegisterBroadcastCommand(Set<BroadcastCommand> commandSet) {
        addHeader(BROADCAST_SET);

        int flags = 0;
        for (BroadcastCommand b : commandSet) {
            flags = flags | b.getFlag();
        }
        addSingleByte(flags & 0x000000ff);
        addSingleByte((flags & 0x0000ff00)>>8);
        addSingleByte((flags & 0x00ff0000)>>16 );
        addSingleByte((flags & 0xff000000)>>24 );
    }

    public RegisterBroadcastCommand(BroadcastCommand command) {
        this(Sets.newHashSet(command));
    }
}
