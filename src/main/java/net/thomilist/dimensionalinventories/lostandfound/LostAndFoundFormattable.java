package net.thomilist.dimensionalinventories.lostandfound;

public interface LostAndFoundFormattable
{
    default String toLostAndFoundScopeString()
    {
        return this.toString();
    }
}
