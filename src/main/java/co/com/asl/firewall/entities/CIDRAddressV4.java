package co.com.asl.firewall.entities;

import java.util.Optional;
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
  private static final String NUMBER_PATTERN = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
  public static final String REGEX_PATTERN =
      "^(" + NUMBER_PATTERN + "\\.){3}" + NUMBER_PATTERN + "(\\/(\\d|[1-2]\\d|3[0-2]))?$";
  private int ip;
  private short mask;

  public CIDRAddressV4(String ip) {
    set(ip);
  }

  public CIDRAddressV4(int ip, short mask) {
    this.ip = ip;
    setMask(mask);
  }

  public int getIp() {
    return ip;
  }

  protected void setIp(final int ip) {
    this.ip = ip;
  }

  public short getMask() {
    return mask;
  }

  protected void setMask(final short mask) {
    Assert.isTrue(mask >= 1 && mask <= TOTAL_BYTES, "Mask must be between 1 and 32");
    this.mask = mask;
    this.ip &= MAX_VALUE << (TOTAL_BYTES - mask);
  }

  private void set(String ip) {
    Assert.isTrue(Pattern.compile(REGEX_PATTERN).matcher(ip).matches(), "IP syntax is not correct");
    String[] parts = StringUtils.split(ip, MASK_SEPARATOR);
    this.ip = 0;
    String[] octets = StringUtils.split(parts[0], GROUP_SEPARATOR);
    for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
      this.ip |= Integer.parseInt(octets[i]);
      if (i < NUMBER_OF_GROUPS - 1) {
        this.ip <<= BYTES_PER_GROUP;
      }
    }

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
      return Integer.compare(cidrAddressV4.getMask(), getMask());
    }
    return compare;
  }

  public Optional<CIDRAddressV4> combineIfPossible(CIDRAddressV4 cidrAddressV4) {
    if (this.getMask() > 0 && cidrAddressV4.getMask() > 0 && this.getMask() < 32
        && cidrAddressV4.getMask() < 32 && this.getMask() != cidrAddressV4.getMask()) {
      return Optional.empty();
    }

    final int thisIp = (this.getIp() >>> (TOTAL_BYTES - this.getMask()));
    final int cidrIp = (cidrAddressV4.getIp() >>> (TOTAL_BYTES - cidrAddressV4.getMask()));
    final int minIp = Math.min(thisIp, cidrIp);
    final int andIp = thisIp & cidrIp;
    final int difIp = Math.abs(thisIp - cidrIp);
    if (minIp == andIp && difIp == 1) {
      return Optional.of(
          new CIDRAddressV4(this.getIp() & cidrAddressV4.getIp(),
              (short) (this.getMask() - 1)));
    }
    return Optional.empty();
  }

  public boolean contains(CIDRAddressV4 cidrAddressV4) {
    return this.getMask() <= cidrAddressV4.getMask()
        && (cidrAddressV4.getIp() & MAX_VALUE << (TOTAL_BYTES - this.getMask())) == this.getIp();
  }

  public String toString() {
    final StringBuilder sbToString = new StringBuilder();
    for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
      int number = this.getIp() << (i * BYTES_PER_GROUP) >>> ((NUMBER_OF_GROUPS - 1) * BYTES_PER_GROUP);
      sbToString.append(number);
      if (i < NUMBER_OF_GROUPS - 1) {
        sbToString.append(GROUP_SEPARATOR);
      }
    }
    if (this.getMask() != 32) {
      sbToString.append(String.format("%s%s", MASK_SEPARATOR, mask));
    }
    return sbToString.toString();
  }
}
