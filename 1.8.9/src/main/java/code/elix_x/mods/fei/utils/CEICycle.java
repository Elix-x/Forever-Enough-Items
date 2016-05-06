package code.elix_x.mods.fei.utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cei.ChunkEdgeRenderer;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import code.elix_x.mods.fei.api.utils.ForFEIUtil;
import code.elix_x.mods.fei.config.FEIConfiguration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

public class CEICycle extends ForFEIUtil<Integer> {

	public static final String[] descs = {"off", "corners", "all"};
	public static final ResourceLocation[] icons = {new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_off.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_corners.png"), new ResourceLocation(ForeverEnoughItemsBase.MODID, FEIConfiguration.icons + "chunkedge_all.png")};

	public static final AField<ChunkEdgeRenderer, Integer> chunkEdgeState = new AField(ChunkEdgeRenderer.class, "chunkEdgeState").setAccessible(true);

	public static ChunkEdgeRenderer cei;

	public static void initChunkRenderer(){
		for(Object o : ((ConcurrentHashMap<Object, ArrayList<IEventListener>>) new AField<EventBus, ConcurrentHashMap<Object, ArrayList<IEventListener>>>(EventBus.class, "listeners").setAccessible(true).get(MinecraftForge.EVENT_BUS)).keySet()){
			if(o instanceof ChunkEdgeRenderer){
				cei = (ChunkEdgeRenderer) o;
				break;
			}
		}
	}

	public CEICycle(){
		super("CEI Cycle", 0, 1, 2);
	}

	@Override
	public Integer getCurrent(){
		return chunkEdgeState.get(cei);
	}

	@Override
	public String getDesc(Integer i){
		return StatCollector.translateToLocal("fei.gui.override.grid.utils.chunkedge." + descs[i]);
	}

	@Override
	public boolean isEnabled(Integer i){
		return true;
	}

	@Override
	public void onSelect(Integer i){
		chunkEdgeState.set(cei, i);
	}

	@Override
	public ResourceLocation getTexture(Integer i){
		return icons[i];
	}

	@Override
	public String getText(Integer i){
		return null;
	}

}
