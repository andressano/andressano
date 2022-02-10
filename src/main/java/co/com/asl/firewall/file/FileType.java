package co.com.asl.firewall.file;

import java.util.Collection;

import org.apache.commons.collections4.Transformer;
import org.springframework.core.io.Resource;

import co.com.asl.firewall.configuration.UFWOperation;
import co.com.asl.firewall.entities.ASNumber;

public enum FileType {
	ASN_FILETYPE("classpath*:META-INF/firewall/%s/%s/ASNumbers.txt", ASNFileReader.class),

	IP_FILETYPE("classpath*:META-INF/firewall/%s/%s/IPs.txt", IPFileReader.class);

	private final String pattern;

	private final Class<? extends Transformer<Resource, Collection<ASNumber>>> transformer;

	FileType(String pattern, Class<? extends Transformer<Resource, Collection<ASNumber>>> transformer) {
		this.pattern = pattern;
		this.transformer = transformer;
	}

	public String path(String profile, UFWOperation ufwOperation) {
		return String.format(pattern, profile, ufwOperation.name().toLowerCase());
	}

	public String getPattern() {
		return pattern;
	}

	public Class<? extends Transformer<Resource, Collection<ASNumber>>> getTransformer() {
		return transformer;
	}
	
	
}
