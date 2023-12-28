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
public class ASNWhoisCallable implements Callable<ASNumber> {

  private final Integer asn;
  private Collection<String> whoisQueries;

  public ASNWhoisCallable(Integer asn, Collection<String> whoisQueries) {
    this.asn = asn;
    this.whoisQueries = whoisQueries;
  }

  @Override
  public ASNumber call() throws Exception {
    ASNumber asNumber = new ASNumber(asn);
    whoisQueries.stream()
        .map(q -> String.format(q, asn))
        .flatMap(Command::execute)
        .map(String::trim)
        .filter(CIDRAddressV4.PREDICATE)
        .map(CIDRAddressV4::new)
        .forEach(asNumber::add);
    if (log.isWarnEnabled() && asNumber.isEmpty()) {
      log.warn("ASN{} has no ips", asNumber.getNumber());
    }
    return asNumber;
  }
}
