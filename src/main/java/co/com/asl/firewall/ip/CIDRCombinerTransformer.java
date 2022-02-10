package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class CIDRCombinerTransformer implements Transformer<Collection<CIDRAddressV4>, Collection<CIDRAddressV4>> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Collection<CIDRAddressV4> transform(Collection<CIDRAddressV4> addresses) {
        if (CollectionUtils.isEmpty(addresses)) return addresses;

        boolean debugEnabled = log.isDebugEnabled();
        int iteration = 0;
        int changes = 0;
        int totalChanges = 0;

        do {
            iteration++;
            totalChanges += changes;
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
                        if (debugEnabled)
                            log.debug("Combination: Combining {} and {} into {}", originalCidr, modifiedCidr, combinedCidr);
                    }
                }
            }

            addresses.removeAll(toRemoveCidrs);
            addresses.addAll(toAddCidrs);

            if (debugEnabled && changes > 0) log.debug("Combination: Iteration {}, changes: {}", iteration, changes);
        } while (changes > 0);
        if (debugEnabled)
            log.debug("There are {} CIDR address(es) after {} iterations and {} combination(s)", addresses.size(), iteration, totalChanges);
        return addresses;
    }
}
