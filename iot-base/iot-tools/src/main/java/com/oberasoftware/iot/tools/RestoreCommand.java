package com.oberasoftware.iot.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;

@Command
public class RestoreCommand {

    @Autowired
    private BackupRestoreCoordinator backupRestoreCoordinator;

    @Bean
    public CommandRegistration restoreCommand() {
        return CommandRegistration.builder()
                .command("restore")
                .description("Restore configuration")
                .withOption()
                    .description("Target Restore Directory")
                    .longNames("targetDir")
                    .position(0)
                    .and()
                .withOption()
                    .description("Target server IP Address")
                    .longNames("targetServer")
                    .defaultValue("127.0.0.1")
                    .required(false)
                    .and()
                .withOption()
                    .description("Target server port")
                    .longNames("targetPort")
                    .defaultValue("9010")
                    .required(false)
                    .and()
                .withTarget().function(f -> {
                    String targetDir = f.getOptionValue("targetDir");
                    String targetServer = f.getOptionValue("targetServer");
                    String targetPort = f.getOptionValue("targetPort");

                    backupRestoreCoordinator.runRestore(targetDir, targetServer, targetPort);
                    return "Restore complete";
                }).and()
                .build();
    }
}
