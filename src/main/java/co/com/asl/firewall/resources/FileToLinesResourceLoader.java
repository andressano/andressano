package co.com.asl.firewall.resources;

import co.com.asl.firewall.configuration.FWOperation;
import co.com.asl.firewall.configuration.FirewallType;
import co.com.asl.firewall.configuration.InputFileType;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileToLinesResourceLoader {

  public Stream<String> load(Resource... resources) {
    return Arrays.stream(resources).flatMap(resource ->
        Try.of(() -> Files.lines(Path.of(resource.getURI())))
            .onFailure(e -> log.error(e.getLocalizedMessage(), e))
            .getOrElse(Stream.empty()));
  }

  public Stream<String> load(InputFileType inputFileType, FWOperation ufwOperation) {
    final ClassPathResource resource = new ClassPathResource(inputFileType.path(ufwOperation));
    if(resource.exists())
      return load(resource);
    return Stream.empty();
  }
}
