package ie.ucd.cobweb.semantic.geojson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Feature {
	// XXX Geometry support.

	private JsonObject root;

	public Feature(JsonElement root) {
		this.root = root.getAsJsonObject();
	}

	public JsonElement getMember(String member) {
		return root.get(member);
	}

	public String getStringMember(String member) {
		JsonElement m = getMember(member);
		return m == null ? "" : m.getAsString();
	}

	public JsonObject getProperties() {
		return root.getAsJsonObject(GeoJSON.PROPERTIES);
	}

	public void setProperties(JsonObject props) {
		root.remove(GeoJSON.PROPERTIES);
		root.add(GeoJSON.PROPERTIES, props);
	}

}
