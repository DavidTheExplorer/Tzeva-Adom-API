package dte.tzevaadomapi.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFactory 
{
	public static URL of(String url) 
	{
		try 
		{
			return new URL(url);
		}
		catch(MalformedURLException exception) 
		{
			throw new RuntimeException(exception);
		}
	}
}
