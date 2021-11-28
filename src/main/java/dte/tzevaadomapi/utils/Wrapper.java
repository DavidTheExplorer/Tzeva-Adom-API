package dte.tzevaadomapi.utils;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class Wrapper<T>
{
	private T object;
	
	public Wrapper(T object) 
	{
		set(object);
	}
	
	public T get() 
	{
		return this.object;
	}
	
	public void set(T object) 
	{
		this.object = Objects.requireNonNull(object);
	}
	
	public void merge(UnaryOperator<T> merger) 
	{
		T newObject = merger.apply(this.object);
		set(newObject);
	}
}
