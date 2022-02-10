package co.com.asl.firewall.resources;

import co.com.asl.firewall.command.Command;
import co.com.asl.firewall.command.CommandTransformer;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.ip.CIDRPredicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
public class ASNLineToASNumberTransformer implements Transformer<String, Collection<CIDRAddressV4>> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String whoisCommandLine;

    @Autowired
    private CommandTransformer commandTransformer;

    public ASNLineToASNumberTransformer(String whoisCommandLine) {
        super();
        this.whoisCommandLine = whoisCommandLine;
    }

    private String findWhois() {
        for (String word : StringUtils.split(this.whoisCommandLine, ' ')) {
            if (!"whois".equalsIgnoreCase(word) && StringUtils.containsIgnoreCase(word, "whois")) return word;
        }
        return "";
    }

    public synchronized Collection<CIDRAddressV4> transform(String asn) {
        Command command = new Command(String.format(whoisCommandLine, asn));
        String tempLine = commandTransformer.transform(command);
        if (StringUtils.isBlank(tempLine)) return Collections.emptyList();

        Collection<CIDRAddressV4> addresses = Arrays.asList(StringUtils.split(tempLine, " ")).stream().filter(CIDRPredicate.IS_CIDR_PREDICATE).map(CIDRAddressV4::new).collect(Collectors.toList());
        if (log.isWarnEnabled() && CollectionUtils.isEmpty(addresses))
            log.warn("No CIDR addresses from {} had been loaded from {}", asn, findWhois());

        if (log.isDebugEnabled() && !CollectionUtils.isEmpty(addresses))
            log.debug("{} CIDR addresses from {} had been loaded from {}", addresses.size(), asn, findWhois());

        return addresses;
    }
}
