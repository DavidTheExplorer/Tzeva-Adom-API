package dte.tzevaadomapi.alertsource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dte.tzevaadomapi.alert.Alert;

/**
 * Requests Alerts from the the website of Pikud Ha Oref.
 */
public class PHOAlertSource implements AlertSource
{
	private static final String REQUEST_URL = "https://www.oref.org.il/WarningMessages/History/AlertsHistory.json";
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	@Override
	public Alert getMostRecentAlert() throws Exception
	{
		JSONArray alertsJsonArray = requestAlertsJSON();
		
		if(alertsJsonArray == null || alertsJsonArray.isEmpty())
			return NO_RESPONSE;
		
		return parseAlert((JSONObject) alertsJsonArray.get(0));
	}
	
	private JSONArray requestAlertsJSON() throws Exception
	{
		try(CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(new HttpGet(REQUEST_URL))) 
		{
			HttpEntity entity = response.getEntity();
			String alertsJsonText = (entity != null ? EntityUtils.toString(entity) : null);
			
			return (JSONArray) JSONValue.parse(alertsJsonText);
		}
	}
	
	private Alert parseAlert(JSONObject alertJson) 
	{
		String city = (String) alertJson.get("data");
		String title = (String) alertJson.get("title");
		LocalDateTime date = LocalDateTime.parse((String) alertJson.get("alertDate"), DATE_FORMATTER);
		
		return new Alert(city, title, date);
	}
}