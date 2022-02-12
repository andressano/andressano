package co.com.asl.firewall.file;

import co.com.asl.firewall.entities.ASNumber;
import java.util.Collection;
import org.springframework.core.io.Resource;

public interface HostsListFileResourceReader {

  Collection<ASNumber> loadResource(Resource asnResource);
}
