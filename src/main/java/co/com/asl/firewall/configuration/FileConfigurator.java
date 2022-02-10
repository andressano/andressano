package co.com.asl.firewall.configuration;

import co.com.asl.firewall.entities.ASNumber;
import co.com.asl.firewall.entities.CIDRAddressV4;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeSet;

@Component
public final class FileConfigurator extends AbstractConfigurator {

    private void writeIPs(OutputStream os) throws IOException {
        final String LINE_SEPARATOR = System.getProperty("line.separator");
        MultiValuedMap<UFWOperation, ASNumber> asnsByOperation = super.getASNsByOperation();

        for (UFWOperation ufwOperation : UFWOperation.values()) {
            IOUtils.write(ufwOperation.name().concat(":").concat(LINE_SEPARATOR), os);
            for (ASNumber asNumber : new TreeSet<>(asnsByOperation.get(ufwOperation))) {
                if (!asNumber.isEmpty()) {
                    Iterator<CIDRAddressV4> addressesIterator = asNumber.iterator();
                    IOUtils.write(LINE_SEPARATOR.concat(asNumber.toString()).concat(":").concat(LINE_SEPARATOR), os);
                    while (addressesIterator.hasNext()) {
                        IOUtils.write(addressesIterator.next().toString(), os);
                        if (addressesIterator.hasNext()) IOUtils.write(",", os);
                    }
                }
                IOUtils.write(LINE_SEPARATOR, os);
            }
        }
    }

    public void configure(String configType, String filename) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        setSetting(configType);
        this.log.info("UFW configuration '{}' started", getSetting());
        File file = new File(filename);
        FileUtils.deleteQuietly(file);
        FileOutputStream fos = new FileOutputStream(file);
        writeIPs(fos);
        IOUtils.closeQuietly(fos);
        stopWatch.stop();
        this.log.info("UFW configuration '{}' finished in {} seconds", getSetting(), stopWatch.getTotalTimeSeconds());
    }
}
