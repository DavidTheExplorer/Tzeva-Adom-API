package dte.tzevaadomapi.utils;

@FunctionalInterface
public interface CheckedFunction<T, R>
{
    R apply(T object) throws Exception;
}
