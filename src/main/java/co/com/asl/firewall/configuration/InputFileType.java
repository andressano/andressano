package co.com.asl.firewall.configuration;

import lombok.Getter;

@Getter
public enum InputFileType {
	ASN_FILETYPE("META-INF/common/%s/ASNumbers.txt"),

	IP_FILETYPE("META-INF/common/%s/IPs.txt"),

	IP_PATHS("META-INF/common/%s/ip-paths.txt");

	private final String pattern;

	InputFileType(String pattern) {
		this.pattern = pattern;
	}

	public String path(FWOperation ufwOperation) {
		return String.format(pattern, ufwOperation.name().toLowerCase());
	}
}
