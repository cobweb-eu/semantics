package ie.ucd.cobweb.semantic.jsonld;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSON {

	public static String extractArrayElement(String key, String element,
			JsonObject spec) {
		JsonArray arr = spec.getAsJsonArray(key);
		if (arr == null)
			return null;

		for (JsonElement el : arr) {
			if (el.isJsonObject()) {
				JsonObject val = el.getAsJsonObject();
				JsonPrimitive prim = val.getAsJsonPrimitive(element);
				return prim.getAsString();
			}
		}
		return null;
	}

	public static String[] extractArrayIDs(String key, JsonObject spec) {
		ArrayList<String> values = new ArrayList<>();
		JsonArray arr = spec.getAsJsonArray(key);
		if (arr == null)
			return new String[] {};

		for (JsonElement el : arr) {
			if (el.isJsonObject()) {
				JsonObject val = el.getAsJsonObject();
				JsonPrimitive prim = val.getAsJsonPrimitive("@id");
				values.add(prim.getAsString());
			}
		}

		return values.toArray(new String[] {});
	}

	public static String extractID(JsonObject spec) {
		JsonPrimitive prim = spec.getAsJsonPrimitive("@id");
		return prim.getAsString();
	}

	public static final String ID = "@id";
	public static final String VALUE = "@value";
	public static final String CONTEXT = "@context";
	public static final String BASE = "@base";
	private static final JsonParser parser = new JsonParser();

	public static JsonElement parse(String contents) {
		return parser.parse(contents);
	}

}
