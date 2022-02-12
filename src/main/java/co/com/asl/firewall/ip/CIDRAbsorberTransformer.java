package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
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

      for (int i = 1; i < CIDRAddressV4.TOTAL_BYTES; i++) {
        final int finalI = i;
        final int initialSize = addresses.size();
        Collection<CIDRAddressV4> toCompareAddress = Collections.synchronizedList(
            addresses.parallelStream()
                .filter(Objects::nonNull)
                .filter(ip -> ip.getMask() == finalI)
                .collect(Collectors.toList()));

        toCompareAddress.forEach(a ->
            addresses.removeAll(
                toCompareAddress.stream().filter(a2 -> (!a.equals(a2) && a.contains(a2))).collect(
                    Collectors.toList()))
        );

        changes += initialSize - addresses.size();
      }
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
