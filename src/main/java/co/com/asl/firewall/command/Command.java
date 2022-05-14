package co.com.asl.firewall.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Command {

  private static final Logger log = LoggerFactory.getLogger(Command.class);

  private Command() {
    super();
  }

  private static Collection<String> read(InputStream is) {
    Scanner scanner = new Scanner(is);
    List<String> response = new ArrayList<>();
    while (scanner.hasNext()) {
      response.add(scanner.next());
    }
    scanner.close();
    return response;
  }

  public static Stream<String> execute(final String commandLine) {
    try {
      String[] commandSyntax = new String[]{"/bin/bash", "-c",
          "timeout 20s ".concat(commandLine)};
      Process process = Runtime.getRuntime().exec(commandSyntax);
      Collection<String> response = read(process.getInputStream());
      if (log.isDebugEnabled()) {
        log.debug("Executing {} with output {}", commandLine, response);
      }
      return response.stream();
    } catch (Exception e) {
      log.error("Error running {}", commandLine, e);
    }
    return Stream.empty();
  }

}
