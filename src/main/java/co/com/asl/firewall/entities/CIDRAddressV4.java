package co.com.asl.firewall.entities;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class CIDRAddressV4 implements Comparable<CIDRAddressV4> {

  public static final short TOTAL_BYTES = 32;
  protected static final String MASK_SEPARATOR = "/";
  protected static final String GROUP_SEPARATOR = ".";
  protected static final short BYTES_PER_GROUP = 8;
  protected static final short NUMBER_OF_GROUPS = TOTAL_BYTES / BYTES_PER_GROUP;
  protected static final int MAX_VALUE = 0xffffffff;
  public static final String NUMBER_PATTERN = "(25[0-5]|2[0-4]\\d|1(\\d{2})|[1-9]\\d|\\d)";
  public static final String REGEX_PATTERN =
      "^(" + NUMBER_PATTERN + "\\.){3}" + NUMBER_PATTERN + "(\\/(\\d|[1-2]\\d|3[0-2]))?$";
  public static final Predicate<String> PREDICATE = Pattern.compile(REGEX_PATTERN)
      .asMatchPredicate();
  private int ip;
  private int mask;

  public CIDRAddressV4(String ip) {
    set(ip);
  }

  public CIDRAddressV4(int ip, int mask) {
    this.ip = ip;
    setMask(mask);
  }

  public CIDRAddressV4(int group1, int group2, int group3, int group4, int mask) {
    setIp(group1, group2, group3, group4);
    setMask(mask);
  }

  public CIDRAddressV4(int group1, int group2, int group3, int group4) {
    this(group1, group2, group3, group4, TOTAL_BYTES);
    setMask(mask);
  }

  public int getIp() {
    return ip;
  }

  protected void setIp(final int ip) {
    this.ip = ip;
  }

  protected void setIp(final int group1, final int group2, final int group3,
      final int group4) {
    this.ip = 0;
    this.ip |= group1;
    this.ip <<= BYTES_PER_GROUP;
    this.ip |= group2;
    this.ip <<= BYTES_PER_GROUP;
    this.ip |= group3;
    this.ip <<= BYTES_PER_GROUP;
    this.ip |= group4;
  }

  public int getMask() {
    return mask;
  }

  protected void setMask(final int mask) {
    Assert.isTrue(mask >= 1 && mask <= TOTAL_BYTES, "Mask must be between 1 and 32");
    this.mask = mask;
    this.ip &= MAX_VALUE << (TOTAL_BYTES - mask);
  }

  private void set(String ip) {
    Assert.isTrue(Pattern.compile(REGEX_PATTERN).matcher(ip).matches(), "IP syntax is not correct");
    String[] parts = StringUtils.split(ip, MASK_SEPARATOR);
    String[] octets = StringUtils.split(parts[0], GROUP_SEPARATOR);
    setIp(Integer.parseUnsignedInt(octets[0]),
        Integer.parseUnsignedInt(octets[1]),
        Integer.parseUnsignedInt(octets[2]),
        Integer.parseUnsignedInt(octets[3]));
    if (parts.length == 2) {
      setMask(Short.parseShort(parts[1]));
    } else {
      setMask(TOTAL_BYTES);
    }
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CIDRAddressV4 && compareTo((CIDRAddressV4) obj) == 0;
  }

  @Override
  public int hashCode() {
    return this.getIp();
  }

  @Override
  public int compareTo(CIDRAddressV4 cidrAddressV4) {
    int compare = 0;
    for (int i = NUMBER_OF_GROUPS - 1; i >= 0 && compare == 0; i--) {
      compare = Integer.compare(getIp() >>> (BYTES_PER_GROUP * i),
          cidrAddressV4.getIp() >>> (BYTES_PER_GROUP * i));
    }
    if (compare == 0) {
      return Integer.compare(getMask(), cidrAddressV4.getMask());
    }
    return compare;
  }

  public boolean canAbsorb(CIDRAddressV4 cidrAddressV4) {
    return this.getMask() < cidrAddressV4.getMask() &&
        (cidrAddressV4.getIp() & MAX_VALUE << (TOTAL_BYTES - this.getMask())) == this.getIp();
  }

  public String toString() {
    final StringBuilder sbToString = new StringBuilder();
    for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
      int number =
          this.getIp() << (i * BYTES_PER_GROUP) >>> ((NUMBER_OF_GROUPS - 1) * BYTES_PER_GROUP);
      sbToString.append(number);
      if (i < NUMBER_OF_GROUPS - 1) {
        sbToString.append(GROUP_SEPARATOR);
      }
    }
    if (this.getMask() != TOTAL_BYTES) {
      sbToString.append(String.format("%s%s", MASK_SEPARATOR, mask));
    }
    return sbToString.toString();
  }

  public static Optional<CIDRAddressV4> combine(CIDRAddressV4 cidr1, CIDRAddressV4 cidr2) {
    if (cidr1 != null &&
        cidr2 != null &&
        cidr1.getMask() == cidr2.getMask()) {
      final int newMask = cidr1.getMask() - 1;
      final int cidr1Ip = cidr1.getIp() >>> (TOTAL_BYTES - newMask);
      final int cidr2Ip = cidr2.getIp() >>> (TOTAL_BYTES - newMask);
      if (cidr1Ip == cidr2Ip) {
        return Optional.of(new CIDRAddressV4(cidr1Ip << (TOTAL_BYTES - newMask), newMask));
      }
    }
    return Optional.empty();
  }
}
