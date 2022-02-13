package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CIDRAbsorberTransformer {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public void transform(Collection<CIDRAddressV4> addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }

    final boolean debugEnabled = log.isDebugEnabled();
    int iteration = 0;
    int changes = 0;
    int totalChanges = 0;

    do {
      iteration++;
      totalChanges += changes;
      changes = 0;
      final int initialSize = addresses.size();
      List<CIDRAddressV4> toRemoveAddresses = new ArrayList<>();

      CIDRAddressV4 previousAddress = null;
      for (CIDRAddressV4 address : addresses) {
        if (Objects.nonNull(previousAddress) && previousAddress.contains(address)) {
          toRemoveAddresses.add(address);
          changes++;
          if (debugEnabled) {
            log.debug("IP {} absorbed by {}", address, previousAddress);
          }
          continue;
        }
        previousAddress = address;
      }
      addresses.removeAll(toRemoveAddresses);
      if (debugEnabled && changes > 0) {
        log.debug("Absorption: Iteration {}, changes: {}", iteration, changes);
      }
    } while (changes > 0);
    if (debugEnabled) {
      log.debug("There are {} CIDR address(es) after {} iterations and {} absorption(s)",
          addresses.size(), iteration, totalChanges);
    }
  }
}
