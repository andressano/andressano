package co.com.asl.firewall.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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
      List<String> response = new ArrayList<>();
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
