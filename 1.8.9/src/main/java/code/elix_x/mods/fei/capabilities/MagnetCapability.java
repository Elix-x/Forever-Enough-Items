package code.elix_x.mods.fei.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MagnetCapability {

	@CapabilityInject(MagnetCapability.class)
	public static final Capability<MagnetCapability> CAPABILITY = null;

	public boolean active;

}
