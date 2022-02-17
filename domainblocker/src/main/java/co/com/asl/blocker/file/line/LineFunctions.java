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

  public static String removeComments(String line) {
    return line.replaceFirst("#(.)*", "").trim();
  }

  public static String removeIp(String line) {
    return line.replaceFirst(LineConstants.IP_REGEX_PATTERN.concat("\\s+"), "");
  }
}
