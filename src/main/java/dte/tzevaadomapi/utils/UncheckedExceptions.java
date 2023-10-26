package dte.tzevaadomapi.utils;

import java.util.function.Function;
import java.util.function.Supplier;

public class UncheckedExceptions 
{
	public static <T> Supplier<T> uncheckedGet(CheckedSupplier<T> supplier) 
	{
		return () -> 
		{
			try 
			{
				return supplier.get();
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T, R> Function<T, R> uncheckedApply(CheckedFunction<T, R> function)
	{
		return object -> 
		{
			try 
			{
				return function.apply(object);
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	@FunctionalInterface
	public static interface CheckedSupplier<T>
	{
		T get() throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedFunction<T, R>
	{
		R apply(T object) throws Exception;
	}
}
