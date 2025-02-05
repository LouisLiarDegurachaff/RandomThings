package org.louis.randomthings.core.block;

import net.minecraft.world.level.block.SoundType;
import org.louis.randomthings.base.BaseBlock;

public class BlockMachineBlock extends BaseBlock {
    public BlockMachineBlock() {
        super(properties -> properties.sound(SoundType.METAL).strength(2,6));
    }
}
