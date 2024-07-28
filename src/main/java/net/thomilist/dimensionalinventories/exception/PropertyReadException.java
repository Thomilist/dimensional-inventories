package net.thomilist.dimensionalinventories.exception;

public class PropertyReadException extends Exception
{
    private static final String DEFAULT_MESSAGE = "Failed to read mod properties";

    public PropertyReadException()
    {
        this(PropertyReadException.DEFAULT_MESSAGE);
    }

    public PropertyReadException(Throwable cause)
    {
        this(PropertyReadException.DEFAULT_MESSAGE, cause);
    }

    public PropertyReadException(String message)
    {
        super(message);
    }

    public PropertyReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
