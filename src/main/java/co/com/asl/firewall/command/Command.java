package co.com.asl.firewall.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public final class Command {

  private Command() {
    super();
  }

  public static final Collection<String> execute(final String commandLine) {
    try {
      String[] commandSyntax = new String[]{"/bin/bash", "-c",
          "timeout 10s ".concat(commandLine)};
      Process process = Runtime.getRuntime().exec(commandSyntax);
      Scanner scanner = new Scanner(process.getInputStream());
      Set<String> response = new TreeSet<>();
      while (scanner.hasNext()) {
        response.add(scanner.next());
      }
      scanner.close();
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

}
