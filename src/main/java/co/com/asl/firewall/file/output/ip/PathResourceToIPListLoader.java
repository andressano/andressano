package co.com.asl.firewall.file.output.ip;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.output.ResourceToPathsLoader;
import io.vavr.control.Try;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathResourceToIPListLoader implements IPListLoader {

  private final ResourceToPathsLoader resourceToPathsLoader;

  @Autowired
  public PathResourceToIPListLoader(
      ResourceToPathsLoader resourceToPathsLoader) {
    this.resourceToPathsLoader = resourceToPathsLoader;
  }

  public Stream<CIDRAddressV4> load(FirewallType firewallType, String setting,
      FWOperation ufwOperation) {
    return resourceToPathsLoader
        .load(ufwOperation)
        .flatMap(PathResourceToIPListLoader::pathToFiles)
        .flatMap(p -> Try.of(() -> Files.readAllLines(p).stream()).getOrElse(Stream.empty()))
        .map(l -> l.replaceFirst("#(.)*", ""))
        .map(l -> l.replaceAll("\\s+", ""))
        .filter(StringUtils::isNotBlank)
        .filter(CIDRAddressV4.PREDICATE)
        .map(CIDRAddressV4::new);
  }

  private static Stream<Path> pathToFiles(String pathText) {
    Path path = Path.of(pathText);
    Stream<Path> stream = Stream.empty();

    if (Files.isDirectory(path)) {
      stream = Stream.concat(stream, Try.of(() -> Files.list(path))
          .onFailure(e -> log.error(e.getLocalizedMessage(), e))
          .getOrElse(Stream.empty()));
    } else if (Files.isRegularFile(path)) {
      stream = Stream.concat(stream, Stream.of(path));
    }
    return stream.filter(p -> p.toString().endsWith(".ipset") || p.toString().endsWith(".netset"));
  }
}
