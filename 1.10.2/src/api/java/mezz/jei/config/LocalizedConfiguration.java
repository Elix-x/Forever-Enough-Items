package mezz.jei.config;

import java.io.File;
import java.util.Arrays;

import mezz.jei.util.Translator;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class LocalizedConfiguration extends Configuration {
	private static final String commentPostfix = ".comment";
	private final String keyPrefix;
	private final String defaultLocalized;
	private final String validLocalized;

	public LocalizedConfiguration(String keyPrefix, File file, String configVersion) {
		super(file, configVersion);
		this.keyPrefix = keyPrefix + '.';
		this.defaultLocalized = Translator.translateToLocal(this.keyPrefix + "default");
		this.validLocalized = Translator.translateToLocal(this.keyPrefix + "valid");
	}

	public void addCategory(String categoryName) {
		String categoryKey = keyPrefix + categoryName;
		String commentKey = categoryKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		setCategoryComment(categoryName, comment);
		setCategoryLanguageKey(categoryName, categoryKey);
	}

	public boolean getBoolean(String category, String name, boolean defaultValue) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		return getBoolean(name, category, defaultValue, comment, langKey);
	}

	public String getString(String name, String category, String defaultValue) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		return getString(name, category, defaultValue, comment, langKey);
	}

	public String getString(String name, String category, String defaultValue, String[] validValues) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);

		Property prop = get(category, name, defaultValue);
		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.setComment(comment + " [" + defaultLocalized + ": " + defaultValue + "] [" + validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
		return prop.getString();
	}

	public <T extends Enum<T>> T getEnum(String name, String category, T defaultValue, T[] validEnumValues) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		Property prop = get(category, name, defaultValue.name());

		String[] validValues = new String[validEnumValues.length];
		for (int i = 0; i < validEnumValues.length; i++) {
			T enumValue = validEnumValues[i];
			validValues[i] = enumValue.name();
		}

		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.setComment(comment + " [" + defaultLocalized + ": " + defaultValue + "] [" + validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
		String stringValue = prop.getString();

		T enumValue = defaultValue;
		for (int i = 0; i < validValues.length; i++) {
			if (stringValue.equals(validValues[i])) {
				enumValue = validEnumValues[i];
			}
		}

		return enumValue;
	}

	public String[] getStringList(String name, String category, String[] defaultValue) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);

		Property prop = get(category, name, defaultValue);
		prop.setLanguageKey(langKey);
		prop.setComment(comment + " [" + defaultLocalized + ": " + Arrays.toString(defaultValue) + ']');
		return prop.getStringList();
	}

	public String[] getStringList(String name, String category, String[] defaultValue, String[] validValues) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);

		Property prop = get(category, name, defaultValue);
		prop.setLanguageKey(langKey);
		prop.setValidValues(validValues);
		prop.setComment(comment + " [" + defaultLocalized + ": " + Arrays.toString(defaultValue) + "] [" + validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
		return prop.getStringList();
	}

	public float getFloat(String name, String category, float defaultValue, float minValue, float maxValue) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		return getFloat(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

	public int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		String langKey = keyPrefix + category + '.' + name;
		String commentKey = langKey + commentPostfix;
		String comment = Translator.translateToLocal(commentKey);
		return getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}
}
