package dte.tzevaadomapi.extras;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alert.notifier.AlertListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An implementation of {@link Consumer<Exception>} that handles a predetermined amount of exceptions, and then stops until the next Tzeva Adom;
 * This prevents unwanted behaviour such as spam-logging the same exception endlessly(e.g. when the <i>AlertProvider</i>'s server is offline).
 *
 * @implSpec To prevent swallowing 2 different exceptions, the limit is applied per <i>Exception Class</i> -
 * So for a limit of 5, only 5 <i>NullPointerException</i>s and 5 <i>IOException</i>s would be handled.
 */
public class LimitedExceptionHandler implements Consumer<Exception>, AlertListener
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
        int timesHandled = this.timesHandled.getOrDefault(exception.getClass(), 0);

        if(timesHandled >= this.limit)
            return;

        this.timesHandled.put(exception.getClass(), ++timesHandled);
        this.delegate.accept(exception);
    }

    @Override
    public void onTzevaAdom(Alert alert)
    {
        this.timesHandled.clear();
    }
}
