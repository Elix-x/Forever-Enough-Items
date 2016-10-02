package at.feldim2425.moreoverlays.config;

import java.util.List;

import static at.feldim2425.moreoverlays.config.ConfigHandler.config;

public class Config {

    public static int light_UpRange;
    public static int light_DownRange;
    public static int light_HRange;
    public static boolean light_IgnoreLayer;

    public static int chunk_EdgeRadius;
    public static boolean chunk_ShowMiddle;

    public static boolean itemsearch_ShowItemSearchKey;
    public static boolean itemsearch_FadeoutText;

    public static int render_chunkEdgeColor;
    public static int render_chunkGridColor;
    public static int render_chunkMiddleColor;
    public static float render_chunkLineWidth;
    public static int render_spawnAColor;
    public static int render_spawnNColor;
    public static float render_spawnLineWidth;


    public static void loadValues(){

        config.setCategoryComment("lightoverlay","Settings for the light / mobspawn overlay");
        light_UpRange = config.get("lightoverlay","uprange",4,"Range of the lightoverlay (positive Y)").getInt();
        light_DownRange =  config.get("lightoverlay","downrange",16,"Range of the lightoverlay (negative Y)").getInt();
        light_HRange =  config.get("lightoverlay","hrange",16,"Range of the lightoverlay (Horizontal N,E,S,W)").getInt();
        light_IgnoreLayer =  config.get("lightoverlay","ignoreLayer", false,"Ignore if there in no 2 Block space to spawn. (Less lag if true)").getBoolean();

        config.setCategoryComment("chunkbounds","Settings for the chunk bounds overlay");
        chunk_EdgeRadius = config.get("chunkbounds","radius", 1, "Radius (in Chunks) to show the edges (red line)").getInt();
        chunk_ShowMiddle = config.get("chunkbounds","middle", true, "Show the middle of the current Chunk (yellow line)").getBoolean();

        config.setCategoryComment("itemsearch","Settings for the item search feature");
        itemsearch_FadeoutText = config.get("itemsearch","fadouttext", true, "Show the 'Item Search' text only for one secound and fade out").getBoolean();
        itemsearch_ShowItemSearchKey = config.get("itemsearch","showkey", true, "If the Item Search is enabled show the key to disable it").getBoolean();

        config.setCategoryComment("rendersettings","Settings for lines & colors\nValues: 0xRRGGBB (Hex)");
        render_chunkEdgeColor = config.get("rendersettings","cedgecolor", 0xFF0000, "Color for the chunk edge").getInt();
        render_chunkGridColor = config.get("rendersettings","cgridcolor", 0x00FF00, "Color for the chunk grid").getInt();
        render_chunkMiddleColor = config.get("rendersettings","cmidcolor", 0xFFFF00, "Color for the middle chunk line").getInt();
        render_chunkLineWidth = (float) config.get("rendersettings","clwidth", 1.5, "Line width for chunk boundaries").getDouble();
        render_spawnAColor = config.get("rendersettings","sacolor", 0xFF0000, "Color the X that marks \"Spawns always possible\"").getInt();
        render_spawnNColor = config.get("rendersettings","sncolor", 0xFFFF00, "Color the X that marks \"Spawns at night possible\"").getInt();
        render_spawnLineWidth = (float) config.get("rendersettings","slwidth", 2 , "Line width for spawn indication").getDouble();

        if(config.hasChanged())
            config.save();
    }

    public static void getCategories(List<String> list){
        list.add("lightoverlay");
        list.add("chunkbounds");
        list.add("itemsearch");
        list.add("rendersettings");
    }
}
