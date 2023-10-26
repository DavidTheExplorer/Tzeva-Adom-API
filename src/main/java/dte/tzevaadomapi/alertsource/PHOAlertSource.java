package dte.tzevaadomapi.alertsource;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.utils.URLFactory;
import dte.tzevaadomapi.utils.UncheckedExceptions.CheckedFunction;

/**
 * Requests Alerts from the the website of Pikud Ha Oref.
 */
public class PHOAlertSource implements AlertSource
{
	private static final URL REQUEST_URL = URLFactory.of("https://www.oref.org.il/WarningMessages/History/AlertsHistory.json");
	
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Alert.class, new AlertDeserializer())
			.create();
	
	@Override
	public Alert getMostRecentAlert() throws Exception
	{
		//read the first alert in the list
		return readJsonArray(reader -> GSON.fromJson(reader, Alert.class));
	}
	
	@Override
	public Deque<Alert> getSince(Alert alert) throws Exception
	{
		return readJsonArray(reader -> 
		{
			Deque<Alert> result = new LinkedList<>();

			while(reader.hasNext()) 
			{
				Alert nextAlert = GSON.fromJson(reader, Alert.class);

				//efficiency - stop when the provided alert is encountered
				if(nextAlert.equals(alert)) 
					break;

				result.add(nextAlert);
			}

			return result;
		});
	}
	
	//starts reading the JSON list posted by Pikud Ha'oref, and applies the function on it
	private <T> T readJsonArray(CheckedFunction<JsonReader, T> resultParser) 
	{
		try(JsonReader reader = new JsonReader(new InputStreamReader(REQUEST_URL.openStream(), UTF_8)))
		{
			reader.beginArray();

			return resultParser.apply(reader);
		}
		catch(Exception exception) 
		{
			throw new RuntimeException(String.format("Could not contact Pikud Ha'oref due to %s", exception));
		}
	}
	
	
	
	private static class AlertDeserializer implements JsonDeserializer<Alert>
	{
		private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

		@Override
		public Alert deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			JsonObject object = json.getAsJsonObject();
			String city = object.get("data").getAsString();
			String title = object.get("title").getAsString();
			LocalDateTime date = LocalDateTime.parse(object.get("alertDate").getAsString(), DATE_FORMATTER);

			return new Alert(city, title, date);
		}
	}
}