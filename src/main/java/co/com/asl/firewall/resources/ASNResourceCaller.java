package co.com.asl.firewall.resources;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Scope("prototype")
@Component
public class ASNResourceCaller implements Callable<ASNumber> {

  private final String whoisCommandLine;
  private final String asn;

  public ASNResourceCaller(String whoisCommandLine, String asn) {
    super();
    this.whoisCommandLine = whoisCommandLine;
    this.asn = asn;
  }

  @Override
  public ASNumber call() {
    Collection<CIDRAddressV4> addresses = ASNLineToASNumberTransformer.transform(
        this.whoisCommandLine,
        this.asn);
    if (CollectionUtils.isEmpty(addresses)) {
      return null;
    }
    return new ASNumber(this.asn, addresses);
  }
}
