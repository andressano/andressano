package co.com.asl.firewall.lines.ufw;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.stream.Stream;

public final class UfwUserRuleLinesUtil {

  private UfwUserRuleLinesUtil() {
    super();
  }

  public static final Stream<String> execute(CIDRAddressV4 cidrAddressV4, FWOperation operation) {
    return Stream.of(
        "",
        String.format("### tuple ### %s any any 0.0.0.0/0 any %s in",
            operation.policy().toLowerCase(), cidrAddressV4.toString()),
        String.format("-A ufw-user-input -s %s -j %s", cidrAddressV4,
            operation.name().toUpperCase()),
        "",
        String.format("### tuple ### %s any any %s any 0.0.0.0/0 out",
            operation.policy().toLowerCase(), cidrAddressV4),
        String.format("-A ufw-user-output -d %s -j %s", cidrAddressV4,
            operation.name().toUpperCase()));
  }
}
