package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonData;

import java.util.Optional;

public final class ShoulderEntityModule
    extends ModuleBase
    implements JsonPlayerModule<ShoulderEntityModuleState>
{
    private static final String MODULE_ID = "shoulder-entity";
    private static final String DESCRIPTION = "Shoulder entities - just parrots, at least for now.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER.create();

    private final ShoulderEntityModuleState state = new ShoulderEntityModuleState();

    public ShoulderEntityModule( final String groupId )
    {
        super(
            ShoulderEntityModule.STORAGE_VERSIONS,
            groupId,
            ShoulderEntityModule.MODULE_ID,
            ShoulderEntityModule.DESCRIPTION
        );
    }

    private static Optional<ParrotEntity.Variant> getVariant( final NbtCompound nbt )
    {
        return nbt.getInt( "Variant" )
            .map( ParrotEntity.Variant::byIndex );
    }

    @Override
    public int moduleVersion()
    {
        return 2;
    }

    @Override
    public Gson gson()
    {
        return ShoulderEntityModule.GSON;
    }

    @Override
    public ShoulderEntityModuleState loadVersionedData( final VersionedJsonData versionedData )
        throws JsonParseException
    {
        return switch ( versionedData.version() )
        {
            case 1 -> this.loadAsVersion1( versionedData );
            default -> JsonPlayerModule.super.loadVersionedData( versionedData );
        };
    }

    @Override
    public ShoulderEntityModuleState newInstance( final ServerPlayerEntity player )
    {
        return new ShoulderEntityModuleState( player );
    }

    @Override
    public ShoulderEntityModuleState state()
    {
        return this.state;
    }

    @Override
    public ShoulderEntityModuleState defaultState()
    {
        return new ShoulderEntityModuleState();
    }

    private ShoulderEntityModuleState loadAsVersion1( final VersionedJsonData versionedData )
    {
        final ShoulderEntityModuleState state = this.gson().fromJson( versionedData.data(), this.state().type() );
        state.leftShoulderParrotVariant = ShoulderEntityModule.getVariant( state.leftShoulderEntity );
        state.rightShoulderParrotVariant = ShoulderEntityModule.getVariant( state.rightShoulderEntity );
        return state;
    }
}
