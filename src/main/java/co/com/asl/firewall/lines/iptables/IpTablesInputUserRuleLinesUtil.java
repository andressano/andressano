package co.com.asl.firewall.lines.iptables;

import co.com.asl.firewall.configuration.ufw.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.stream.Stream;

public final class IpTablesInputUserRuleLinesUtil {

  private IpTablesInputUserRuleLinesUtil() {
    super();
  }

  public static final Stream<String> execute(CIDRAddressV4 cidrAddressV4,
      UFWOperation operation) {
    switch (operation) {
      case ACCEPT:
      case DROP:
        return Stream.of(
            String.format("-A fw-user-input -s %s -j %s", cidrAddressV4,
                operation.name().toUpperCase())
        );
      case REJECT:
        return Stream.of(
            String.format("-A fw-user-input -s %s -j %s --reject-with icmp-port-unreachable",
                cidrAddressV4,
                operation.name().toUpperCase())
        );
    }
    return Stream.empty();
  }
}
