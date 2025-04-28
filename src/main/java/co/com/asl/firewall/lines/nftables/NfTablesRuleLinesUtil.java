package co.com.asl.firewall.lines.nftables;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NfTablesRuleLinesUtil {

  public static Stream<String> execute(CIDRAddressV4 cidrAddressV4,
      FWOperation operation) {
    switch (operation) {
      case ACCEPT:
      case DROP:
      case REJECT:
        return Stream.of(
            String.format("add rule ip filter user-output ip daddr %s counter %s",
                cidrAddressV4,
                operation.name().toLowerCase())
        );
    }
    return Stream.empty();
  }
}
