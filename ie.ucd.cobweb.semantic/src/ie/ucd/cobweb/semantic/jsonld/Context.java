package ie.ucd.cobweb.semantic.jsonld;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Context {
	private static final String SEP = ":";
	private Map<String, String> map;

	public Context() {
		map = new HashMap<String, String>();
	}

	public void load(JsonObject data) {
		for (Entry<String, JsonElement> entry : data.entrySet()) {
			String key = entry.getKey();
			JsonElement el = entry.getValue();
			if (el.isJsonPrimitive()) {
				String value = el.getAsString();
				if (value.indexOf(SEP) > 0) {
					value = substitute(value, data);
				}
				map.put(key, value);
			}
		}
	}

	private static String substitute(String value, JsonObject data) {
		String[] vals = value.split(SEP);
		JsonElement el = data.get(vals[0]);
		if (el == null || vals.length != 2)
			return value;
		return el.getAsString() + vals[1];
	}

	public String getType(String key) {
		return map.get(key);
	}
}
