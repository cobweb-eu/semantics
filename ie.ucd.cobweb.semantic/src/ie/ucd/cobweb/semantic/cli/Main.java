package ie.ucd.cobweb.semantic.cli;

import ie.ucd.cobweb.semantic.SurveyData;
import ie.ucd.cobweb.semantic.jsonld.Context;
import ie.ucd.cobweb.semantic.mapping.Ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Main {
	public static final byte[] accounted = new byte[] { 127, 111, 126, -105,
			-6, 65, -69, -114, -71, -91, 21, 99, -6, -51, -29, -59 };

	static HashSet<String> lists = new HashSet<>();
	static FileCache cache = new FileCache("temp/");

	public static void main(String[] args) throws IOException {
		String file = "data/jk-feature.jsonld";
		if(args.length>0)
			file = args[0];
		
		
		
		// TODO: Load context as specified from data itself.
		String filename = "data/base.json-ld"; //TODO: Cache
		JsonElement data = loadData(filename);
		Context context = new Context();
		context.load(data.getAsJsonObject());
		System.out.println("Loaded Context");

//		filename = "data/75802b0b-452b-4ff6-bb1c-1856b2582f89.txt";
//		[127, 111, 126, -105, -6, 65, -69, -114, -71, -91, 21, 99, -6, -51, -29, -59]
		
//		filename = "data/11b4c5da-1464-4e47-ab1f-dc3563ec9ef2.txt";
//		[-55, -16, -122, 111, 84, 97, 24, 115, 34, 56, 20, -35, -10, 5, -51, 37]
		
//		filename = "data/b1b28830-9443-46b1-82f7-3d772f30cdbb.txt";
//		[-72, 48, -40, -59, -33, 77, -61, 26, 8, -99, 39, -14, -51, 53, -59, -34]
//		[-76, 17, -32, 95, -113, -12, -94, -91, 24, 125, -13, -18, 71, -49, 15, 122]
		
//		filename = "data/c5493108-d140-43a3-82c9-7ef80730b788.txt";
//		[56, 113, 27, -78, 4, -63, 71, 116, -47, -54, 44, -15, -80, -121, -92, 53]
//		[21, -127, -73, 86, 17, -55, -76, -115, 64, 47, -55, 34, -36, 35, 62, -69]
//		[-109, -46, 114, -38, 9, 110, -120, -22, 89, -9, 109, -70, 76, 80, -9, -55]
//		[-93, 114, 121, 43, -32, 84, 53, -84, 122, 88, -127, 3, 26, -118, 81, -80]
		
		filename = file;
//		[127, 111, 126, -105, -6, 65, -69, -114, -71, -91, 21, 99, -6, -51, -29, -59]
		data = loadData(filename);

		process(data, context);

		System.out.println();
		System.out.println("Fingerprints");
		for (String b : lists.toArray(new String[] {}))
			System.out.println(b);
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

		byte[] f = dv.getFingerprint();
		String fs = Arrays.toString(f);
		System.out.println(fs);
		lists.add(fs);
//		if (true)
//			return;
		if (!Arrays.equals(f, accounted))
			return;

		System.out.println("Loaded Survey");

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

		ontology.export(dv, cache);
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
