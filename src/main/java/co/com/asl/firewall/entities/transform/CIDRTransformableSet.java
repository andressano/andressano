package co.com.asl.firewall.entities.transform;

import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode
public class CIDRTransformableSet extends TreeSet<CIDRAddressV4> {

  private final transient Logger log = LoggerFactory.getLogger(getClass());
  private final boolean isDebugEnabled = log.isDebugEnabled();
  @Setter
  @Getter
  private String name;

  public CIDRTransformableSet(Collection<CIDRAddressV4> addresses) {
    super(addresses);
  }

  public CIDRTransformableSet() {
    super();
  }

  public CIDRTransformableSet(String name) {
    super();
    setName(name);
  }

  public CIDRTransformableSet transform() {
    int totalChanges = 0;
    int iteration = 0;
    int changes = 0;
    do {
      iteration++;
      int initialSize = this.size();
      CIDRAbsorber.absorb(this);
      CIDRCombiner.combine(this);
      changes = initialSize - this.size();

      if (isDebugEnabled && changes > 0) {
        log.debug("[{}] Transformation: Iteration {}, changes: {}", this.getName(), iteration,
            changes);
        totalChanges += changes;
      }
    } while (changes > 0);
    if (isDebugEnabled && totalChanges > 0) {
      log.debug("[{}] There are {} CIDR address(es) after {} iterations and {} change(s)",
          this.getName(), this.size(), iteration, totalChanges);
    }
    return this;
  }
}
