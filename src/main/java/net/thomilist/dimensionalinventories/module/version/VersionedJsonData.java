package net.thomilist.dimensionalinventories.module.version;

import com.google.gson.JsonElement;

public record VersionedJsonData(int version, JsonElement data)
{ }
