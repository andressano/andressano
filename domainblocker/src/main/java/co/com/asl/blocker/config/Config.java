package co.com.asl.blocker.config;

import co.com.asl.blocker.file.line.LineFunctions;
import co.com.asl.blocker.runtime.CommandRunner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@ComponentScan("co.com.asl.blocker")
public class Config {

  public static final String BLACKLIST_BEAN = "blacklist";
  public static final String WHITELIST_BEAN = "whitelist";
  @Autowired
  private ResourceLoader resourceLoader;

  private Collection<String> loadMetaInfFile(ResourceLoader resourceLoader, String file)
      throws IOException {
    try (Stream<String> stream = Files.lines(Path.of(
        resourceLoader
            .getResource("classpath:/META-INF".concat(file))
            .getURI()))) {
      return stream
          .map(LineFunctions.replaceComments())
          .filter(StringUtils::isNotBlank)
          .collect(Collectors.toList());
    }
  }

  @Bean
  public String preludeLines(ResourceLoader resourceLoader, CommandRunner commandRunner)
      throws IOException {
    String os = StringUtils.lowerCase(System.getProperty("os.name"));
    String hostName = commandRunner.execute("cat /etc/hostname");
    return Files.readString(
            Path.of(
                resourceLoader
                    .getResource(String.format("classpath:/META-INF/hostsFiles/%s.txt", os))
                    .getURI()))
        .replace("%%COMPUTER_NAME%%", hostName);
  }

  @Bean
  public Collection<String> blacklist(ResourceLoader resourceLoader) throws IOException {
    return loadMetaInfFile(resourceLoader, "/antiads/blacklist.txt");
  }

  @Bean
  public Collection<String> whitelist(ResourceLoader resourceLoader) throws IOException {
    return loadMetaInfFile(resourceLoader, "/antiads/whitelist.txt");
  }
}