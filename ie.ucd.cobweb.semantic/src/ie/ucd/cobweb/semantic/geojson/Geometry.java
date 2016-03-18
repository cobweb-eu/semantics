package ie.ucd.cobweb.semantic.geojson;

import java.util.HashMap;
import java.util.Map;

import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;
import ie.ucd.cobweb.semantic.swe4cs.SWE4CS;

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

	public Map<String, Object> collapse(String id) {
		Map<String, Object> map = new HashMap<>();
		
		String point_format = "<gml:Point gml:id=\"%s\">\r\n"+
		 "<gml:pos srsName=\"urn:ogc:def:crs:EPSG:6.8:3857\">%f %f</gml:pos>\r\n"+
		 "</gml:Point>";
		//52.409602775074845 -4.078234501964251
		
		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.SHAPE, String.format(point_format, id, lat, lon));
		 
		return map;
	}
}
