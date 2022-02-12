package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CIDRTransformer {

  private final Logger log = LoggerFactory.getLogger(getClass());
  @Autowired
  protected CIDRAbsorberTransformer cidrAbsorberTransformer;
  @Autowired
  protected CIDRCombinerTransformer cidrCombinerTransformer;

  public void transform(Collection<CIDRAddressV4> addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      return;
    }
    final boolean isInfoEnabled = log.isInfoEnabled();

    int totalChanges = 0;
    int iteration = 0;
    int changes = 0;
    do {
      iteration++;
      int initialSize = addresses.size();
      cidrAbsorberTransformer.transform(addresses);
      cidrCombinerTransformer.transform(addresses);
      changes = initialSize - addresses.size();

      if (isInfoEnabled && changes > 0) {
        log.info("Transformation: Iteration {}, changes: {}", iteration, changes);
        totalChanges += changes;
      }
    } while (changes > 0);
    if (isInfoEnabled && totalChanges > 0) {
      log.info("There are {} CIDR address(es) after {} iterations and {} change(s)",
          addresses.size(), iteration, totalChanges);
    }
  }
}
