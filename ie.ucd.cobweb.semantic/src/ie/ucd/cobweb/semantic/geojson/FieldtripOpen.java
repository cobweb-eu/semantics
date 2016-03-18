package ie.ucd.cobweb.semantic.geojson;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ie.ucd.cobweb.semantic.jsonld.Context;

public class FieldtripOpen {

	public static final String FIELDS = "fields";
	public static final String EDITOR = "editor";
	public static final String ID = "editor";
	public static final String TIMESTAMP = "timestamp";

	public static void simplify(Feature feature, Context context) {
		JsonObject props = feature.getProperties();
		JsonObject sems = new JsonObject();

		JsonArray fields = props.getAsJsonArray(FIELDS);
		props.remove(FIELDS);
		for (JsonElement el : fields) {
			if (el.isJsonObject()) {
				JsonObject field = el.getAsJsonObject();

				JsonPrimitive id = field.getAsJsonPrimitive("id");
				JsonElement val = field.getAsJsonPrimitive("val");

				String type = context.getType(id.getAsString());

				sems.add(type, val);
			}
		}

		for (Entry<String, JsonElement> et : props.entrySet()) {
			String type = context.getType(et.getKey());
			JsonElement val = et.getValue();

			sems.add(type, val);
		}

		feature.setProperties(sems);
	}

}
