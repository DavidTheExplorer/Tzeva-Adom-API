package dte.tzevaadomapi.alert.provider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.utils.URLFactory;
import dte.tzevaadomapi.utils.CheckedFunction;

/**
 * Requests Alerts from Pikud Ha'Oref.
 */
public class PHOAlertProvider extends OnlineAlertProvider
{
	private static final URL REQUEST_URL = URLFactory.of("https://www.oref.org.il/warningMessages/alert/History/AlertsHistory.json");
	
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
			.create();
	
	/**
	 * Creates a provider based on Pikud Ha'oref, without any proxy involved.
	 */
	public PHOAlertProvider()
	{
		super(REQUEST_URL);
	}
	
	/**
	 * Creates a provider based on Pikud Ha'oref that uses the provided {@code proxy} to connect.
	 * <p>
	 * * Use this constructor when the library is used <i>outside</i> of Israel.
	 * 
	 * @param proxy The proxy to use when connecting.
	 */
	public PHOAlertProvider(Proxy proxy)
	{
		super(REQUEST_URL, proxy);
	}
	
	@Override
	public Alert getMostRecentAlert()
	{
		//read the first alert in the list
		return beginReadingArray(reader -> GSON.fromJson(reader, Alert.class));
	}
	
	@Override
	public Deque<Alert> getSince(Alert alert)
	{
		return beginReadingArray(reader -> 
		{
			Deque<Alert> result = new LinkedList<>();

			while(reader.hasNext()) 
			{
				Alert nextAlert = GSON.fromJson(reader, Alert.class);
				
				//stop when the provided alert is reached
				if(nextAlert.equals(alert)) 
					break;

				result.add(nextAlert);
			}

			return result;
		});
	}
	
	//starts reading the JSON array posted by Pikud Ha'oref, and applies the function on it
	private <T> T beginReadingArray(CheckedFunction<JsonReader, T> resultParser)
	{
		try(JsonReader reader = new JsonReader(newInputStreamReader()))
		{
			try
			{
				reader.beginArray();
			}
			catch(IOException exception)
			{
				return null;
			}

			return resultParser.apply(reader);
		}
		catch(Exception exception) 
		{
			throw new RuntimeException("Could not contact Pikud Ha'oref", exception);
		}
	}
	
	
	
	private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime>
	{
		@Override
		public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			return LocalDateTime.parse(json.getAsString(), Alert.DATE_FORMATTER);
		}
	}
}