package net.thomilist.dimensionalinventories.module.base;

public interface StatefulModule<T extends ModuleState>
    extends Module
{
    T state();
    T defaultState();
}
