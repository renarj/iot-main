package com.oberasoftware.iot.tools;

import com.oberasoftware.iot.client.ClientConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SpringApplicationAdminJmxAutoConfiguration.class,
        DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Import(ClientConfiguration.class)
@CommandScan
@EnableCommand({BackupCommand.class, RestoreCommand.class})
public class CommandLineClient {
    public static void main(String[] args) {

        new SpringApplicationBuilder(CommandLineClient.class).web(WebApplicationType.NONE).run(args);
    }

}
