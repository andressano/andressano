package co.com.asl.blocker.file;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.line.LineConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class HostsLinesCreator {

  @Autowired
  private BlacklistLoader blacklistLoader;
  @Autowired
  private SitesToHostsLoader sitesToHostsLoader;
  @Autowired
  @Qualifier("whitelist")
  private Collection<String> whitelist;

  private boolean isValid(String host) {
    return StringUtils.isNotBlank(host) && whitelist.stream().anyMatch(h->!host.endsWith(h));
  }

  public Collection<String> load(Operation operation) throws IOException {

    Stream<String> hosts = blacklistLoader.loadHostsLines();
    if (Operation.CREATE_HOSTS_FILE.equals(operation)) {
      hosts = Stream.concat(hosts, sitesToHostsLoader.loadHostsLines());
    }

    return hosts
        .filter(this::isValid)
        .map(l -> String.format("%s\t%s", LineConstants.ROUTE_IP, l))
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toCollection(TreeSet::new));
  }
}
