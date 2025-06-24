package dte.tzevaadomapi.alert.notifier;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import dte.tzevaadomapi.alert.Alert;

/** Keeps track of the Tzeva Adom alerts that an {@link AlertNotifier} has captured. */
public class AlertHistory implements Iterable<Alert>
{
	private final Deque<Alert> history = new LinkedList<>();

	void addAll(Collection<Alert> alerts)
	{
		this.history.addAll(alerts);
	}
	
	/**
	 * Returns the most recent alert in this history.
	 * 
	 * @return The most recent alert.
	 */
	public Optional<Alert> getMostRecent()
	{
		return Optional.ofNullable(this.history.peekLast());
	}
	
	/**
	 * Returns a list of the alerts that happened in the provided {@code region}.
	 *
	 * @param region The region, may be partial.
	 * @return The region's history.
	 * @implNote This method accepts partial regions - If "תל" is provided, the result could contain alerts from both <i>תל אביב</i> and <i>תל מונד</i>.
	 */
	public List<Alert> ofRegion(String region)
	{
		return this.history.stream()
				.filter(alert -> alert.region().contains(region))
				.collect(toList());
	}
	
	/**
	 * Returns a {@link Deque} snapshot of this history.
	 * 
	 * @return The deque.
	 */
	public Deque<Alert> toDeque()
	{
		return new LinkedList<>(this.history);
	}

	/**
	 * Returns how many alerts this history contains.
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