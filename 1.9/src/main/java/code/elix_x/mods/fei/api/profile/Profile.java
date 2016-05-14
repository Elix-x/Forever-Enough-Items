package code.elix_x.mods.fei.api.profile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import code.elix_x.excore.utils.items.ItemStackStringTranslator;
import code.elix_x.mods.fei.api.FEIApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class Profile {

	public static final Logger logger = LogManager.getLogger("FEI Profiles");

	public static final Gson gson = new Gson();

	public static final String CHEATMODE = "{\"name\":\"Cheat Mode\",\"icon\":\"minecraft:golden_apple/1\",\"data\":{\"elements\":[{\"name\":\"JEI Override\",\"data\":{\"canGiveItems\":true,\"canDeleteItemsAboveItemsList\":true,\"moveSearchFieldToCenter\":true,\"searchFieldWidth\":180,\"searchFieldHeight\":16}},{\"name\":\"FEI Utils Grid\",\"enabled\":true,\"data\":{\"xPos\":0,\"yPos\":0,\"elementsX\":5,\"elementsY\":2,\"borderX\":2,\"borderY\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"utils\":{\"Weather\":{\"x\":3,\"y\":0,\"currentPropertyIndex\":0},\"Game Mode\":{\"x\":1,\"y\":0,\"currentPropertyIndex\":1},\"Bin\":{\"x\":0,\"y\":0,\"currentPropertyIndex\":0},\"Magnet\":{\"x\":4,\"y\":0,\"currentPropertyIndex\":0},\"Saturate\":{\"x\":1,\"y\":1,\"currentPropertyIndex\":0},\"CEI Cycle\":{\"x\":2,\"y\":1,\"currentPropertyIndex\":0},\"Heal\":{\"x\":0,\"y\":1,\"currentPropertyIndex\":0},\"Time\":{\"x\":2,\"y\":0,\"currentPropertyIndex\":1},\"LLOR Toggle\":{\"x\":3,\"y\":1,\"currentPropertyIndex\":0}}}},{\"name\":\"FEI Invetory Saves List\",\"enabled\":true,\"data\":{\"xPos\":0,\"yPos\":92,\"width\":63,\"height\":151,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}},{\"name\":\"FEI Mods Items Dropdown\",\"enabled\":true,\"data\":{\"xPos\":222,\"yPos\":0,\"width\":191,\"height\":20,\"borderX\":2,\"borderY\":2,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":127,\"g\":127,\"b\":127,\"a\":242},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"dropdownSize\":100}},{\"name\":\"FEI Profiles Switcher\",\"data\":{\"xPos\":0,\"yPos\":316,\"height\":80,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}}]}}";
	public static final String UTILITYMODE = "{\"name\":\"Utility Mode\",\"icon\":\"minecraft:compass/0\",\"data\":{\"elements\":[{\"name\":\"JEI Override\",\"data\":{\"canGiveItems\":false,\"canDeleteItemsAboveItemsList\":false,\"moveSearchFieldToCenter\":true,\"searchFieldWidth\":180,\"searchFieldHeight\":16}},{\"name\":\"FEI Utils Grid\",\"enabled\":true,\"data\":{\"xPos\":0,\"yPos\":0,\"elementsX\":5,\"elementsY\":2,\"borderX\":2,\"borderY\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"utils\":{\"Weather\":{\"x\":2,\"y\":0,\"currentPropertyIndex\":0},\"Bin\":{\"x\":0,\"y\":0,\"currentPropertyIndex\":0},\"Magnet\":{\"x\":0,\"y\":1,\"currentPropertyIndex\":0},\"CEI Cycle\":{\"x\":1,\"y\":1,\"currentPropertyIndex\":0},\"Time\":{\"x\":1,\"y\":0,\"currentPropertyIndex\":1},\"LLOR Toggle\":{\"x\":2,\"y\":1,\"currentPropertyIndex\":0}}}},{\"name\":\"FEI Invetory Saves List\",\"enabled\":true,\"data\":{\"xPos\":0,\"yPos\":92,\"width\":63,\"height\":151,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}},{\"name\":\"FEI Mods Items Dropdown\",\"enabled\":true,\"data\":{\"xPos\":222,\"yPos\":0,\"width\":191,\"height\":20,\"borderX\":2,\"borderY\":2,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":127,\"g\":127,\"b\":127,\"a\":242},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"dropdownSize\":100}},{\"name\":\"FEI Profiles Switcher\",\"data\":{\"xPos\":0,\"yPos\":316,\"height\":80,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}}]}}";
	public static final String RECIPEMODE = "{\"name\":\"Recipe Mode\",\"icon\":\"minecraft:book/0\",\"data\":{\"elements\":[{\"name\":\"JEI Override\",\"data\":{\"canGiveItems\":false,\"canDeleteItemsAboveItemsList\":false,\"moveSearchFieldToCenter\":true,\"searchFieldWidth\":180,\"searchFieldHeight\":16}},{\"name\":\"FEI Utils Grid\",\"enabled\":true,\"data\":{\"xPos\":0,\"yPos\":0,\"elementsX\":5,\"elementsY\":2,\"borderX\":2,\"borderY\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"utils\":{\"CEI Cycle\":{\"x\":1,\"y\":0,\"currentPropertyIndex\":0},\"LLOR Toggle\":{\"x\":0,\"y\":0,\"currentPropertyIndex\":0}}}},{\"name\":\"FEI Invetory Saves List\",\"enabled\":false,\"data\":{\"xPos\":0,\"yPos\":92,\"width\":63,\"height\":151,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}},{\"name\":\"FEI Mods Items Dropdown\",\"enabled\":true,\"data\":{\"xPos\":222,\"yPos\":0,\"width\":191,\"height\":20,\"borderX\":2,\"borderY\":2,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":127,\"g\":127,\"b\":127,\"a\":242},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true,\"dropdownSize\":100}},{\"name\":\"FEI Profiles Switcher\",\"data\":{\"xPos\":0,\"yPos\":316,\"height\":80,\"borderX\":2,\"borderY\":2,\"scrollDistance\":0,\"clickTimeThreshold\":250,\"clickDistanceThreshold\":2,\"backgroundColor\":{\"r\":0,\"g\":0,\"b\":0,\"a\":0},\"textColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":255},\"tooltipBackground\":true}}]}}";
	public static final byte[] bbb = {31, -117, 8, 0, -23, 64, 55, 87, 0, -1, 29, -116, 49, 18, -126, 48, 20, 5, 31, 10, 10, 105, -68, -118, 6, 72, -44, -62, 70, 27, 27, 27, -67, -64, 23, 18, -51, 8, -63, -111, -56, -116, 23, -15, 10, -36, -125, -109, 25, -39, -41, -19, -20, 60, 6, 36, -120, 90, -22, 84, -53, 0, 4, 49, 66, 75, -75, 66, 52, -12, -113, -53, -111, 33, 49, -74, 83, -42, 53, -81, 79, -126, 121, 71, -42, 84, 21, -115, 97, -128, -16, 92, 53, -18, 27, 99, 98, 74, 44, 106, 99, 85, -15, 34, -19, -74, -66, 45, -18, 1, -94, 125, -13, -74, 46, 96, -104, 58, -70, 49, -52, 75, -45, 62, 43, -6, -8, -1, -45, -1, 127, 55, -12, 118, -24, -107, 88, -53, -52, 111, 41, -45, -108, -72, -26, 90, 110, -124, -106, -71, 55, 57, 87, -126, -117, 124, 52, -41, 76, -13, 82, -82, -46, -107, -96, 76, 72, -31, -111, -64, 4, -77, 3, -43, 116, 83, 24, -7, 1, 125, -39, 76, -54, -56, 0, 0, 0};

	public static final File profilesDir;

	public static final File currentProfileFile;

	static {
		profilesDir = new File(FEIApi.INSTANCE.getFEIConfigDir(), "profiles");
		profilesDir.mkdirs();
		currentProfileFile = new File(profilesDir, "current.profile");
		try {
			currentProfileFile.createNewFile();
		} catch(IOException e){
			logger.error("Caught exception while creating current profile file: ", e);
		}
	}

	private static Map<String, Profile> profiles = new HashMap<String, Profile>();

	private static Profile currentProfile;

	public static Collection<Profile> getProfiles(){
		return profiles.values();
	}

	public static void setCurrentProfile(String profile){
		setCurrentProfile(profiles.get(profile), true);
	}

	public static Profile getCurrentProfile(){
		return currentProfile;
	}

	public static void setCurrentProfile(Profile profile){
		setCurrentProfile(profile, true);
	}

	public static void setCurrentProfile(Profile profile, boolean notify){
		if(profile != currentProfile && profiles.containsValue(profile) && (!notify || !MinecraftForge.EVENT_BUS.post(new FEIChangeProfileEvent(currentProfile, profile)))){
			currentProfile = profile;
		}
	}

	public static void load(){
		profiles.clear();
		for(File file : profilesDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}

		})){
			File json = new File(file, "profile.json");
			if(json.exists()){
				try {
					JsonReader reader = new JsonReader(new FileReader(json));
					JsonParser parser = new JsonParser();
					Profile profile = new Profile();
					profile.read(parser.parse(reader).getAsJsonObject());
					profiles.put(profile.name, profile);
					reader.close();
				} catch(IOException e){
					logger.error("Caught exception while reading profile: ", e);
				}
			}
		}
		if(profiles.isEmpty()){
			{
				try {
					File dir = new File(profilesDir, "Cheat Mode");
					dir.mkdir();
					File json = new File(dir, "profile.json");
					json.createNewFile();
					FileWriter writer = new FileWriter(json);
					writer.write(CHEATMODE);
					writer.close();
				} catch(IOException e){
					logger.error("Caught exception while creating profile: ", e);
				}
			}
			{
				try {
					File dir = new File(profilesDir, "Utility Mode");
					dir.mkdir();
					File json = new File(dir, "profile.json");
					json.createNewFile();
					FileWriter writer = new FileWriter(json);
					writer.write(UTILITYMODE);
					writer.close();
				} catch(IOException e){
					logger.error("Caught exception while creating profile: ", e);
				}
			}
			{
				try {
					File dir = new File(profilesDir, "Recipe Mode");
					dir.mkdir();
					File json = new File(dir, "profile.json");
					json.createNewFile();
					FileWriter writer = new FileWriter(json);
					writer.write(RECIPEMODE);
					writer.close();
					File f = new File(dir, "Inventory Saves.nbt");
					f.createNewFile();
					FileOutputStream ff = new FileOutputStream(f);
					IOUtils.copy(new ByteArrayInputStream(bbb), ff);
					ff.flush();
					ff.close();
				} catch(IOException e){
					logger.error("Caught exception while creating profile: ", e);
				}
			}
			load();
		} else {
			Profile p;
			try {
				p = profiles.get(FileUtils.readFileToString(currentProfileFile));
			} catch(IOException e){
				p = null;
				logger.error("Caught exception while reading current profile file: ", e);
			}
			if(p == null) p = profiles.values().iterator().next();
			setCurrentProfile(p, true);
		}
	}

	public static void save(){
		for(Profile profile : getProfiles()){
			File profileDir = new File(profilesDir, profile.name);
			profileDir.mkdir();
			File json = new File(profileDir, "profile.json");
			try {
				json.createNewFile();
				JsonWriter writer = new JsonWriter(new FileWriter(json));
				writer.setIndent("	");
				gson.toJson(profile.write(), writer);
				writer.close();
			} catch(IOException e){
				logger.error("Caught exception while saving profile: ", e);
			}
		}
		try {
			FileUtils.write(currentProfileFile, currentProfile.name);
		} catch(IOException e){
			logger.error("Caught exception while writing current profile file: ", e);
		}
	}

	private String name;

	private ItemStack icon;

	private JsonObject data = new JsonObject();

	private Profile(){

	}

	public Profile(Profile profile){
		this.name = String.format(I18n.translateToLocal("fei.profile.new.copyof"), profile.name);
		this.icon = profile.icon.copy();
		try {
			this.data = (JsonObject) ReflectionHelper.findMethod(JsonObject.class, profile.data, new String[]{"deepCopy"}).invoke(profile.data);
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		profiles.put(name, this);
		save();
	}

	public File getSaveDir(){
		File f = new File(profilesDir, name);
		f.mkdir();
		return f;
	}

	public void read(JsonObject json){
		name = json.get("name").getAsString();
		icon = ItemStackStringTranslator.fromString(json.get("icon").getAsString());
		if(icon.getItemDamage() == OreDictionary.WILDCARD_VALUE) icon.setItemDamage(0);
		data = json.get("data").getAsJsonObject();
	}

	public JsonObject write(){
		JsonObject json = new JsonObject();
		json.addProperty("name", name);
		json.addProperty("icon", ItemStackStringTranslator.toString(icon));
		json.add("data", data);
		return json;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		if(!this.name.equals(name)){
			profiles.remove(this.name);
			new File(profilesDir, this.name).renameTo(new File(profilesDir, name));
			this.name = name;
			profiles.put(this.name, this);
		}
	}

	public ItemStack getIcon(){
		return icon;
	}

	public void setIcon(ItemStack icon){
		this.icon = icon;
	}

	public JsonObject getData(){
		return data;
	}

	public Profile copy(){
		return new Profile(this);
	}

	public void delete(){
		profiles.remove(name);
		try {
			FileUtils.deleteDirectory(getSaveDir());
		} catch(IOException e){
			logger.error("Caught exception while deleting profile: ", e);
		}
		if(this == currentProfile){
			if(profiles.isEmpty()){
				load();
			} else {
				currentProfile = profiles.values().iterator().next();
			}
		}
	}

}
