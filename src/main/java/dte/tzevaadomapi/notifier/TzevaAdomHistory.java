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
	
	public void update(Alert alert) 
	{
		this.history.add(alert);
	}
	
	public void update(Collection<Alert> alerts)
	{
		this.history.addAll(alerts);
	}
	
	public Optional<Alert> getMostRecent()
	{
		return Optional.ofNullable(this.history.peekLast());
	}
	
	public List<Alert> ofCity(String city)
	{
		return this.history.stream()
				.filter(alert -> alert.getCity().contains(city))
				.collect(toList());
	}
	
	public Deque<Alert> toDeque()
	{
		return new LinkedList<>(this.history);
	}

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