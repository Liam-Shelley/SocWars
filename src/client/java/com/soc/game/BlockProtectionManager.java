package com.soc.game;

import com.soc.lib.SparseVoxelOctree;
import com.soc.networking.s2c.BlockProtectionPayload;
import net.minecraft.util.math.BlockPos;

public class BlockProtectionManager {
    public static final BlockProtectionManager INSTANCE = new BlockProtectionManager();

    private SparseVoxelOctree<Boolean> overlay;
    private BlockPos origin;

    private BlockProtectionManager() {}

    public void setBlockProtection(BlockProtectionPayload packet) {
        this.overlay = packet.blockProtectionOverlay();
        this.origin = packet.origin();
    }

    public boolean isBlockProtected(BlockPos pos) {
        if (this.overlay == null) return false;

        return this.overlay.get(pos, this.origin);
    }
}
