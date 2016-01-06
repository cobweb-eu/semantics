package ie.ucd.cobweb.semantic.geojson;

import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Geometry {
	private double lon;
	private double lat;

	public Geometry(JsonObject root) {
		JsonPrimitive type = root.getAsJsonPrimitive("type");
		if ("Point".equals(type.getAsString())) {
			JsonArray coords = root.getAsJsonArray("coordinates");
			lon = coords.get(0).getAsDouble();
			lat = coords.get(1).getAsDouble();
		}
	}

	public void export(Exporter ex) {
		ex.set("geometry.type", "Point");
		ex.set("geometry.lat", lat + "");
		ex.set("geometry.lon", lon + "");
	}
}
