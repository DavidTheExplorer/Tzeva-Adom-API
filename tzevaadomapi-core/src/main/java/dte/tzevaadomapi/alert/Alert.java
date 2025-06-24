package dte.tzevaadomapi.alert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Tzeva Adom alert and provides information about it.
 *
 * @param region The approximate region where this alert happened.
 * @param description The description of the event that caused this alert.
 * @param date When this alert happened.
 */
public record Alert(@SerializedName("data") String region,
					@SerializedName("title") String description,
					@SerializedName("alertDate") LocalDateTime date)
{
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

	@Override
	public String toString()
	{
		return "Alert [region=%s, description=%s, date=%s]".formatted(this.region, this.description, this.date.format(DATE_FORMATTER));
	}
}