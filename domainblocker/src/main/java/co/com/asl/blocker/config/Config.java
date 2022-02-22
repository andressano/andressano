package co.com.asl.blocker.config;

import co.com.asl.blocker.runtime.CommandRunner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("co.com.asl.blocker")
public class Config {

  @Bean
  public String osName(CommandRunner commandRunner) {
    return StringUtils.lowerCase(System.getProperty("os.name"));
  }

  @Bean
  public String hostName(CommandRunner commandRunner) {
    return commandRunner.execute("cat /etc/hostname");
  }
}