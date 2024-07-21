package net.thomilist.dimensionalinventories.module.builtin.pool;

// To provide better command feedback
public record DimensionPoolOperationResult(
    DimensionPoolOperation request,
    DimensionPoolOperation operation,
    String target,
    String from,
    String to,
    boolean success)
{ }
