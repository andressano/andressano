package co.com.asl.blocker.line.generation;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.host.Blacklist;
import co.com.asl.blocker.host.Whitelist;
import co.com.asl.blocker.line.LineFunctions;
import io.vavr.control.Try;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HostsLinesCreator implements LinesCreator {

  @Autowired
  private Collection<Blacklist> blacklists;
  @Autowired
  private Collection<Whitelist> whitelist;

  private boolean isValid(String host) {
    return StringUtils.isNotBlank(host) && whitelist.stream().flatMap(wl ->
        Try.of(wl::loadLines)
            .getOrElse(Stream.empty())).anyMatch(h -> !host.endsWith(h));
  }

  public Stream<String> create() throws IOException {
    return blacklists
        .stream()
        .flatMap(b -> Try.of(b::loadLines)
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(Stream.empty()))
        .filter(this::isValid)
        .sorted()
        .distinct()
        .map(LineFunctions::formatLine);
  }

  @Override
  public int priority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean isOperationAllowed(Operation operation) {
    return Operation.CREATE_HOSTS_FILE.equals(operation) || Operation.DEFAULT_HOSTS_FILE.equals(
        operation);
  }
}
