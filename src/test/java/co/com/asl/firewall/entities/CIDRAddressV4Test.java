package co.com.asl.firewall.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class CIDRAddressV4Test {

  @Test
  void getIp() {
  }

  @Test
  void setIp() {
  }

  @Test
  void getMask() {
  }

  @Test
  void setMask() {
  }

  @Test
  void testEquals() {
  }

  @Test
  void testHashCode() {
  }

  @Test
  void compareTo() {
  }

  @Test
  void combineIfPossible() {
    CIDRAddressV4 cidr1 = new CIDRAddressV4(192,168,2,0,24);
    CIDRAddressV4 cidr2 = new CIDRAddressV4(192,168,3,0,24);
    Optional<CIDRAddressV4> combined = CIDRAddressV4.combine(cidr1, cidr2);
    CIDRAddressV4 total = new CIDRAddressV4(192,168,2,0,23);
    assertEquals(total, combined.get());
  }

  @Test
  void canAbsorb() {
    CIDRAddressV4 cidr1 = new CIDRAddressV4(192,168,0,0,16);
    CIDRAddressV4 cidr2 = new CIDRAddressV4(192,168,255,255);
    assertTrue(cidr1.canAbsorb(cidr2));
  }

  @Test
  void testToString() {
  }
}