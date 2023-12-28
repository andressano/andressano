package co.com.asl.firewall.entities;

import co.com.asl.firewall.entities.transform.CIDRTransformableSet;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Setter
@Getter
public class ASNumber extends CIDRTransformableSet implements Comparable<ASNumber> {

  public static final String REGEX_PATTERN = "(\\d{1,6})";
  public static final Predicate<String> PREDICATE = Pattern.compile(REGEX_PATTERN)
      .asMatchPredicate();
  private static final long serialVersionUID = 1L;
  private int number;

  public ASNumber(int number, String name) {
    setNumber(number);
    setName(name);
  }

  public ASNumber(int number) {
    this(number,"AS".concat(Integer.toString(number)));
  }

  public ASNumber(Collection<CIDRAddressV4> addresses) {
    super(addresses);
  }

  public ASNumber(String asn, Collection<CIDRAddressV4> addreses) {
    this(addreses);
    setNumber(Integer.parseInt(asn));
    setName(asn);
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
