package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.resources.ASNResourceCaller;
import io.vavr.CheckedFunction1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ASNFileReader implements HostsListFileResourceReader {

  private static final Predicate<String> ASN_LINE_PREDICATE = l -> Pattern.matches(
      ASNumber.REGEX_PATTERN, l);

  private final Logger log;
  @Autowired
  private IPListResourceTransformer ipListResourceTransformer;
  @Autowired
  private BeanFactory beanFactory;
  @Value("#{ numberThreads }")
  private int numberThreads;
  @Value("classpath:/META-INF/whois/servers.txt")
  private Resource whoisResource;

  public ASNFileReader() {
    this.log = LoggerFactory.getLogger(getClass());
  }

  private Collection<ASNResourceCaller> createResourceCallers(Resource asnResource) {
    Collection<String> asnLines = ipListResourceTransformer.transform(asnResource)
        .filter(ASN_LINE_PREDICATE).collect(Collectors.toUnmodifiableList());
    Collection<String> whoisLines = ipListResourceTransformer.transform(whoisResource)
        .collect(Collectors.toUnmodifiableList());
    Collection<ASNResourceCaller> asnResourceCallers = new ArrayList<>();

    for (String asn : asnLines) {
      for (String whoisCommandLine : whoisLines) {
        asnResourceCallers.add(beanFactory.getBean(ASNResourceCaller.class,
            whoisCommandLine, asn));
      }
    }
    return asnResourceCallers;
  }

  private List<ASNumber> loadAsNumbers(Collection<ASNResourceCaller> asnResourceCallers) {
    List<ASNumber> asNumbersList = Collections.emptyList();
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);
      asNumbersList = executorService
          .invokeAll(asnResourceCallers)
          .parallelStream()
          .map(CheckedFunction1.lift(Future::get))
          .map(f -> f.getOrElse(Optional.empty()))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
      executorService.shutdown();
    } catch (InterruptedException e) {
      log.error(e.getLocalizedMessage(), e);
      Thread.currentThread().interrupt();
    }
    return asNumbersList;
  }

  private List<ASNumber> combineAsNumbers(List<ASNumber> asNumbersList) {
    List<ASNumber> asNumbers = new ArrayList<>();
    for (ASNumber asNumber : asNumbersList) {
      int idx = asNumbers.lastIndexOf(asNumber);
      if (idx != -1) {
        asNumbers.get(idx).addAll(asNumber);
        continue;
      }
      asNumbers.add(asNumber);
    }

    return asNumbers;
  }

  public Collection<ASNumber> loadResource(Resource asnResource) {
    Collection<ASNResourceCaller> asnResourceCallers = createResourceCallers(asnResource);
    List<ASNumber> asNumbersList = loadAsNumbers(asnResourceCallers);
    return combineAsNumbers(asNumbersList);
  }
}