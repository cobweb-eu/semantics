package ie.ucd.cobweb.semantic.mapping;

import ie.ucd.cobweb.semantic.jsonld.JSON;

import com.google.gson.JsonObject;

public class Configuration {

	public static final String TEMPLATE = "http://prophet.ucd.ie/ontology/cobweb/map#templateFile";
	public static final String TEMPLATE_COLLECTION = "http://prophet.ucd.ie/ontology/cobweb/map#templateCollection";
	public static final String TYPE = "http://prophet.ucd.ie/ontology/cobweb/map#type";

	public final String template;
	public final String type;

	public Configuration(String filename, String type) {
		this.template = filename;
		this.type = type;
	}

	public static Configuration extract(JsonObject spec) {
		String filename = JSON.extractArrayElement(TEMPLATE, "@value", spec);

		String type = JSON.extractArrayElement(TYPE, "@value", spec);

		return new Configuration(filename, type);
	}

}
