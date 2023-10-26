package dte.tzevaadomapi.alert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a <b>Tzeva Adom</b> and provides information about it.
 */
public class Alert
{
	private final String city;
	private final String title;
	private final LocalDateTime date;
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public Alert(String city, String title, LocalDateTime date) 
	{
		this.city = city;
		this.title = title;
		this.date = date;
	}
	
	/**
	 * Returns the city where this alert happened.
	 * 
	 * @return The city.
	 */
	public String getCity() 
	{
		return this.city;
	}
	
	/**
	 * Describes the cause of this alert - what happened that had caused it to happen.
	 * 
	 * @return This alert's description.
	 */
	public String getTitle() 
	{
		return this.title;
	}
	
	/**
	 * Returns when this alert happened.
	 * 
	 * @return This alert's date.
	 */
	public LocalDateTime getDate() 
	{
		return this.date;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.city, this.title, this.date);
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(this == object)
			return true;
		
		if(!(object instanceof Alert))
			return false;
		
		Alert other = (Alert) object;
		
		return Objects.equals(this.city, other.city) && 
				Objects.equals(this.title, other.title) &&
				Objects.equals(this.date, other.date);
	}
	
	@Override
	public String toString()
	{
		return String.format("Alert [city=%s, title=%s, date=%s]", this.city, this.title, this.date.format(DATE_FORMATTER));
	}
}