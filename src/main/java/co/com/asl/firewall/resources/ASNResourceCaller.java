package co.com.asl.firewall.resources;

import co.com.asl.firewall.entities.CIDRAddressV4;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.Callable;

@Scope("prototype")
@Component
public class ASNResourceCaller implements Callable<Collection<CIDRAddressV4>> {

    private final String whoisCommandLine;
    private final String asn;
    @Autowired
    protected BeanFactory beanFactory;

    public ASNResourceCaller(String whoisCommandLine, String asn) {
        super();
        this.whoisCommandLine = whoisCommandLine;
        this.asn = asn;
    }

    @Override
    public Collection<CIDRAddressV4> call() {
        return beanFactory.getBean(ASNLineToASNumberTransformer.class, this.whoisCommandLine).transform(asn);
    }
}
