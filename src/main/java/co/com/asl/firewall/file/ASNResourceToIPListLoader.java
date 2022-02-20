package co.com.asl.firewall.file;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ASNResourceToIPListLoader implements IPListLoader {

  @Autowired
  private ResourceToASNListLoader resourceToASNListLoader;
  @Autowired
  private ASNListLoader asnListLoader;

  public Stream<CIDRAddressV4> load(String string, UFWOperation ufwOperation) {
    return asnListLoader.load(
            resourceToASNListLoader
                .load(string, ufwOperation))
        .flatMap(Collection::stream);
  }
}
