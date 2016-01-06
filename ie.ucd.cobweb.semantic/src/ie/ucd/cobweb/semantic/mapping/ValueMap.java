package ie.ucd.cobweb.semantic.mapping;

import ie.ucd.cobweb.semantic.SurveyData.Property;
import ie.ucd.cobweb.semantic.jsonld.JSON;
import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import com.google.gson.JsonObject;

public class ValueMap {
	/**
	 * Key to provide in dictionary.
	 */
	private static final String TO = "http://prophet.ucd.ie/ontology/cobweb/map#mapsToElement";

	/**
	 * For values matching
	 */
	private static final String FILTER = "http://prophet.ucd.ie/ontology/cobweb/map#forValue";
	/**
	 * Use this value instead.
	 */
	private static final String RESULT = "http://prophet.ucd.ie/ontology/cobweb/map#mappedValue";

	public final String id;

	private String to;
	private String filter;
	private String result;

	private ValueMap(String id, String to, String filter, String result) {
		this.id = id;

		this.to = to;
		this.filter = filter;
		this.result = result;
	}

	public static ValueMap extract(JsonObject spec) {
		String id = JSON.extractID(spec);

		String to = JSON.extractArrayElement(TO,"@value",spec);
		if (to == null)
			return null;

		String filter = JSON.extractArrayElement(FILTER,"@value",spec);
		String result = JSON.extractArrayElement(RESULT,"@value",spec);

		return new ValueMap(id, to, filter, result);
	}

	public void export(Property prop, Exporter ex) {
		String value = prop.value;
		if( value.equals(filter)) 
			value = result;
		
		ex.set(to, value);
	}

}
