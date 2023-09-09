package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.ip.concurrent.ASNWhoisCallable;
import io.vavr.control.Try;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ASNListLoader {

  @Autowired
  private BeanFactory beanFactory;

  public Stream<ASNumber> load(Stream<Integer> asnList) {
    final int threads = Runtime.getRuntime().availableProcessors();

    Collection<ASNWhoisCallable> callables = asnList
        .map(asn -> beanFactory.getBean(ASNWhoisCallable.class, asn))
        .collect(Collectors.toList());

    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    Stream<ASNumber> list = Stream.empty();
    try {
      list = executorService.invokeAll(callables)
          .stream()
          .map(f -> Try.of(f::get)
              .onFailure(e -> log.error(e.getLocalizedMessage(), e)).getOrNull())
          .filter(Objects::nonNull);
    } catch (InterruptedException e) {
      log.error(e.getLocalizedMessage(), e);
      Thread.currentThread().interrupt();
    }
    executorService.shutdown();
    return list;
  }
}
