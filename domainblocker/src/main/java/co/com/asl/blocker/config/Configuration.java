package co.com.asl.blocker.config;

import co.com.asl.blocker.runtime.CommandRunner;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean(name = "localhostName")
    public String localhostName(CommandRunner commandRunner) {
        return commandRunner.execute("cat /etc/hostname");
    }

}