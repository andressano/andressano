package co.com.asl.firewall.configuration;

public enum InputFileType {
	ASN_FILETYPE("META-INF/firewall/%s/%s/%s/ASNumbers.txt"),

	IP_FILETYPE("META-INF/firewall/%s/%s/%s/IPs.txt");

	private final String pattern;

	InputFileType(String pattern) {
		this.pattern = pattern;
	}

	public String path(FirewallType firewallType, String profile, FWOperation ufwOperation) {
		return String.format(pattern, firewallType.name().toLowerCase(), profile, ufwOperation.name().toLowerCase());
	}

	public String getPattern() {
		return pattern;
	}
}
