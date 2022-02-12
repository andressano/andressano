package co.com.asl.firewall.entities;

import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

public class ASNumber extends TreeSet<CIDRAddressV4> implements Comparable<ASNumber> {

  public static final String REGEX_PATTERN = "AS([0-9]{1,6})";
  private static final long serialVersionUID = 1L;
  private int number;

  public ASNumber(int number) {
    super();
    setNumber(number);
  }

  public ASNumber(String asn) {
    Assert.isTrue(Pattern.compile(REGEX_PATTERN).matcher(asn).matches(), "ASN Not valid.");
    setNumber(Integer.parseInt(asn.substring(2)));
  }

  public ASNumber(Collection<CIDRAddressV4> addreses) {
    super(addreses);
  }

  public ASNumber(String asn, Collection<CIDRAddressV4> addreses) {
    this(addreses);
    Assert.isTrue(Pattern.compile(REGEX_PATTERN).matcher(asn).matches(), "ASN Not valid.");
    setNumber(Integer.parseInt(asn.substring(2)));
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    Assert.isTrue(number > 0, "AS Number must be positive");
    this.number = number;
  }

  public void join(ASNumber asNumber) {
    if (Objects.isNull(asNumber)) {
      return;
    }

    if (asNumber.getNumber() == this.getNumber()) {
      addAll(asNumber);
    }
  }

  @Override
  public String toString() {
    return "AS" + number;
  }

  @Override
  public int hashCode() {
    return getNumber();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ASNumber && compareTo((ASNumber) obj) == 0;
  }

  @Override
  public int compareTo(ASNumber asNumber) {
    return Integer.compare(getNumber(), asNumber.getNumber());
  }
}
