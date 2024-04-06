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

  private static Stream<String> read(InputStream is, final String commandLine) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(is)) {
      String output = IOUtils.toString(isr).replaceAll("\\s+", ",");
      String[] results = StringUtils.tokenizeToStringArray(output, ",");
      if(results.length != 0 && output.isBlank())
        log.warn("Result mismatches using {}", commandLine);
      return Stream.of(results);
    }
  }

  public static Stream<String> execute(final String commandLine) {
    try {
      String[] commandSyntax = new String[]{"/bin/bash", "-c",
          "timeout 20s ".concat(commandLine)};
      Process process = Runtime.getRuntime().exec(commandSyntax);
      return read(process.getInputStream(), commandLine);
    } catch (Exception e) {
      log.error("Error running {}", commandLine, e);
    }
    return Stream.empty();
  }

}
