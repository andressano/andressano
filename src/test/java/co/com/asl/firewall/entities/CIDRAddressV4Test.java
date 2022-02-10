package co.com.asl.firewall.entities;

import com.sun.source.tree.Tree;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CIDRAddressV4Test {

    @Test
    public void rangeContains() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("199.96.0.0/16");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("199.96.62.0/23");
        assertTrue(cidrAddressV4.contains(cidrAddressV4Comp));
    }

    @Test
    public void rangeNotContains() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("128.1.0.0/16");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("216.201.80.0/20");
        assertFalse(cidrAddressV4.contains(cidrAddressV4Comp));
    }

    @Test
    public void isNotNext() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("171.208.0.0/12");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("114.224.0.0/12");
        Optional<CIDRAddressV4> combinedCidr = cidrAddressV4.combineIfPossible(cidrAddressV4Comp);
        assertFalse(combinedCidr.isPresent());
    }

    @Test
    public void isNotNext2() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("223.203.104.0/22");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("223.203.100.0/22");
        Optional<CIDRAddressV4> combinedCidr = cidrAddressV4.combineIfPossible(cidrAddressV4Comp);
        assertFalse(combinedCidr.isPresent());
    }

    @Test
    public void isNext2() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("211.156.96.0/20");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("211.156.112.0/20");
        Optional<CIDRAddressV4> combinedCidr = cidrAddressV4.combineIfPossible(cidrAddressV4Comp);
        assertEquals("211.156.96.0/19", combinedCidr.get().toString());
    }

    @Test
    public void isNext3() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("139.202.0.0/16");
        CIDRAddressV4 cidrAddressV4Comp = new CIDRAddressV4("139.203.0.0/16");
        Optional<CIDRAddressV4> combinedCidr = cidrAddressV4.combineIfPossible(cidrAddressV4Comp);
        assertEquals("139.202.0.0/15", combinedCidr.get().toString());
    }

    @Test
    public void compareToTest() {
        List<CIDRAddressV4> test = new ArrayList<>();
        test.add(new CIDRAddressV4("1.1.8.0/24"));
        test.add(new CIDRAddressV4("61.160.0.0/14"));
        test.add(new CIDRAddressV4("104.37.184.0/21"));
        test.add(new CIDRAddressV4("223.255.0.0/18"));

        Collections.sort(test);
        Iterator<CIDRAddressV4> iterator = test.iterator();
        assertEquals("1.1.8.0/24", iterator.next().toString());
        assertEquals("61.160.0.0/14", iterator.next().toString());
        assertEquals("104.37.184.0/21", iterator.next().toString());
        assertEquals("223.255.0.0/18", iterator.next().toString());
    }

    @Test
    public void toStringTest() {
        CIDRAddressV4 cidrAddressV4 = new CIDRAddressV4("211.156.96.14/31");
        assertTrue("211.156.96.14/31".equals(cidrAddressV4.toString()));
    }
}
