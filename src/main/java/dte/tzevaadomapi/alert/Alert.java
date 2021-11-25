package dte.tzevaadomapi.alert;

import java.time.LocalDateTime;
import java.util.Objects;

public class Alert
{
	private final String city;
	private final LocalDateTime date;
	
	public Alert(String city, LocalDateTime date) 
	{
		this.city = city;
		this.date = date;
	}
	
	public String getCity() 
	{
		return this.city;
	}
	
	public LocalDateTime getDate() 
	{
		return this.date;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.city, this.date);
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(this == object)
			return true;
		
		if(!(object instanceof Alert))
			return false;
		
		Alert other = (Alert) object;
		
		return Objects.equals(this.city, other.city) && Objects.equals(this.date, other.date);
	}
	
	@Override
	public String toString()
	{
		return String.format("Alert [city=%s, date=%s]", this.city, this.date);
	}
}