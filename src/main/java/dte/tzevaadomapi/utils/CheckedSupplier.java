package dte.tzevaadomapi.utils;

@FunctionalInterface
public interface CheckedSupplier<T>
{
    T get() throws Exception;
}
