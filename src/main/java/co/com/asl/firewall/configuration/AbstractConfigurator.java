package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import co.com.asl.firewall.file.FileType;
import co.com.asl.firewall.file.HostsListFileResourceReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class AbstractConfigurator {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  @Autowired
  protected ResourcePatternResolver resourcePatternResolver;

  private String setting;
  @Autowired
  private BeanFactory beanFactory;

  protected String getSetting() {
    return this.setting;
  }

  protected void setSetting(String setting) {
    this.setting = setting;
  }

  private Stream<ASNumber> loadResources(FileType fileType, UFWOperation ufwOperation)
      throws IOException {
    log.debug("Loading resource for filetype {} and operation {}", fileType, ufwOperation);
    final String path = fileType.path(getSetting(), ufwOperation);
    final HostsListFileResourceReader resourceReader =
        beanFactory.getBean(fileType.getResourceReader());
    return Arrays
        .stream(resourcePatternResolver.getResources(path))
        .flatMap(r -> resourceReader.loadResource(r).stream());
  }

  protected MultiValuedMap<UFWOperation, ASNumber> getASNsByOperation() throws IOException {
    MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = new HashSetValuedHashMap<>();

    for (UFWOperation ufwOperation : UFWOperation.values()) {
      for (FileType fileType : FileType.values()) {
        loadResources(fileType, ufwOperation)
            .forEach(a -> asnsByOperation.putAll(ufwOperation, Collections.singleton(a)));
      }
    }
    return asnsByOperation;
  }

  protected synchronized MultiValuedMap<UFWOperation, CIDRAddressV4> getCidrsByOperation()
      throws IOException {
    MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = getASNsByOperation();
    MultiValuedMap<UFWOperation, CIDRAddressV4> cidrsByOperation = new HashSetValuedHashMap<>();

    for (UFWOperation ufwOperation : UFWOperation.values()) {
      Collection<CIDRAddressV4> addresses =
          asnsByOperation
              .get(ufwOperation)
              .stream()
              .flatMap(ASNumber::stream)
              .collect(Collectors.toCollection(CIDRTransformableSet::new))
              .transform();
      cidrsByOperation.putAll(ufwOperation, addresses);
    }
    return cidrsByOperation;
  }
}
