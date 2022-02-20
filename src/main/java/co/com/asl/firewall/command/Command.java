package co.com.asl.firewall.command;

import java.io.IOException;
import java.util.ArrayList;
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

  public static final Stream<String> execute(final String commandLine) {
    try {
      String[] commandSyntax = new String[]{"/bin/bash", "-c",
          "timeout 10s ".concat(commandLine)};
      Process process = Runtime.getRuntime().exec(commandSyntax);
      Scanner scanner = new Scanner(process.getInputStream());
      List<String> response = new ArrayList<>();
      while (scanner.hasNext()) {
        response.add(scanner.next());
      }
      scanner.close();
      if (log.isDebugEnabled()) {
        log.debug("Executing {} with output {}", commandLine, response);
      }
      return response.stream();
    } catch (IOException e) {
      log.error("Error running {}", commandLine, e);
    }
    return Stream.empty();
  }

}
