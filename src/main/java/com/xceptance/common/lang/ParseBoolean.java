package com.xceptance.common.lang;

import com.xceptance.common.util.XltCharBuffer;

public class ParseBoolean 
{
	/**
	 * Parses chars and evaluates if this is a boolean. Anything that is not true or TRUE
	 * or anything like True will evaluate to false
	 */
	public static boolean parse(final XltCharBuffer b)
	{
		// length is incorrect, it is false
		if (b.length() != 4)
		{
			return false;
		}
		
		// it is length 4, safe here
		final char t = b.get(0);
		final char r = b.get(1);
		final char u = b.get(2);
		final char e = b.get(3);
		
		// fastpath and slowpath
		final boolean b1 = (t == 't' & r == 'r' & u == 'u' & e == 'e');
		
		return b1 ? true : ((t == 't' || t == 'T') && (r == 'r' || r == 'R') && (u == 'u' || u == 'U') && (e == 'e' || e == 'E'));
	}
}
