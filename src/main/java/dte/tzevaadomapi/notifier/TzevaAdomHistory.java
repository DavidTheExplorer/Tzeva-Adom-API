package dte.tzevaadomapi.notifier;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import dte.tzevaadomapi.alert.Alert;

public class TzevaAdomHistory implements Iterable<Alert>
{
	private final Deque<Alert> history = new LinkedList<>();
	
	/**
	 * Records a collection of alerts.
	 * 
	 * @param alerts The alerts.
	 */
	void update(Collection<Alert> alerts)
	{
		this.history.addAll(alerts);
	}
	
	/**
	 * Returns the most recent alert in the history of the underlying notifier.
	 * 
	 * @return The most recent alert.
	 */
	public Optional<Alert> getMostRecent()
	{
		return Optional.ofNullable(this.history.peekLast());
	}
	
	/**
	 * Returns the list of alerts that happened in a provided {@code region} since the underlying notifier was started.
	 * 
	 * @param region The region, may be partial.
	 * @return The region's history.
	 */
	public List<Alert> ofRegion(String region)
	{
		return this.history.stream()
				.filter(alert -> alert.getRegion().contains(region))
				.collect(toList());
	}
	
	/**
	 * Returns a {@link Deque} representation of this history.
	 * 
	 * @return The deque.
	 */
	public Deque<Alert> toDeque()
	{
		return new LinkedList<>(this.history);
	}

	/**
	 * Returns how many alerts were recorded since the underlying notifier was started.
	 * 
	 * @return This history's size.
	 */
	public int size() 
	{
		return this.history.size();
	}

	@Override
	public Iterator<Alert> iterator() 
	{
		return this.history.iterator();
	}
}