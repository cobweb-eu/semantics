package ie.ucd.cobweb.semantic;

import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.jsonld.Context;
import ie.ucd.cobweb.semantic.mapping.Ontology;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SurveyData {
	private JsonObject data;
	private Context context;

	private String base;
	private Map<String, Property> properties;
	private String name;
	private Geometry geometry;
	private byte[] fingerprint;

	public SurveyData(JsonObject data, Context context, Ontology ontology)
			throws FileNotFoundException {
		this.data = data;
		this.context = context;
		this.properties = new HashMap<String, Property>();
		this.fingerprint = null;

		JsonArray cta = data.getAsJsonArray("@context");
		base = extractBase(cta);
		System.out.println(String.format("URL Base: %s", base));
		if (base == null)
			System.err.println("No base specified.");
		System.out.println();

		JsonObject properties = data.getAsJsonObject("properties");
		loadProperties(properties);

		JsonArray fields = properties.getAsJsonArray("fields");
		loadFields(fields);

		name = data.getAsJsonPrimitive("name").getAsString();
		geometry = new Geometry(data.getAsJsonObject("geometry"));
		String cname = context.getType("name");
		this.properties.put(cname, new Property(cname, name));
	}

	public Collection<Property> properties() {
		return properties.values();
	}

	public Geometry getGeometry() {
		return geometry;
	}

	private void loadProperties(JsonObject properties) {
		for (Entry<String, JsonElement> entry : properties.entrySet()) {
			String key = entry.getKey();
			JsonElement v = entry.getValue();
			if (!v.isJsonPrimitive())
				continue;

			String dt = context.getType(key);
			String val = v.getAsString();
			this.properties.put(dt, new Property(dt, val));
		}
	}

	private void loadFields(JsonArray fields) {
		properties.remove("fields");
		ArrayList<String> fields_temp = new ArrayList<>();

		for (JsonElement el : fields) {
			if (el.isJsonObject()) {
				JsonObject resp = el.getAsJsonObject();
				JsonPrimitive type = resp.getAsJsonPrimitive("@type");
				JsonPrimitive id = resp.getAsJsonPrimitive("id");
				JsonElement val_temp = resp.get("val");
				JsonPrimitive val = val_temp != null
						&& val_temp.isJsonPrimitive() ? resp
						.getAsJsonPrimitive("val") : null;
				JsonPrimitive label = resp.getAsJsonPrimitive("label");

				if (type == null) {
					type = id;
					System.err.println(String.format(
							"Response '%s' has no type specified.",
							id.getAsString()));
				}
				String ts = type.getAsString();
				String lbl = label.getAsString();

				fields_temp.add(ts + lbl);

				ts = base + ts;
				String vs = val == null ? "" : val.getAsString();
				properties.put(ts, new Property(ts, vs));
			}
		}

		String[] fingers = fields_temp.toArray(new String[] {});
		Arrays.sort(fingers);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (String finger : fingers) {
				md.update(finger.getBytes("UTF-8"));
			}
			fingerprint = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public byte[] getFingerprint() {
		return fingerprint;
	}

	private static String extractBase(JsonArray context) {
		if (context == null)
			return null;
		for (JsonElement el : context) {
			if (el.isJsonObject()) {
				JsonObject spec = el.getAsJsonObject();
				JsonElement base = spec.getAsJsonPrimitive("@base");
				String value = base.getAsString();

				return value;
			}
		}
		return null;
	}

	public class Property {
		public String type;
		public String value;

		private Property(String type, String value) {
			this.type = type;
			this.value = value;
		}
	}

	public String getOntologyURL() {
		return base;
	}
}
