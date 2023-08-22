package dte.tzevaadomapi.alert;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a <b>Tzeva Adom</b> alert and contains information about where/when it happened.
 */
public class Alert
{
	private final String city;
	private final String title;
	private final LocalDateTime date;
	
	public Alert(String city, String title, LocalDateTime date) 
	{
		this.city = city;
		this.title = title;
		this.date = date;
	}
	
	public String getCity() 
	{
		return this.city;
	}
	
	public String getTitle() 
	{
		return this.title;
	}
	
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
		return String.format("Alert [city=%s, title=%s, date=%s]", this.city, this.title, this.date);
	}
}