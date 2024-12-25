package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.util.StringHelper;

public class InvalidModuleException
    extends RuntimeException
{
    public InvalidModuleException(Class<?> moduleType, String groupId, String moduleId)
    {
        super(
            "The module '%s' is invalid, because '%s' is not a valid module type (modules must extend '%s' or '%s')"
                .formatted(
                    StringHelper.joinAndWrapScopes(groupId, moduleId),
                    moduleType.getName(),
                    PlayerModule.class,
                    ConfigModule.class
                )
        );
    }
}
