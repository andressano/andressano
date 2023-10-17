package co.com.asl.firewall.lines.file;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import co.com.asl.firewall.entities.transform.CIDRTransformableSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileLinesUtil {

  public static Stream<String> createLines(CIDRTransformableSet transformableSet) {
    if (CollectionUtils.isEmpty(transformableSet)) {
      return Stream.empty();
    }

    return Stream.of(
        transformableSet.getName() + ":",
        StringUtils.join(transformableSet, ", "),
        "");
  }

}
