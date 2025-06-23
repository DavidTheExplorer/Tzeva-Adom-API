package dte.tzevaadomapi.alert.provider;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Represents a provider that requires internet connection in order to access.
 * <p>
 * This class takes care of the essential connection details(the url, a possible proxy) and provides convenient connection methods.
 */
public abstract class OnlineAlertProvider implements AlertProvider
{
	private URL requestURL;
	private final Proxy proxy;

	protected OnlineAlertProvider(URL requestURL)
	{
		this(requestURL, Proxy.NO_PROXY);
	}

	protected OnlineAlertProvider(URL requestURL, Proxy proxy)
	{
		this.requestURL = requestURL;
		this.proxy = proxy;
	}

	public void changeRequestURL(URL requestURL)
	{
		this.requestURL = requestURL;
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