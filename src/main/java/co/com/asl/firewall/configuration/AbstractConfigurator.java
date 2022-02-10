package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import co.com.asl.firewall.file.FileType;
import co.com.asl.firewall.ip.CIDRTransformer;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractConfigurator {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    protected ResourcePatternResolver resourcePatternResolver;
    @Autowired
    protected CIDRTransformer cidrTransformer;

    private String setting;
    @Autowired
    private BeanFactory beanFactory;

    protected String getSetting() {
        return this.setting;
    }

    protected void setSetting(String setting) {
        this.setting = setting;
    }

    private Collection<Resource> loadResources(FileType fileType, UFWOperation ufwOperation) throws IOException {
        String path = fileType.path(getSetting(), ufwOperation);
        return Arrays.asList(resourcePatternResolver.getResources(path));
    }

    protected MultiValuedMap<UFWOperation, ASNumber> getASNsByOperation() throws IOException {
        MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = new HashSetValuedHashMap<>();

        for (UFWOperation ufwOperation : UFWOperation.values()) {
            for (FileType fileType : FileType.values()) {
                Transformer<Resource, Collection<ASNumber>> transformer = beanFactory.getBean(fileType.getTransformer());
                for (Resource resource : loadResources(fileType, ufwOperation)) {
                    Collection<ASNumber> asNumbers = transformer.transform(resource);
                    asnsByOperation.putAll(ufwOperation, asNumbers);
                }
            }
        }
        return asnsByOperation;
    }

    protected MultiValuedMap<UFWOperation, CIDRAddressV4> getCidrsByOperation() throws IOException {
        MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = getASNsByOperation();
        MultiValuedMap<UFWOperation, CIDRAddressV4> cidrsByOperation = new HashSetValuedHashMap<>();

        for (UFWOperation ufwOperation : UFWOperation.values())
            cidrsByOperation.putAll(ufwOperation, cidrTransformer.transform(asnsByOperation.get(ufwOperation).stream().flatMap(ASNumber::stream).collect(Collectors.toList())));
        return cidrsByOperation;
    }
}
