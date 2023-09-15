package co.com.asl.firewall.entities.transform;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

final class CIDRAbsorber {

  private static final Logger log = LoggerFactory.getLogger(CIDRAbsorber.class);

  private CIDRAbsorber() {
  }

  static void doAbsorb(CIDRTransformableSet addresses, CIDRAddressV4 cidr,
      Set<CIDRAddressV4> removeSet) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }
    addresses.stream()
        .filter(c -> c.getMask() > cidr.getMask())
        .filter(c -> !removeSet.contains(c))
        .filter(cidr::canAbsorb)
        .forEach(removeSet::add);
  }

  public static void absorb(CIDRTransformableSet cidrs) {
    Set<CIDRAddressV4> removeSet = new HashSet<>();
    for (CIDRAddressV4 cidr : cidrs) {
      doAbsorb(cidrs, cidr, removeSet);
    }
    cidrs.removeAll(removeSet);
    if (log.isInfoEnabled() && !removeSet.isEmpty()) {
      log.info("[{}] Removing {} addresses by absorption", cidrs.getName(), removeSet.size());
    }
  }
}
