package net.thomilist.dimensionalinventories.exception;

public class ModuleConstructionException
    extends RuntimeException
{
    public ModuleConstructionException(Class<?> moduleType, String groupId, Throwable cause)
    {
        super(
            "The module '%s' from module group '%s' could not be constructed"
                .formatted(moduleType.getName(), groupId),
            cause
        );
    }
}
