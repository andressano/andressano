package co.com.asl.blocker.line;

import java.util.stream.Stream;

public class LineConstants {

  public static final String IP_REGEX_PATTERN = "((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))";
  public static final String HOSTNAME_PATTERN = "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])";
  public static final String COMMENT_PATTERN = "#(.)*";
  public static final String ROUTE_IP = "0.0.0.0";
  public static final String COMPUTER_NAME = "%%COMPUTER_NAME%%";
  public static final String GENERATED_HOSTS = "# Generated hosts";

  private LineConstants() {
    super();
  }
}
