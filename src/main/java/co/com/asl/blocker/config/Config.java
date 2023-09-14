package co.com.asl.blocker.config;

import co.com.asl.blocker.runtime.CommandRunner;
import java.rmi.Remote;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("co.com.asl.blocker")
public class Config {

  @Bean
  public String hostFilePath() {
    final String osName = StringUtils.lowerCase(System.getProperty("os.name"));
    return String.format("classpath:/META-INF/hostsFiles/%s.txt", osName);
  }

  @Bean
  public String hostName(CommandRunner commandRunner) {
    return commandRunner.execute("cat /etc/hostname").findFirst().orElse("");
  }

  @Bean
  public String sitesClasspath() {
    return "classpath:/META-INF/sites.txt";
  }

  @Bean
  public String allowListClasspath() {
    return "classpath:/META-INF/allow-list/*.txt";
  }

  @Bean
  public String denyListClasspath() {
    return "classpath:/META-INF/deny-list/*.txt";
  }

  @Bean
  public URLFactory urlFactory() {
    return new RemoteURLFactory();
  }
}