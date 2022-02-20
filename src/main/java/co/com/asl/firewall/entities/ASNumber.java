package co.com.asl.firewall.entities;

import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

public class ASNumber extends CIDRTransformableSet implements Comparable<ASNumber> {

  public static final String REGEX_PATTERN = "AS([0-9]{1,6})";
  public static final Predicate<String> PREDICATE = Pattern.compile(REGEX_PATTERN)
      .asMatchPredicate();
  private static final long serialVersionUID = 1L;
  private int number;

  public ASNumber(int number) {
    setNumber(number);
  }

  public ASNumber(int number, String name) {
    setNumber(number);
    setName(name);
  }


  public ASNumber(String asn) {
    Assert.isTrue(PREDICATE.test(asn), "ASN Not valid.");
    setNumber(Integer.parseInt(asn.substring(2)));
    setName(asn);
  }

  public ASNumber(Collection<CIDRAddressV4> addreses) {
    super(addreses);
  }

  public ASNumber(String asn, Collection<CIDRAddressV4> addreses) {
    this(addreses);
    Assert.isTrue(PREDICATE.test(asn), "ASN Not valid.");
    setNumber(Integer.parseInt(asn.substring(2)));
    setName(asn);
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    Assert.isTrue(number >= 0, "AS Number must be positive");
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
  public ASNumber transform() {
    return (ASNumber) super.transform();
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
