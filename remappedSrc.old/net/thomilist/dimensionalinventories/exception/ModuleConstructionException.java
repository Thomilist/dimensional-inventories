package net.thomilist.dimensionalinventories.exception;

public class ModuleConstructionException
    extends RuntimeException
{
    public ModuleConstructionException( final Class<?> moduleType, final String groupId, final Throwable cause )
    {
        super(
            "The module '%s' from module group '%s' could not be constructed".formatted(
                moduleType.getName(),
                groupId
            ), cause
        );
    }
}
