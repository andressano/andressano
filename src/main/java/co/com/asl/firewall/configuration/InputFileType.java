package co.com.asl.firewall.configuration;

import co.com.asl.firewall.configuration.ufw.UFWOperation;

public enum InputFileType {
  ASN_FILETYPE("classpath*:META-INF/firewall/%s/%s/ASNumbers.txt"),

  IP_FILETYPE("classpath*:META-INF/firewall/%s/%s/IPs.txt");

  private final String pattern;

  InputFileType(String pattern) {
    this.pattern = pattern;
  }

  public String path(String profile, UFWOperation ufwOperation) {
    return String.format(pattern, profile, ufwOperation.name().toLowerCase());
  }

  public String getPattern() {
    return pattern;
  }
}
