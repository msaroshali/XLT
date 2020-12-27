package com.xceptance.xlt.engine.util;

import java.util.Arrays;

/**
 * This is a wrapper around string to cache the hashcode and 
 * change the equals comparison. This is a perfect candidate for Valhalla's
 * inline classes
 *  
 * @author rschwietzke
 *
 */
public class FastString
{
    private final int hashCode;
    private final String data;
    private final char[] charArray;
    
    public FastString(final String data, int hashCode)
    {
        this.data = data;
        this.hashCode = hashCode;
        this.charArray = data.toCharArray();
    }
    
    public FastString(final String data)
    {
        this.data = data;
        this.hashCode = data.hashCode();
        this.charArray = data.toCharArray();
    }
    
    @Override
    public String toString()
    {
        return data;
    }
    
    @Override
    public int hashCode()
    {
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }

        FastString other = (FastString) obj;
        if (hashCode != obj.hashCode())
        {
            return false;
        }
        
        return Arrays.equals(charArray, other.charArray);
    }
    
    
}
