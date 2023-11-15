package dte.tzevaadomapi.alertsource;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Represents a source that requires internet connection in order to access. 
 * <p>
 * This class holds the essential connection details(the url, a possible proxy, etc) and provides convenient connection methods.
 */
public abstract class OnlineAlertSource implements AlertSource
{
	private final URL requestURL;
	private final Proxy proxy;
	
	public OnlineAlertSource(URL requestURL)
	{
		this(requestURL, Proxy.NO_PROXY);
	}
	
	public OnlineAlertSource(URL requestURL, Proxy proxy) 
	{
		this.requestURL = requestURL;
		this.proxy = proxy;
	}
	
	protected URLConnection openConnection() throws IOException 
	{
		return this.requestURL.openConnection(this.proxy);
	}
	
	protected InputStreamReader newInputStreamReader() throws IOException 
	{
		return new InputStreamReader(openConnection().getInputStream(), UTF_8);
	}
}