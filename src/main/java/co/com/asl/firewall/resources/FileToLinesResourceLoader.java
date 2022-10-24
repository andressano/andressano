package co.com.asl.firewall.resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.configuration.InputFileType;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileToLinesResourceLoader {

  private final ResourcePatternResolver resourcePatternResolver;

  @Autowired
  public FileToLinesResourceLoader(
      ResourcePatternResolver resourcePatternResolver) {
    this.resourcePatternResolver = resourcePatternResolver;
  }

  public Stream<String> load(Resource... resources) {
    return Arrays.stream(resources).flatMap(resource ->
        Try.of(() -> Files.lines(Path.of(resource.getURI())))
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(Stream.empty()));
  }

  public Stream<String> load(InputFileType inputFileType, FirewallType firewallType, String profile, FWOperation ufwOperation) {
    return load(
        Try.of(() -> resourcePatternResolver.getResources(inputFileType.path(firewallType, profile, ufwOperation)))
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(new Resource[]{}));
  }

}
