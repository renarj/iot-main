package com.oberasoftware.iot.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BackupRestoreCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(BackupRestoreCoordinator.class);
    private static final String PLUGIN_BACKUP_JSON = "plugin-backup.json";

    @Autowired
    private ConfigBackupRunner configBackupRunner;

    @Autowired
    private ConfigRestoreRunner configRestoreRunner;

    private File targetDirectory;
    private String targetHost;
    private String targetPort;

    public void runRestore(String targetDir, String targetHost, String targetPort) {
        checkDirectory(targetDir);
        this.targetHost = targetHost;
        this.targetPort = targetPort;

        runPluginRestore();
        runSchemaRestore();
    }

    public void runBackup(String targetDir, String targetHost, String targetPort) {
        checkDirectory(targetDir);
        this.targetHost = targetHost;
        this.targetPort = targetPort;

        runPluginBackup();
        runSchemaBackup();
    }

    private void checkDirectory(String targetDir) {
        if(targetDir != null) {
            this.targetDirectory = new File(targetDir);
            if(!this.targetDirectory.exists()) {
                this.targetDirectory.mkdirs();
            }
            if(!this.targetDirectory.isDirectory()) {
                LOG.error("Target directory is not a directory: {}", targetDir);
                System.exit(-1);
            }
        }
    }

    private void runPluginRestore() {
        File pluginBackup = new File(targetDirectory, PLUGIN_BACKUP_JSON);
        configRestoreRunner.restorePlugins(new RemoteConfig(targetHost, targetPort, pluginBackup.toString()));
    }

    private void runSchemaRestore() {
        File schemaBackup = new File(targetDirectory, "schema-backup.json");
        configRestoreRunner.restoreSchemas(new RemoteConfig(targetHost, targetPort, schemaBackup.toString()));
    }

    private void runPluginBackup() {
        File pluginBackup = new File(targetDirectory, PLUGIN_BACKUP_JSON);
        LOG.info("Running Plugin backup on target: {}:{} and storing in file: {}", targetHost, targetPort, pluginBackup);
        configBackupRunner.backupPlugins(new RemoteConfig(targetHost, targetPort, pluginBackup.toString()));
    }

    private void runSchemaBackup() {
        File schemaBackup = new File(targetDirectory, "schema-backup.json");
        LOG.info("Running Schemas backup on target: {}:{} and storing in file: {}", targetHost, targetPort, schemaBackup);
        configBackupRunner.backupSchemas(new RemoteConfig(targetHost, targetPort, schemaBackup.toString()));

    }
}
