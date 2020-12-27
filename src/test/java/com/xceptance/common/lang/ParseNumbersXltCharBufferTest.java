package com.xceptance.common.lang;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.common.util.XltCharBuffer;

/**
 * Test for parsing longs and ints.
 * 
 * @author René Schwietzke (Xceptance Software Technologies GmbH)
 */
public class ParseNumbersXltCharBufferTest
{
    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseLong(java.lang.String)}.
     */
    @Test
    public final void testParseLong()
    {
        {
            final String s = "1670036109465868";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "0";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "1670036";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseLong(java.lang.String)}.
     */
    @Test
    public final void testParseLongFallback()
    {
        {
            final String s = "-1670036109465868";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "-0";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "-1670036";
            Assert.assertEquals((long) Long.valueOf(s), ParseNumbers.parseLong(XltCharBuffer.valueOf(s)));
        }
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test
    public final void testParseInt()
    {
        {
            final String s = "1670036108";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "0";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "1670036";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test
    public final void testParseIntFallback()
    {
        {
            final String s = "-1670036108";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "-0";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
        {
            final String s = "-1670036";
            Assert.assertEquals((int) Integer.valueOf(s), ParseNumbers.parseInt(XltCharBuffer.valueOf(s)));
        }
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionInt_Empty()
    {
        final String s = "";
        ParseNumbers.parseInt(XltCharBuffer.valueOf(s));
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionInt_Space()
    {
        final String s = " ";
        ParseNumbers.parseInt(XltCharBuffer.valueOf(s));
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionInt_WrongCharacter()
    {
        final String s = "aaa";
        ParseNumbers.parseInt(XltCharBuffer.valueOf(s));
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionLong_Empty()
    {
        final String s = "";
        ParseNumbers.parseLong(XltCharBuffer.valueOf(s));
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionLong_Space()
    {
        final String s = " ";
        ParseNumbers.parseLong(XltCharBuffer.valueOf(s));
    }

    /**
     * Test method for {@link com.xceptance.common.ParseNumbers.FastParseNumbers#fastParseInt(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public final void testNumberFormatExceptionLong_WrongCharacter()
    {
        final String s = "aaa";
        ParseNumbers.parseLong(XltCharBuffer.valueOf(s));
    }

    // ================================================================
    // Double
    
    @Test
    public void doubleHappyPath()
    {
        Assert.assertEquals("1.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("1"))));
        Assert.assertEquals("1.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("1.0"))));
        Assert.assertEquals("1.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("1.000"))));
        Assert.assertEquals("10.100000000000001", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("10.1"))));
        Assert.assertEquals("2.2", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("2.2"))));
        Assert.assertEquals("2.222", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("2.222"))));
        Assert.assertEquals("112.255", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("112.255"))));
        Assert.assertEquals("44112.222", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("44112.222"))));
        Assert.assertEquals("0.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("0"))));
        Assert.assertEquals("0.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("0.0"))));
        Assert.assertEquals("-1.0", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("-1"))));
        Assert.assertEquals("0.2", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("0.2"))));
        Assert.assertEquals("100.10000000000001", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("100.1"))));
        Assert.assertEquals("1000.1", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("1000.1"))));
        Assert.assertEquals("10000.1", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("10000.1"))));
        Assert.assertEquals("100000.1", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("100000.1"))));

        Assert.assertEquals("0.25", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("0.25"))));
        Assert.assertEquals("10.25", String.valueOf(ParseNumbers.parseDouble(XltCharBuffer.valueOf("10.25"))));
    }
}
