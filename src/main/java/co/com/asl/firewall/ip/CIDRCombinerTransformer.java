package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CIDRCombinerTransformer {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public void transform(Collection<CIDRAddressV4> addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }

    int changes = 0;
    do {
      changes = 0;

      Set<CIDRAddressV4> toAddCidrs = new HashSet<>();
      Set<CIDRAddressV4> toRemoveCidrs = new HashSet<>();

      CIDRAddressV4 originalCidr = null;
      for (CIDRAddressV4 modifyingCidr : addresses) {
        if (Objects.nonNull(originalCidr) && originalCidr.getMask() == modifyingCidr.getMask()) {
          Optional<CIDRAddressV4> combinedCidr = originalCidr.combineIfPossible(modifyingCidr);
          if (combinedCidr.isPresent()) {
            toRemoveCidrs.add(originalCidr);
            toRemoveCidrs.add(modifyingCidr);
            toAddCidrs.add(combinedCidr.get());
            if (log.isDebugEnabled()) {
              log.debug("IPs {} and {} combined into {}", originalCidr, modifyingCidr,
                  combinedCidr);
            }
            changes++;
          }
        }
        originalCidr = modifyingCidr;
      }

      addresses.removeAll(toRemoveCidrs);
      addresses.addAll(toAddCidrs);
    } while (changes > 0);
  }
}
