package co.com.asl.blocker.file;

import co.com.asl.blocker.enums.Operation;
import co.com.asl.blocker.file.line.LineConstants;
import io.vavr.control.Try;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HostsLinesCreator {

  @Autowired
  private ListableBeanFactory beanFactory;
  @Autowired
  @Qualifier("whitelist")
  private Collection<String> whitelist;

  private boolean isValid(String host) {
    return StringUtils.isNotBlank(host) && whitelist.stream().anyMatch(h -> !host.endsWith(h));
  }

  public Collection<String> create(Operation operation) {
    return beanFactory.getBeansOfType(Operable.class)
        .values()
        .stream()
        .filter(o -> o.validOperations().contains(operation))
        .flatMap(o -> Try.of(o::loadLines)
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(Stream.empty()))
        .filter(this::isValid)
        .sorted()
        .distinct()
        .map(l -> String.format("%s\t%s", LineConstants.ROUTE_IP, l))
        .collect(Collectors.toList());
  }
}
