package co.com.asl.firewall.file;

import co.com.asl.firewall.configuration.UFWOperation;

public enum FileType {
  ASN_FILETYPE("classpath*:META-INF/firewall/%s/%s/ASNumbers.txt", ASNFileReader.class),

  IP_FILETYPE("classpath*:META-INF/firewall/%s/%s/IPs.txt", IPFileReader.class);

  private final String pattern;
  private final Class<? extends HostsListFileResourceReader> resourceReader;

  FileType(String pattern, Class<? extends HostsListFileResourceReader> resourceReader) {
    this.pattern = pattern;
    this.resourceReader = resourceReader;
  }

  public String path(String profile, UFWOperation ufwOperation) {
    return String.format(pattern, profile, ufwOperation.name().toLowerCase());
  }

  public String getPattern() {
    return pattern;
  }

  public Class<? extends HostsListFileResourceReader> getResourceReader() {
    return resourceReader;
  }
}
