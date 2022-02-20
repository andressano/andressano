package co.com.asl.firewall.entities.transform;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

final class CIDRAbsorber {

  private static final Logger log = LoggerFactory.getLogger(CIDRAbsorber.class);
  private static final boolean DEBUG_ENABLED = log.isDebugEnabled();

  private CIDRAbsorber() {
  }

  static void absorb(CIDRTransformableSet addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }

    List<CIDRAddressV4> toRemoveAddresses = new ArrayList<>();
    CIDRAddressV4 previousAddress = null;
    for (CIDRAddressV4 address : addresses) {
      if (Objects.nonNull(previousAddress) && previousAddress.contains(address)) {
        toRemoveAddresses.add(address);
        if (DEBUG_ENABLED) {
          log.debug("[{}] IP {} absorbed by {}", addresses.getName(), address, previousAddress);
        }
        continue;
      }
      previousAddress = address;
    }
    addresses.removeAll(toRemoveAddresses);
  }
}
