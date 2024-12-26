package net.thomilist.dimensionalinventories.exception;

public class ModuleNotRegisteredException
    extends RuntimeException
{
    public <T> ModuleNotRegisteredException( final Class<T> moduleType )
    {
        super( "No module of type '%s' registered".formatted( moduleType.getName() ) );
    }

    public ModuleNotRegisteredException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
