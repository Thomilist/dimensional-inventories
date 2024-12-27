package net.thomilist.dimensionalinventories.compatibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class, interface or method is not compatible with all versions of e.g. Java or Minecraft that the
 * project as a whole supports.
 */
@Target( { ElementType.TYPE, ElementType.METHOD } )
@Retention( RetentionPolicy.CLASS )
public @interface LimitedCompatibility
{
    /**
     * The target that the version range applies to, e.g. "Java" or "Minecraft".
     *
     * @return the target that the version range applies to
     */
    String target();

    /**
     * The range of versions that the annotated class, interface or method is compatible with, e.g. ">=17" or ">=1.20 .1
     * <1.21"
     *
     * @return the range of compatible versions
     */
    String versions();
}
