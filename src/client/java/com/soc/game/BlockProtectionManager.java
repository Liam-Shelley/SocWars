package com.soc.game;

import com.soc.lib.SparseVoxelOctree;
import com.soc.networking.s2c.BlockProtectionPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

    public int getBlockOutlineColour(BlockPos pos, int def) {
        final HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            int color = 0xcc222222;

            if (this.isBlockProtected(pos)) {
                color += 0x00cc0000;
            }
            if (this.isBlockProtected(pos.offset(((BlockHitResult)hitResult).getSide()))) {
                color += 0x0000cc00;
            }
            return color;
        }
        return def;
    }
}
