package co.com.asl.blocker.line;

import org.apache.commons.lang3.StringUtils;

public class LineFunctions {

  private LineFunctions() {
  }

  public static String removeComments(String line) {
    return line.replaceFirst(LineConstants.COMMENT_PATTERN, "").trim();
  }

  public static String removeIp(String line) {
    return line.replaceFirst(LineConstants.IP_REGEX_PATTERN.concat("\\s+"), "");
  }

  public static boolean isValidLine(String line) {
    return StringUtils.isNotBlank(line) && line.matches(LineConstants.HOSTNAME_PATTERN);
  }
  public static String formatLine(String host) {
    return String.format("%s\t%s", LineConstants.ROUTE_IP, host);
  }
}
