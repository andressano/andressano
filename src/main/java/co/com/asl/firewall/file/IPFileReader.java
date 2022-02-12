package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class IPFileReader implements HostsListFileResourceReader {

  private static final Predicate<String> CIDR_PREDICATE = line -> Pattern.matches(
      CIDRAddressV4.REGEX_PATTERN, line);
  @Autowired
  protected ResourceLinesTransformer resourceLinesTransformer;

  public Collection<ASNumber> loadResource(Resource resource) {
    Collection<CIDRAddressV4> addresses = this.resourceLinesTransformer.transform(resource).stream()
        .filter(CIDR_PREDICATE).map(CIDRAddressV4::new).collect(Collectors.toList());
    return List.of(new ASNumber(addresses));
  }
}
