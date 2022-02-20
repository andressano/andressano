package co.com.asl.firewall.file.output.ip;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.CIDRAddressV4;
import java.util.stream.Stream;

public interface IPListLoader {

  Stream<CIDRAddressV4> load(String setting, UFWOperation ufwOperation);
}
