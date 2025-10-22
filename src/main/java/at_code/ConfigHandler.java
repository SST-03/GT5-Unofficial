package at_code;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

// Code from amazing trophies, need to rewrite some time
public class ConfigHandler {

    public static final String PROPERTY_REGISTRY_NAME = "registryName";
    public static final String PROPERTY_META = "meta";
    public static final String PROPERTY_NBT = "nbt";
    private static final JsonParser PARSER = new JsonParser();

    @SuppressWarnings("unchecked")
    public static Class<? extends Entity> parseEntityClass(JsonElement json) {
        String name = json.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(name);
        if (clazz != null) {
            return (Class<? extends Entity>) clazz;
        }
        // not a valid entity name, try parsing as class name
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find target class!", e);
        }
        if (Entity.class.isAssignableFrom(clazz)) {
            return (Class<? extends Entity>) clazz;
        } else {
            throw new IllegalArgumentException(name + " is not a subclass of " + Entity.class.getName() + "!");
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends EntityLivingBase> parseEntityLivingClass(JsonElement element) {
        String className = element.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(className);
        if (clazz == null) {
            // not a valid entity name, try parsing as class name
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Could not find target class!", e);
            }
        }
        if (EntityLivingBase.class.isAssignableFrom(clazz)) {
            return (Class<? extends EntityLivingBase>) clazz;
        } else {
            throw new IllegalArgumentException(
                className + " is not a subclass of " + EntityLivingBase.class.getName() + "!");
        }
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new JsonSyntaxException("Required property \"" + key + "\" is missing!");
        }
        return parser.apply(element);
    }

    public static int getIntegerProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsInt);
    }

    public static JsonObject getObjectProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsJsonObject);
    }

    public static <T> Set<T> getSetProperty(JsonObject json, String key, Function<JsonElement, T> parser) {
        return getProperty(json, key, jsonElement -> {
            Set<T> set = new HashSet<>();
            if (jsonElement.isJsonPrimitive()) {
                set.add(parser.apply(jsonElement));
                return set;
            }
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                set.add(parser.apply(array.get(i)));
            }
            return set;
        });
    }

    public static String getStringProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsString);
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser, T fallback) {
        JsonElement element = json.get(key);
        if (element == null) {
            return fallback;
        }
        if (element.isJsonNull()) {
            return null;
        }
        return parser.apply(element);
    }

    public static boolean getBooleanProperty(JsonObject json, String key, boolean fallback) {
        return getProperty(json, key, JsonElement::getAsBoolean, fallback);
    }

    public static double getDoubleProperty(JsonObject json, String key, double fallback) {
        return getProperty(json, key, JsonElement::getAsDouble, fallback);
    }

    public static float getFloatProperty(JsonObject json, String key, float fallback) {
        return getProperty(json, key, JsonElement::getAsFloat, fallback);
    }

    public static int getIntegerProperty(JsonObject json, String key, int fallback) {
        return getProperty(json, key, JsonElement::getAsInt, fallback);
    }

    public static <T> Set<T> getSetProperty(JsonObject json, String key, Function<JsonElement, T> parser,
        Set<T> fallback) {
        return getProperty(json, key, jsonElement -> {
            Set<T> set = new HashSet<>();
            if (jsonElement.isJsonPrimitive()) {
                set.add(parser.apply(jsonElement));
                return set;
            }
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                set.add(parser.apply(array.get(i)));
            }
            return set;
        }, fallback);
    }

    public static String getStringProperty(JsonObject json, String key, String fallback) {
        return getProperty(json, key, JsonElement::getAsString, fallback);
    }

}
