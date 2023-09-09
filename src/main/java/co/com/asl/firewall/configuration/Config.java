package co.com.asl.firewall.configuration;

import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan("co.com.asl.firewall")
public class Config {

  @Bean
  public Collection<String> whoisQueries(
      FileToLinesResourceLoader fileToLinesResourceLoader,
      @Value("classpath:/META-INF/whois/queries.txt") Resource whoisResource) {
    return fileToLinesResourceLoader
        .load(whoisResource)
        .collect(Collectors.toList());
  }
}
