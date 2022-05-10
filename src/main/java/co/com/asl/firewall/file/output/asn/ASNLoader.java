package co.com.asl.firewall.file.output.asn;

import co.com.asl.firewall.configuration.ufw.UFWOperation;
import co.com.asl.firewall.entities.ASNumber;
import java.util.stream.Stream;

public interface ASNLoader {

  Stream<ASNumber> load(String setting, UFWOperation ufwOperation);
}
