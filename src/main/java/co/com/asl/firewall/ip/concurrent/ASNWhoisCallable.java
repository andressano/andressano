package co.com.asl.firewall.ip.concurrent;

import co.com.asl.firewall.command.Command;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Scope("prototype")
@Component
public class ASNWhoisCallable implements Callable<ASNumber> {

  private final Integer asn;
  @Autowired
  @Qualifier("whoisQueries")
  private Collection<String> whoisQueries;

  public ASNWhoisCallable(Integer asn) {
    this.asn = asn;
  }

  @Override
  public ASNumber call() throws Exception {
    ASNumber result = whoisQueries.stream()
        .map(q -> String.format(q, asn))
        .flatMap(Command::execute)
        .map(String::trim)
        .filter(CIDRAddressV4.PREDICATE)
        .map(CIDRAddressV4::new)
        .collect(Collectors.toCollection(() -> new ASNumber(asn)));
    if (CollectionUtils.isEmpty(result)) {
      log.warn("{} has no records", this.asn);
    }
    return result;
  }
}
