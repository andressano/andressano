package co.com.asl.firewall.configuration;

public enum FWOperation {
    ACCEPT("allow"), DROP("drop"), REJECT("reject");

    FWOperation(String policy) {
        this.policy = policy;
    }

    private final String policy;

    public String policy() {
        return policy;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
