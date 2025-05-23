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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ASNListLoader {

  private final Collection<String> whoisQueries;

  @Autowired
  public ASNListLoader(@Qualifier("whoisQueries") Collection<String> whoisQueries) {
    this.whoisQueries = whoisQueries;
  }

  public Stream<ASNumber> load(Stream<Integer> asnList) {
    final int threads = Runtime.getRuntime().availableProcessors();

    Collection<ASNWhoisCallable> callables = asnList
        .map(asn -> new ASNWhoisCallable(asn, whoisQueries))
        .collect(Collectors.toList());

    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    Stream<ASNumber> list = Stream.empty();
    try {
      list = executorService.invokeAll(callables)
          .stream()
          .map(f -> Try.of(f::get)
              .onFailure(e -> log.error(e.getLocalizedMessage(), e))
              .getOrNull())
          .filter(asn -> Objects.nonNull(asn) && !asn.isEmpty());
    } catch (InterruptedException e) {
      log.error(e.getLocalizedMessage(), e);
      Thread.currentThread().interrupt();
    }
    executorService.shutdown();
    return list;
  }
}
