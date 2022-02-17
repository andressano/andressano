package co.com.asl.blocker.config;

import co.com.asl.blocker.file.WhitelistLoader;
import co.com.asl.blocker.runtime.CommandRunner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
@ComponentScan("co.com.asl.blocker")
public class Config {
;
  @Autowired
  private CommandRunner commandRunner;
  @Autowired
  private WhitelistLoader whitelistLoader;

  @Bean
  public String osName() {
    return StringUtils.lowerCase(System.getProperty("os.name"));
  }

  @Bean
  public String hostName() {
    return commandRunner.execute("cat /etc/hostname");
  }

  @Bean
  public Collection<String> whitelist() throws IOException {
    return whitelistLoader.loadHostsLines().collect(Collectors.toList());
  }
}