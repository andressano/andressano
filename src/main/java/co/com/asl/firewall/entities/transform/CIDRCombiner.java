package co.com.asl.firewall.entities.transform;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

final class CIDRCombiner {

  private static final Logger log = LoggerFactory.getLogger(CIDRCombiner.class);

  private CIDRCombiner() {
  }

  static void combine(CIDRTransformableSet addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }
    Set<CIDRAddressV4> addList = new HashSet<>();
    Set<CIDRAddressV4> removeList = new HashSet<>();

    CIDRAddressV4 originalCidr = null;
    for (CIDRAddressV4 modifyingCidr : addresses) {
      if (Objects.nonNull(originalCidr) && originalCidr.getMask() == modifyingCidr.getMask()) {
        Optional<CIDRAddressV4> combinedCidr = CIDRAddressV4.combine(originalCidr, modifyingCidr);
        if (combinedCidr.isPresent()) {
          removeList.add(originalCidr);
          removeList.add(modifyingCidr);
          addList.add(combinedCidr.get());
          if (log.isDebugEnabled()) {
            log.debug("[{}] IPs {} and {} combined into {}",
                addresses.getName(),
                originalCidr,
                modifyingCidr,
                combinedCidr);
          }
        }
      }
      originalCidr = modifyingCidr;
    }
    addresses.removeAll(removeList);
    addresses.addAll(addList);
    if (log.isInfoEnabled()) {
      if (!addList.isEmpty()) {
        log.info("[{}] IPs {} added and {} removed by combination", addresses.getName(),
            addList.size(),
            removeList.size());
      }
    }
  }
}
