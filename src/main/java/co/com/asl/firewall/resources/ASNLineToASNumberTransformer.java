package co.com.asl.firewall.resources;

import co.com.asl.firewall.command.Command;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.ip.CIDRPredicate;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

public final class ASNLineToASNumberTransformer {

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
        .filter(CIDRPredicate.IS_CIDR_PREDICATE)
        .map(CIDRAddressV4::new)
        .collect(Collectors.toList());
  }
}
