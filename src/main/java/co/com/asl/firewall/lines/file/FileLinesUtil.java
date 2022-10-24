package co.com.asl.firewall.lines.file;

import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

public final class FileLinesUtil {

  private FileLinesUtil() {
  }

  public static final Stream<String> createLines(CIDRTransformableSet transformableSet) {
    if (CollectionUtils.isEmpty(transformableSet)) {
      return Stream.empty();
    }

    return Stream.of(
        transformableSet.getName() + ":",
        StringUtils.join(transformableSet, ", "),
        "");
  }

}
