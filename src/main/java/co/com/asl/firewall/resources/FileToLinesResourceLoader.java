package co.com.asl.firewall.resources;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.file.FileType;
import io.vavr.control.Try;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileToLinesResourceLoader {

  @Autowired
  private ResourcePatternResolver resourcePatternResolver;

  public Stream<String> load(Resource... resources) {
    return Arrays.stream(resources).flatMap(resource ->
        Try.of(() -> Files.lines(Path.of(resource.getURI())))
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(Stream.empty()));
  }

  public Stream<String> load(FileType fileType, String profile, UFWOperation ufwOperation) {
    return load(
        Try.of(() -> resourcePatternResolver.getResources(fileType.path(profile, ufwOperation)))
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(new Resource[]{}));
  }

}
