package co.com.asl.firewall.configuration.ufw;

public enum UFWOperation {
    ACCEPT("allow"), DROP("drop"), REJECT("reject");

    UFWOperation(String policy) {
        this.policy = policy;
    }

    private String policy;

    public String policy() {
        return policy;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
