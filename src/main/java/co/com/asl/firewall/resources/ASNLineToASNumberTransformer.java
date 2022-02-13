package co.com.asl.firewall.resources;

import co.com.asl.firewall.command.Command;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

public final class ASNLineToASNumberTransformer {

  private static final String IP_NUMBER_PATTERN = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
  private static final String CIDR_PATTERN = "^(" + IP_NUMBER_PATTERN
      + "\\.){3}" + IP_NUMBER_PATTERN + "(\\/([0-9]|[1-2][0-9]|3[0-2]))$";

  private ASNLineToASNumberTransformer() {
    super();
  }

  public static final synchronized Collection<CIDRAddressV4> transform(
      final String whoisCommandLine, final String asn) {
    Collection<String> result = Command.execute(String.format(whoisCommandLine, asn));

    if (CollectionUtils.isEmpty(result)) {
      return Collections.emptyList();
    }

    return result
        .stream()
        .filter(l -> l.matches(CIDR_PATTERN))
        .map(CIDRAddressV4::new)
        .collect(Collectors.toList());
  }
}
