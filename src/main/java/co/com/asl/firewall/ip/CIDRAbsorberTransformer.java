package co.com.asl.firewall.ip;

import co.com.asl.firewall.entities.CIDRAddressV4;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CIDRAbsorberTransformer implements Transformer<Collection<CIDRAddressV4>, Collection<CIDRAddressV4>> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Collection<CIDRAddressV4> transform(Collection<CIDRAddressV4> addresses) {
        if (CollectionUtils.isEmpty(addresses)) return addresses;

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
                Set<CIDRAddressV4> maskedAddress = addresses.stream().filter(ip -> ip.getMask() == finalI).collect(Collectors.toSet());
                maskedAddress.forEach(address -> addresses.removeIf(toRemoveCidr -> !address.equals(toRemoveCidr) && address.contains(toRemoveCidr)));
                changes += initialSize - addresses.size();
            }
            if (debugEnabled && changes > 0)
                log.debug("Absorption: Iteration {}, changes: {}", iteration, changes);
        } while (changes > 0);
        if (debugEnabled)
            log.debug("There are {} CIDR address(es) after {} iterations and {} absorption(s)", addresses.size(), iteration, totalChanges);
        return addresses;
    }
}
