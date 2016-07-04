package slimeknights.mantle.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IServantLogic {

  BlockPos getMasterPosition();

  /** The block should already have a valid master */
  void notifyMasterOfChange();

  /**
   * Checks if this block can be tied to this master
   *
   * @param world the world of master
   * @param pos   position of master
   * @return whether the servant can be tied to this master
   */

  boolean setPotentialMaster(IMasterLogic master, World world, BlockPos pos);

  /**
   * Used to set and verify that this is the block's master
   *
   * @param pos position of master
   * @return Is this block tied to this master?
   */

  boolean verifyMaster(IMasterLogic master, World world, BlockPos pos);

  /**
   * Exactly what it says on the tin
   *
   * @param pos position of master
   */

  void invalidateMaster(IMasterLogic master, World world, BlockPos pos);
}
