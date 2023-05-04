package net.thomilist.dimensionalinventories;

public class LogThrottler {
    private Integer counter = 0;
    private Integer factor = 0;

    public LogThrottler(Integer throttlingFactor)
    {
        factor = throttlingFactor;
    }

    public boolean get()
    {
        return counter++ % factor == 0;
    }
}
