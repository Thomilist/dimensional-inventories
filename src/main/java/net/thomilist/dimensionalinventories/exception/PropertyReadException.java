package net.thomilist.dimensionalinventories.exception;

public class PropertyReadException
    extends RuntimeException
{
    private static final String DEFAULT_MESSAGE = "Failed to read mod properties";

    public PropertyReadException()
    {
        this( PropertyReadException.DEFAULT_MESSAGE );
    }

    public PropertyReadException( final String message )
    {
        super( message );
    }

    public PropertyReadException( final Throwable cause )
    {
        this( PropertyReadException.DEFAULT_MESSAGE, cause );
    }

    public PropertyReadException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
