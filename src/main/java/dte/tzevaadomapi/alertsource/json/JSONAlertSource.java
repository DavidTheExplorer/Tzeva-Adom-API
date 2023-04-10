package dte.tzevaadomapi.alertsource.json;

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
import dte.tzevaadomapi.alertsource.AlertSource;

/**
 * Requests Alerts from an API that returns a JSON response of a list of Alerts.
 */
public abstract class JSONAlertSource implements AlertSource
{
	private final String requestURL;
	
	protected JSONAlertSource(String requestURL) 
	{
		this.requestURL = requestURL;
	}
	
	@Override
	public Alert getMostRecentAlert() throws Exception
	{
		JSONArray alertsJsonArray = requestAlertsJSON();
		
		if(alertsJsonArray == null || alertsJsonArray.isEmpty())
			throw new IllegalArgumentException("Cannot get the most recent alert due to an empty JSON response!");
		
		return fromJSON((JSONObject) alertsJsonArray.get(0));
	}
	
	protected abstract Alert fromJSON(JSONObject alertJson);
	
	private JSONArray requestAlertsJSON() throws Exception
	{
		try(CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(new HttpGet(this.requestURL))) 
		{
			HttpEntity entity = response.getEntity();
			String alertsJsonText = (entity != null ? EntityUtils.toString(entity) : null);
			
			return (JSONArray) JSONValue.parse(alertsJsonText);
		}
	}
}