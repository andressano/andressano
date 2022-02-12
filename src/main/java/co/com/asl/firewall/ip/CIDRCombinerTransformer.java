package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CIDRCombinerTransformer {

  public void transform(Collection<CIDRAddressV4> addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }

    int changes = 0;

    do {
      changes = 0;
      Set<CIDRAddressV4> toAddCidrs = new HashSet<>();
      Set<CIDRAddressV4> toRemoveCidrs = new HashSet<>();

      for (CIDRAddressV4 originalCidr : addresses) {
        for (CIDRAddressV4 modifiedCidr : addresses) {
          Optional<CIDRAddressV4> combinedCidr = originalCidr.combineIfPossible(modifiedCidr);
          if (combinedCidr.isPresent()) {
            toRemoveCidrs.add(originalCidr);
            toRemoveCidrs.add(modifiedCidr);
            toAddCidrs.add(combinedCidr.get());
            changes++;
          }
        }
      }

      addresses.removeAll(toRemoveCidrs);
      addresses.addAll(toAddCidrs);
    } while (changes > 0);
  }
}
