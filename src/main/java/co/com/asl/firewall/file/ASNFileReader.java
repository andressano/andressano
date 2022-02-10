package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.ip.CIDRTransformer;
import co.com.asl.firewall.resources.ASNResourceCaller;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ASNFileReader implements Transformer<Resource, Collection<ASNumber>> {
    private static final Predicate<String> ASN_LINE_PREDICATE = l -> Pattern.matches(ASNumber.REGEX_PATTERN, l);

    private final Logger log;
    @Autowired
    protected CIDRTransformer cidrTransformer;
    @Autowired
    private ResourceLinesTransformer resourceLinesTransformer;
    @Autowired
    private BeanFactory beanFactory;
    @Value("#{ numberThreads }")
    private int numberThreads;
    @Value("classpath:/META-INF/whois/servers.txt")
    private Resource whoisResource;

    public ASNFileReader() {
        this.log = LoggerFactory.getLogger(getClass());
    }

    public Collection<ASNumber> transform(Resource asnResource) {
        Set<ASNumber> asNumbers = Collections.synchronizedSet(new TreeSet<>());
        Stream<String> asnLines = resourceLinesTransformer.transform(asnResource).filter(ASN_LINE_PREDICATE);
        Collection<String> whoisLines = resourceLinesTransformer.transform(whoisResource).collect(Collectors.toUnmodifiableList());
        ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);

        for (String asn : asnLines.collect(Collectors.toUnmodifiableList())) {
            Collection<CIDRAddressV4> addresses = new TreeSet<>();
            for (String whoisCommandLine : whoisLines) {
                ASNResourceCaller asnResourceCaller = beanFactory.getBean(ASNResourceCaller.class, whoisCommandLine, asn);
                Future<Collection<CIDRAddressV4>> futureASNumber = executorService.submit(asnResourceCaller);
                try {
                    addresses.addAll(futureASNumber.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getLocalizedMessage(), e);
                    Thread.currentThread().interrupt();
                }
            }
            if (!CollectionUtils.isEmpty(addresses)) {
                asNumbers.add(new ASNumber(asn, cidrTransformer.transform(addresses)));
            }
        }
        executorService.shutdown();
        return asNumbers;
    }
}
