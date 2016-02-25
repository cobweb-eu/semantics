package ie.ucd.cobweb.semantic.geojson;

import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Geometry {
	private static final String COORDINATES = "coordinates";
	private static final String TYPE = "type";

	private double lon;
	private double lat;

	public Geometry(JsonObject root) {
		JsonPrimitive type = root.getAsJsonPrimitive(TYPE);
		if ("Point".equals(type.getAsString())) {
			JsonArray coords = root.getAsJsonArray(COORDINATES);
			lon = coords.get(0).getAsDouble();
			lat = coords.get(1).getAsDouble();
		}
	}

	public void export(Exporter ex) {
		ex.set("geometry_type", "Point");
		ex.set("geometry_lat", lat + "");
		ex.set("geometry_lon", lon + "");
	}
}
