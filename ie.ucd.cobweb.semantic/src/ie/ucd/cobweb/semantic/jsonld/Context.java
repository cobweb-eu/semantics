package ie.ucd.cobweb.semantic.jsonld;

import ie.ucd.cobweb.semantic.cli.FileCache;
import ie.ucd.cobweb.semantic.cli.Main;
import ie.ucd.cobweb.semantic.geojson.Feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Context {
	private static final String SEP = ":";
	private static Context dflt;

	public final String base;
	private Map<String, String> map;

	private Context(String base, Map<String, String> map) {
		this.base = base;
		this.map = map;
	}

	public String getType(String key) {
		String value = map.get(key);
		if (value == null)
			value = base + key;

		return value;
	}

	public static Context extract(Feature feature, FileCache cache,
			String fingerprint) {
		JsonElement c = feature.getMember(JSON.CONTEXT);
		if (c == null) {
			return defaultContext(fingerprint);
		} else {
			JsonArray ca = c.getAsJsonArray();
			String base = extractBase(ca);
			Context[] cas = extractContexts(ca, cache);
			Context merge = merge(cas);

			return new Context(base, merge.map);
		}
	}

	private static Context defaultContext(String fingerprint) {
		String base = lookupBase(fingerprint);
		return base == null ? null : new Context(base, dflt.map);
	}

	static {
		try {
			dflt = Context.from(Main.loadSource("data/base.json-ld")
					.getAsJsonObject());
		} catch (IOException e) {
		}
	}

	private static String lookupBase(String fingerprint) {
		return null;
	}

	public static String extractBase(JsonArray context) {
		for (JsonElement el : context) {
			if (el.isJsonObject()) {
				JsonObject spec = el.getAsJsonObject();
				return base(spec);
			}
		}
		return null;
	}

	private static String base(JsonObject obj) {
		JsonElement base = obj.getAsJsonPrimitive(JSON.BASE);
		String value = base == null ? "" : base.getAsString();
		return value;
	}

	private static Context[] extractContexts(JsonArray context, FileCache cache) {
		ArrayList<Context> contexts = new ArrayList<>();
		for (JsonElement el : context) {
			if (el.isJsonPrimitive()) {
				String url = el.getAsString();
				JsonElement obj = cache.getJSONDocument(url);
				Context c = from(obj.getAsJsonObject());
				contexts.add(c);
			}
		}

		return contexts.toArray(new Context[] {});
	}

	private static Context merge(Context[] contexts) {
		Map<String, String> map = new HashMap<>();
		for (Context c : contexts) {
			map.putAll(c.map);
		}
		return new Context(null, map);
	}

	public static Context from(JsonObject data) {
		Map<String, String> map = new HashMap<>();
		String base = null;

		for (Entry<String, JsonElement> entry : data.entrySet()) {
			String key = entry.getKey();
			JsonElement el = entry.getValue();
			if (el.isJsonPrimitive()) {
				String value = el.getAsString();
				if (value.indexOf(SEP) > 0) {
					value = substitute(value, data);
				}
				map.put(key, value);
			} else if (el.isJsonObject()) {
				base = base(el.getAsJsonObject());
			}
		}

		return new Context(base, map);
	}

	private static String substitute(String value, JsonObject data) {
		String[] vals = value.split(SEP);
		JsonElement el = data.get(vals[0]);
		if (el == null || vals.length != 2)
			return value;
		return el.getAsString() + vals[1];
	}
}
