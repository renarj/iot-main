package com.oberasoftware.iot.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BackupRestoreCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(BackupRestoreCoordinator.class);
    private static final String PLUGIN_BACKUP_JSON = "plugin-backup.json";

    @Autowired
    private BackupRunner backupRunner;

    @Autowired
    private ConfigRestoreRunner configRestoreRunner;

    private Map<String, Action> backupActions = new HashMap<>();
    private Map<String, Action> restoreActions = new HashMap<>();

    private File targetDirectory;
    private String targetHost;
    private String targetPort;

    public BackupRestoreCoordinator() {
        backupActions.put("plugin-backup.json", config -> backupRunner.backupPlugins(config));
        backupActions.put("schema-backup.json", config -> backupRunner.backupSchemas(config));
        backupActions.put("things-backup.json", config -> backupRunner.backupThings(config));
        backupActions.put("controllers-backup.json", config -> backupRunner.backupControllers(config));

        restoreActions.put("plugin-backup.json", config -> configRestoreRunner.restorePlugins(config));
        restoreActions.put("schema-backup.json", config -> configRestoreRunner.restoreSchemas(config));
        restoreActions.put("controllers-backup.json", config -> configRestoreRunner.restoreControllers(config));
        restoreActions.put("things-backup.json", config -> configRestoreRunner.restoreThings(config));
    }

    public boolean runRestore(String targetDir, String targetHost, String targetPort) {
        checkDirectory(targetDir);
        this.targetHost = targetHost;
        this.targetPort = targetPort;

        var r = runActions("Restore", restoreActions);
        LOG.info("Restore process completed: {}", r);
        return r;
    }

    public boolean runBackup(String targetDir, String targetHost, String targetPort) {
        checkDirectory(targetDir);
        this.targetHost = targetHost;
        this.targetPort = targetPort;

        var r = runActions("Backup", backupActions);
        LOG.info("Backup process completed: {}", r);
        return r;
    }

    private boolean runActions(String processType, Map<String, Action> actions) {
        AtomicBoolean failure = new AtomicBoolean(true);
        actions.forEach((k, v) -> {
            if(failure.get()) {
                LOG.info("Starting {} for file: {}", processType, k);
                var c = new RemoteConfig(targetHost, targetPort, new File(targetDirectory, k).toString());
                if(!v.run(c)) {
                    failure.set(false);
                }
            }
        });
        return failure.get();
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

    private interface Action {
        boolean run(RemoteConfig config);
    }
}
