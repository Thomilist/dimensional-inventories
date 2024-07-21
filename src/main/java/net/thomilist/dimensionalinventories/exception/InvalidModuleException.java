package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.util.LogHelper;

public class InvalidModuleException
    extends RuntimeException
{
    public InvalidModuleException(Class<?> moduleType, String groupId, String moduleId)
    {
        super
        (
            "The module "
            + LogHelper.joinAndWrapScopes(groupId, moduleId)
            + " is invalid, because '"
            + moduleType.getName()
            + "' is not a valid module type"
        );
    }
}
