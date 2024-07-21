package net.thomilist.dimensionalinventories.exception;

public class ModuleNotRegisteredException
    extends RuntimeException
{
    public <T> ModuleNotRegisteredException(Class<T> moduleType)
    {
        super("No module of type '" + moduleType.getName() + "' registered");
    }

    public ModuleNotRegisteredException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
