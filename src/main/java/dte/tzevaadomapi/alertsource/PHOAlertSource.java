package dte.tzevaadomapi.alertsource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.json.simple.JSONObject;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.json.JSONAlertSource;

/**
 * Requests Alerts from the the website of Pikud Ha Oref.
 */
public class PHOAlertSource extends JSONAlertSource
{
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public PHOAlertSource()
	{
		super("https://www.oref.org.il/WarningMessages/History/AlertsHistory.json");
	}
	
	@Override
	protected Alert fromJSON(JSONObject alertJson) 
	{
		String city = (String) alertJson.get("data");
		String title = (String) alertJson.get("title");
		LocalDateTime date = LocalDateTime.parse((String) alertJson.get("alertDate"), DATE_FORMATTER);
		
		return new Alert(city, title, date);
	}
}