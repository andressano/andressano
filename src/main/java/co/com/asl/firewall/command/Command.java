package co.com.asl.firewall.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Command {

  private static final Logger log = LoggerFactory.getLogger(Command.class);

  private static Stream<String> read(InputStream is) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(is)) {
      String output = IOUtils.toString(isr).replaceAll("\\s+", ",");
      return Stream.of(StringUtils.tokenizeToStringArray(output, ","));
    }
  }

  public static Stream<String> execute(final String commandLine) {
    try {
      String[] commandSyntax = new String[]{"/bin/bash", "-c",
          "timeout 20s ".concat(commandLine)};
      Process process = Runtime.getRuntime().exec(commandSyntax);
      return read(process.getInputStream());
    } catch (Exception e) {
      log.error("Error running {}", commandLine, e);
    }
    return Stream.empty();
  }

}
