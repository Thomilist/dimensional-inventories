package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.util.LogHelper;

public class ModuleConstructionException
    extends RuntimeException
{
    public ModuleConstructionException(Class<?> moduleType, String groupId, String moduleId, Throwable cause)
    {
        super
        (
            "The module "
                + LogHelper.joinAndWrapScopes(groupId, moduleId)
                + " of type '"
                + moduleType.getName()
                + "' could not be constructed",
            cause
        );
    }
}
