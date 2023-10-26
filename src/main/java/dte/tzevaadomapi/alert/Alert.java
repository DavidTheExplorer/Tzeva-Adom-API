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
	private final String region;
	private final String title;
	private final LocalDateTime date;
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public Alert(String region, String title, LocalDateTime date) 
	{
		this.region = region;
		this.title = title;
		this.date = date;
	}
	
	/**
	 * Returns the region where this alert happened.
	 * 
	 * @return This alert's region.
	 */
	public String getRegion() 
	{
		return this.region;
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
		return Objects.hash(this.region, this.title, this.date);
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(this == object)
			return true;
		
		if(!(object instanceof Alert))
			return false;
		
		Alert other = (Alert) object;
		
		return Objects.equals(this.region, other.region) && 
				Objects.equals(this.title, other.title) &&
				Objects.equals(this.date, other.date);
	}
	
	@Override
	public String toString()
	{
		return String.format("Alert [region=%s, title=%s, date=%s]", this.region, this.title, this.date.format(DATE_FORMATTER));
	}
}