package co.com.asl.firewall.configuration.nftables;

import co.com.asl.firewall.configuration.AbstractConfigurator;
import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.output.ip.IPListLoader;
import co.com.asl.firewall.lines.nftables.NfTablesRuleLinesUtil;
import co.com.asl.firewall.resources.FileToLinesResourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
@Slf4j
public final class NfTablesConfigurator extends AbstractConfigurator {

  private final FileToLinesResourceLoader fileToLinesResourceLoader;
  private final ResourcePatternResolver resourcePatternResolver;
  private final Collection<IPListLoader> listLoaders;

  @Autowired
  public NfTablesConfigurator(
      FileToLinesResourceLoader fileToLinesResourceLoader,
      ResourcePatternResolver resourcePatternResolver, Collection<IPListLoader> listLoaders) {
    this.fileToLinesResourceLoader = fileToLinesResourceLoader;
    this.resourcePatternResolver = resourcePatternResolver;
    this.listLoaders = listLoaders;
  }

  private Collection<String> loadRulesLines() {
    Collection<String> addressRulesLines = new ArrayList<>();

    for (FWOperation ufwOperation : FWOperation.values()) {
      Collection<String> addresses = listLoaders.stream()
          .flatMap(ll -> ll.load(FirewallType.NFTABLES, getProfile(), ufwOperation))
          .collect(Collectors.toCollection(CIDRTransformableSet::new)).transform().stream()
          .flatMap(a -> NfTablesRuleLinesUtil.execute(a, ufwOperation))
          .collect(Collectors.toList());
      addressRulesLines.addAll(addresses);
    }
    return addressRulesLines;
  }

  @Override
  protected void writeFile() throws IOException {
    Collection<String> lines = new ArrayList<>();
    lines.addAll(readFiles("start.txt"));
    lines.addAll(readFiles("startRules.txt"));
    lines.addAll(loadRulesLines());
    lines.addAll(readFiles("endRules.txt"));
    lines.addAll(readFiles("end.txt"));

    final Path userRulesPath = Path.of(getPath());
    Files.write(userRulesPath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Collection<String> readFiles(String file) throws IOException {
    Collection<String> lines = new ArrayList<>();
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
            String.format("classpath*:META-INF/firewall/nftables/%s", file)))
        .collect(Collectors.toList()));
    lines.addAll(fileToLinesResourceLoader.load(resourcePatternResolver.getResources(
            String.format("classpath*:META-INF/firewall/nftables/%s/**/%s", profile, file)))
        .collect(Collectors.toList()));
    return lines;
  }
}
