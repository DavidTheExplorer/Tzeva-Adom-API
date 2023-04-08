package dte.tzevaadomapi.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UncheckedExceptions
{
	public static Runnable unchecked(CheckedRunnable runnable)
	{
		return () -> 
		{
			try
			{
				runnable.run();
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier)
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
	
	public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer)
	{
		return object -> 
		{
			try
			{
				consumer.accept(object);
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T, R> Predicate<T> unchecked(CheckedPredicate<T> predicate)
	{
		return object -> 
		{
			try 
			{
				return predicate.test(object);
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	@FunctionalInterface
	public static interface CheckedRunnable
	{
		void run() throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedConsumer<T>
	{
		void accept(T object) throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedSupplier<T>
	{
		T get() throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedPredicate<T>
	{
		boolean test(T object) throws Exception;
	}
}
