package dte.tzevaadomapi.extras;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.listener.TzevaAdomListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An implementation of {@link Consumer<Exception>} that handles a predetermined amount of exceptions, and then stops until the next Tzeva Adom;
 * This prevents unwanted behaviour such as logging the same exception endlessly(e.g. when the <i>AlertSource</i>'s server is offline).
 *
 * @implSpec To prevent swallowing 2 different exceptions, the limit is applied per <i>Exception Class</i> -
 * So for a limit of 5, only 5 <i>NullPointerException</i>s and 5 <i>IOException</i>s would be handled.
 */
public class LimitedExceptionHandler implements Consumer<Exception>, TzevaAdomListener
{
    private final Consumer<Exception> delegate;
    private final int limit;
    private final Map<Class<?>, Integer> timesHandled = new HashMap<>();

    public LimitedExceptionHandler(int limit, Consumer<Exception> delegate)
    {
        this.delegate = delegate;
        this.limit = limit;
    }

    @Override
    public void accept(Exception exception)
    {
        this.timesHandled.merge(exception.getClass(), 1, Integer::sum);

        if(this.timesHandled.get(exception.getClass()) <= this.limit)
            this.delegate.accept(exception);
    }

    @Override
    public void onTzevaAdom(Alert alert)
    {
        this.timesHandled.clear();
    }
}
