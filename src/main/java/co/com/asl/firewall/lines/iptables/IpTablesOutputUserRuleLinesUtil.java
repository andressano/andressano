package co.com.asl.firewall.lines.iptables;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.stream.Stream;

public final class IpTablesOutputUserRuleLinesUtil {

  private IpTablesOutputUserRuleLinesUtil() {
    super();
  }

  public static final Stream<String> execute(CIDRAddressV4 cidrAddressV4,
      FWOperation operation) {
    switch (operation) {
      case ACCEPT:
      case DROP:
        return Stream.of(
            String.format("-A fw-user-output -d %s -j %s", cidrAddressV4,
                operation.name().toUpperCase())
        );
      case REJECT:
        return Stream.of(
            String.format("-A fw-user-output -d %s -j %s --reject-with icmp-port-unreachable",
                cidrAddressV4,
                operation.name().toUpperCase())
        );
    }
    return Stream.empty();
  }
}
