package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.util.StringHelper;

public class InvalidModuleException
    extends RuntimeException
{
    public InvalidModuleException(Class<?> moduleType, String groupId, String moduleId)
    {
        super
        (
            "The module "
            + StringHelper.joinAndWrapScopes(groupId, moduleId)
            + " is invalid, because '"
            + moduleType.getName()
            + "' is not a valid module type"
        );
    }
}
