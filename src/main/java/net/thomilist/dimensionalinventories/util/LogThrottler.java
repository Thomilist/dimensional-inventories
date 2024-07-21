package net.thomilist.dimensionalinventories.util;

public class LogThrottler
{
    private Integer counter = 0;
    private final Integer factor;

    public LogThrottler(Integer throttlingFactor)
    {
        factor = throttlingFactor;
    }

    public boolean get()
    {
        return counter++ % factor == 0;
    }
}
