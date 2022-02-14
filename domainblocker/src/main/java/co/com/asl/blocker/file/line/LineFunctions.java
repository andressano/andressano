package co.com.asl.blocker.file.line;

import java.util.function.Function;

public class LineFunctions {

  private static final String COMPUTER_NAME = "%%COMPUTER_NAME%%";

  private LineFunctions() {
  }

  public static Function<String, String> replaceTextConsumer(String text, String replacement) {
    return c -> c.replace(text, replacement);
  }

  public static Function<String, String> replaceHostName(String hostName) {
    return replaceTextConsumer(COMPUTER_NAME, hostName);
  }

  public static Function<String, String> replaceComments() {
    return l -> l.replaceFirst("#(.)*", "").trim();
  }

  public static Function<String, String> removeIp() {
    return l -> l.replaceFirst(LineConstants.IP_REGEX_PATTERN, "");
  }
}
