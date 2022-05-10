package co.com.asl.firewall.file.output.asn;

import co.com.asl.firewall.configuration.ufw.UFWOperation;
import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.file.output.ip.ResourceToIPListLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IPResourceToASNLoader implements ASNLoader {

  private final ResourceToIPListLoader resourceToIPListLoader;

  @Autowired
  public IPResourceToASNLoader(
      ResourceToIPListLoader resourceToIPListLoader) {
    this.resourceToIPListLoader = resourceToIPListLoader;
  }

  @Override
  public Stream<ASNumber> load(String profile, UFWOperation ufwOperation) {
    return Stream.of(resourceToIPListLoader.load(profile, ufwOperation)
        .collect(Collectors.toCollection(() -> new ASNumber(0, "Others"))));
  }
}
