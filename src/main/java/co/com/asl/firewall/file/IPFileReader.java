package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class IPFileReader implements HostsListFileResourceReader {

  @Autowired
  protected IPListResourceTransformer ipListResourceTransformer;

  public Collection<ASNumber> loadResource(Resource resource) {
    Collection<CIDRAddressV4> addresses =
        this.ipListResourceTransformer
            .transform(resource)
            .filter(line -> line.matches(CIDRAddressV4.REGEX_PATTERN))
            .map(CIDRAddressV4::new)
            .collect(Collectors.toList());
    return List.of(new ASNumber(addresses));
  }
}
