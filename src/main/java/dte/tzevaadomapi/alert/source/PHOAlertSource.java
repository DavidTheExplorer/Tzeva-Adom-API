package dte.tzevaadomapi.alert.source;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.json.simple.JSONObject;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alert.source.json.JSONAlertSource;

/**
 * Requests Alerts from the the website of Pikud Ha Oref.
 */
public class PHOAlertSource extends JSONAlertSource
{
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public PHOAlertSource(String phoRequestURL)
	{
		super(phoRequestURL);
	}
	
	@Override
	protected Alert fromJSON(JSONObject alertJSON) 
	{
		String city = (String) alertJSON.get("data");
		LocalDateTime date = LocalDateTime.parse((String) alertJSON.get("alertDate"), DATE_FORMATTER);
		
		return new Alert(city, date);
	}
}