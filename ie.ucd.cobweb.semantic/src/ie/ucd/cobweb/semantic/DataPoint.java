package ie.ucd.cobweb.semantic;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ie.ucd.cobweb.semantic.geojson.Feature;
import ie.ucd.cobweb.semantic.geojson.FieldtripOpen;
import ie.ucd.cobweb.semantic.geojson.GeoJSON;
import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.jsonld.Context;

public class DataPoint {

	private static Geometry geometry;

	public static DataPoint extract(Feature feature) {
		String name = feature.getStringMember(GeoJSON.NAME);
		String id = feature.getMember(GeoJSON.PROPERTIES).getAsJsonObject()
				.get(GeoJSON.ID).getAsString();
		String editor = feature.getMember(GeoJSON.PROPERTIES).getAsJsonObject()
				.get(FieldtripOpen.ID).getAsString();

		DataPoint point = new DataPoint(name, id, editor);

		loadProperties(feature, point);
		loadFields(feature, point);

		return point;
	}

	private static void loadFields(Feature feature, DataPoint point) {
		ArrayList<String> fields_temp = new ArrayList<>();
		JsonObject props = feature.getProperties();
		JsonArray surv = props.get(FieldtripOpen.FIELDS).getAsJsonArray();

		for (JsonElement el : surv) {
			if (el.isJsonObject()) {
				JsonObject resp = el.getAsJsonObject();

				JsonPrimitive id = resp.getAsJsonPrimitive("id");
				JsonPrimitive type = id;// resp.getAsJsonPrimitive("@type");
				JsonElement val_temp = resp.get("val");
				JsonPrimitive val = val_temp != null
						&& val_temp.isJsonPrimitive() ? resp
						.getAsJsonPrimitive("val") : null;
				JsonPrimitive label = resp.getAsJsonPrimitive("label");

				String ts = type.getAsString();
				String lbl = label.getAsString();

				fields_temp.add(ts + lbl);

				String vs = val == null ? "" : val.getAsString();
				point.properties.put(ts, point.new Property(ts, vs));
			}

			String[] fingers = fields_temp.toArray(new String[] {});
			setFingerprint(point, fingers);
		}
	}

	private static void loadProperties(Feature feature, DataPoint point) {
		JsonObject props = feature.getProperties();

		point.properties.put(GeoJSON.NAME, point.new Property(GeoJSON.NAME,
				point.name));

		for (Entry<String, JsonElement> entry : props.entrySet()) {
			String key = entry.getKey();
			JsonElement v = entry.getValue();
			if (!v.isJsonPrimitive())
				continue;

			point.properties.put(key, point.new Property(key, v.toString()));
		}

		geometry = new Geometry(feature.getMember(GeoJSON.GEOMETRY)
				.getAsJsonObject());
	}

	private static void setFingerprint(DataPoint point, String[] fingers) {
		Arrays.sort(fingers);
		String fingerprint = "";

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (String finger : fingers) {
				md.update(finger.getBytes("UTF-8"));
			}
			byte[] fp = md.digest();
			fingerprint = Base64.getEncoder().encodeToString(fp);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		point.fingerprint = fingerprint;
	}

	public final String name;
	public final String id;
	public final String editor;
	private String fingerprint;
	private HashMap<String, Property> properties;

	private DataPoint(String name, String id, String editor) {
		this.name = name;
		this.id = id;
		this.editor = editor;

		this.properties = new HashMap<>();
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public static DataPoint convert(DataPoint origin, Context context) {
		DataPoint conv = new DataPoint(origin.name, origin.id, origin.editor);
		for (Property p : origin.properties.values()) {
			String key = context.getType(p.type);
			Property prop = conv.new Property(key, p.value);

			conv.properties.put(key, prop);
		}
		return conv;
	}

	public Collection<Property> properties() {
		return properties.values();
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public class Property {
		public String type;
		public String value;

		protected Property(String type, String value) {
			this.type = type;
			this.value = value;
		}
	}
}
