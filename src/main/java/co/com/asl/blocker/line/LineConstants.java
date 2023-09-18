package co.com.asl.blocker.line;

public class LineConstants {

  private static final String IP_NUMBER_PATTERN = "(25[0-5]|2[0-4][0-9]|1([0-9]{2})|[1-9][0-9]|[0-9])";
  public static final String IP_REGEX_PATTERN =
      "((" + IP_NUMBER_PATTERN + "\\.){3})" + IP_NUMBER_PATTERN;
  public static final String HOSTNAME_PATTERN = "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])";
  public static final String COMMENT_PATTERN = "#(.)*";
  public static final String ROUTE_IP = "0.0.0.0";
  public static final String COMPUTER_NAME = "%%COMPUTER_NAME%%";
  public static final String GENERATED_HOSTS = "# Generated hosts";

  private LineConstants() {
    super();
  }
}
