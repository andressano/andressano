package co.com.asl.firewall.resources;

import co.com.asl.firewall.command.Command;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.stream.Collectors;

public final class ASNLineToASNumberTransformer {

  private ASNLineToASNumberTransformer() {
    super();
  }

  public static final synchronized Collection<CIDRAddressV4> transform(
      final String whoisCommandLine, final String asn) {
    return Command.execute(String.format(whoisCommandLine, asn))
        .stream()
        .filter(l -> l.matches(CIDRAddressV4.REGEX_PATTERN))
        .map(CIDRAddressV4::new)
        .collect(Collectors.toList());
  }
}
