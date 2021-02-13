package com.xceptance.common.lang;

import org.junit.Test;

import com.xceptance.common.util.XltCharBuffer;

import static org.junit.Assert.*;

public class ParseBooleanTest 
{
	@Test
	public void normal()
	{
		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("true")));
		assertFalse(ParseBoolean.parse(XltCharBuffer.valueOf("false")));
		assertFalse(ParseBoolean.parse(XltCharBuffer.valueOf("trueish")));
		assertFalse(ParseBoolean.parse(XltCharBuffer.valueOf("wahr")));

		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("TRUE")));
		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("truE")));
		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("True")));
		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("tRue")));
		assertTrue(ParseBoolean.parse(XltCharBuffer.valueOf("trUe")));

	}
}
