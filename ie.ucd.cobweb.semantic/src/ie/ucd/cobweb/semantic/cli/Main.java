package ie.ucd.cobweb.semantic.cli;

import ie.ucd.cobweb.semantic.SurveyData;
import ie.ucd.cobweb.semantic.jsonld.Context;
import ie.ucd.cobweb.semantic.mapping.Ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Main {
	public static final byte[] accounted = new byte[] { 127, 111, 126, -105,
			-6, 65, -69, -114, -71, -91, 21, 99, -6, -51, -29, -59 };

	public static void main(String[] args) throws IOException {
		// TODO: Load context as specified from data itself.
		String filename = "data/base.json-ld";
		JsonElement data = loadData(filename);
		Context context = new Context(data.getAsJsonObject());
		System.out.println("Loaded Context");

		filename = "data/jk-feature.jsonld";
		data = loadData(filename);

		process(data, context);
	}

	private static void process(JsonElement data, Context context)
			throws FileNotFoundException {
		// Check if collection or single feature.
		JsonObject geojson = data.getAsJsonObject();
		JsonPrimitive type = geojson.getAsJsonPrimitive("type");
		if (type.getAsString().equals("Feature")) {
			processFeature(data, context);
		} else if (type.getAsString().equals("FeatureCollection")) {
			JsonArray objs = geojson.getAsJsonArray("features");
			for (JsonElement el : objs) {
				processFeature(el, context);
			}
		}
	}

	private static void processFeature(JsonElement data, Context context)
			throws FileNotFoundException {
		Ontology ontology = new Ontology();
		SurveyData dv = new SurveyData(data.getAsJsonObject(), context,
				ontology);
		String filename;

		System.out.println("Loaded Survey");

		byte[] f = dv.getFingerprint();
		if (!Arrays.equals(f, accounted))
			return;

		filename = dv.getOntologyURL();
		// Do local cache lookup.
		filename = "data/japaneseknotweed1.jsonld";
		JsonElement ont = loadData(filename);
		ontology.loadMappings(ont);

		// Pull imports and do cache lookups.
		filename = "data/dwc.jsonld";
		ont = loadData(filename);
		ontology.loadMappings(ont);

		System.out.println("Loaded Ontology");

		// SurveyVerifier sv = new SurveyVerifier("data/jk.xhtml+rdfa");

		ontology.export(dv);
	}

	private static JsonElement loadData(String filename)
			throws FileNotFoundException {
		File response = new File(filename);
		FileReader fr = new FileReader(response);
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(fr);
		// JsonObject data = root.getAsJsonObject();
		return root;
	}
}
