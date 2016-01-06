package ie.ucd.cobweb.semantic.mapping;

import ie.ucd.cobweb.semantic.jsonld.JSON;

import com.google.gson.JsonObject;

public class ValueConfig {
	public static final String MAPPING = "http://prophet.ucd.ie/ontology/cobweb/survey#hasMapping";
	
	public final String id;
	public final String[] mappings;

	public ValueConfig(String id, String[] mappings) {
		this.id = id;
		this.mappings= mappings;
	}

	public static ValueConfig extract(JsonObject spec) {
		String id = JSON.extractID(spec);
		
		String[] mappings = JSON.extractArrayIDs(MAPPING, spec);
		
		return new ValueConfig(id, mappings);
	}
}
