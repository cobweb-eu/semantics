package ie.ucd.cobweb.semantic.mapping;

import ie.ucd.cobweb.semantic.jsonld.JSON;

import com.google.gson.JsonObject;

public class ValueConstant {
	public final String type;
	public final String key;
	public final String val;

	public static final String KEY = "http://prophet.ucd.ie/ontology/cobweb/map#key";
	public static final String VAL = "http://prophet.ucd.ie/ontology/cobweb/map#value";
	public static final String TYPE = "http://prophet.ucd.ie/ontology/cobweb/map#mapType";

	private ValueConstant(String type, String key, String val) {
		this.type = type;
		this.key = key;
		this.val = val;
	}

	public static ValueConstant extract(JsonObject spec) {
		String key = JSON.extractArrayElement(KEY, "@value", spec);
		String val = JSON.extractArrayElement(VAL, "@value", spec);
		String type = JSON.extractArrayElement(TYPE, "@value", spec);

		return new ValueConstant(type, key, val);
	}
}
