package net.thomilist.dimensionalinventories.util;

public class LogThrottler
{
    private final Integer factor;
    private Integer counter = 0;

    public LogThrottler( final Integer throttlingFactor )
    {
        this.factor = throttlingFactor;
    }

    public boolean get()
    {
        return (this.counter++ % this.factor) == 0;
    }
}
