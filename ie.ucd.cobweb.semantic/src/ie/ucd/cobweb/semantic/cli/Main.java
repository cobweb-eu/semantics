package ie.ucd.cobweb.semantic.cli;

import freemarker.template.TemplateException;
import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.geojson.Feature;
import ie.ucd.cobweb.semantic.geojson.FieldtripOpen;
import ie.ucd.cobweb.semantic.jsonld.Context;
import ie.ucd.cobweb.semantic.swe4cs.DataRecord;
import ie.ucd.cobweb.semantic.swe4cs.FeatureOfInterest;
import ie.ucd.cobweb.semantic.swe4cs.Procedure;
import ie.ucd.cobweb.semantic.swe4cs.SWE4CS;
import ie.ucd.cobweb.semantic.swe4cs.SWEObservation;
import ie.ucd.cobweb.semantic.swe4cs.Timestamp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Main {
	public enum Mode {
		EXPORT, SIMPLIFY, SWE4CS
	}

	static {
		Map<String, String> locals = new HashMap<>();

		locals.put(
				"http://prophet.ucd.ie/ontology/cobweb/context/geojson.jsonld",
				"data/base.json-ld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/context/base.jsonld",
				"data/base.json-ld");
		locals.put(
				"http://prophet.ucd.ie/ontology/cobweb/surveys/japaneseknotweed1",
				"data/japaneseknotweed1.jsonld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/survey",
				"data/dwc.jsonld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/properties",
				"data/dwc.jsonld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/geojson",
				"data/dwc.jsonld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/map",
				"data/dwc.jsonld");
		locals.put("http://prophet.ucd.ie/ontology/cobweb/dwc",
				"data/dwc.jsonld");
		locals.put(
				"http://prophet.ucd.ie/ontology/cobweb/mapping/darwincore.ftl",
				"templates/template.ftl");
		// locals.put("","");
		// locals.put("","");
		cache = new FileCache("temp/", locals);
	}

	static HashSet<String> lists = new HashSet<>();
	static List<SWEObservation> bundles = new ArrayList<>();
	static FileCache cache;

	private static Mode mode;

	public static void main(String[] args) throws IOException,
			TemplateException {
		String file = "data/jk-feature.jsonld";
		if (args.length > 0) {
			file = args[0];
		}
		mode = getMode(args);

		// process(file);
		// f29+l/pBu465pRVj+s3jxQ==
		//process("data/75802b0b-452b-4ff6-bb1c-1856b2582f89.txt");
		// //f29+l/pBu465pRVj+s3jxQ==
		// process("data/11b4c5da-1464-4e47-ab1f-dc3563ec9ef2.txt");
		// //yfCGb1RhGHMiOBTd9gXNJQ==
		process("data/b1b28830-9443-46b1-82f7-3d772f30cdbb.txt");
		// //tBHgX4/0oqUYffPuR88Peg== uDDYxd9NwxoInSfyzTXF3g==
		// process("data/c5493108-d140-43a3-82c9-7ef80730b788.txt");
		// //OHEbsgTBR3TRyizxsIekNQ== o3J5K+BUNax6WIEDGopRsA==
		// //FYG3VhHJtI1AL8ki3CM+uw== k9Jy2gluiOpZ9226TFD3yQ==

		// System.out.println("Unique IDs:");
		// for (String s : lists)
		// System.out.println(s);

		cache.close();
	}

	private static Mode getMode(String[] args) {
		String mode = "SWE4CS";
		if (args.length > 1) {
			mode = args[1];
		}
		Mode type;
		switch (mode) {
		case "EXPORT":
			type = Mode.EXPORT;
			break;
		case "SWE4CS":
			type = Mode.SWE4CS;
			break;
		default:
			System.err
					.println("Mode incorrectly specified. Defaulting to SIMPLIFY.\r\n"
							+ "(Valid values are SIMPLIFY, EXPORT and SWE4CS");
		case "SIMPLIFY":
			type = Mode.SIMPLIFY;
		}

		return type;
	}

	private static void process(String file) throws IOException,
			TemplateException {
		JsonElement survey_raw = loadSource(file);
		Feature[] features = processGeoJSON(survey_raw);

		for (Feature feature : features) {
			DataPoint point = DataPoint.extract(feature);
			lists.add(point.getFingerprint());

			Context context = Context.extract(feature, cache,
					point.getFingerprint());

			switch (mode) {
			case SIMPLIFY:
				FieldtripOpen.simplify(feature, context);
				break;
			case EXPORT:
				processExport(point, context);
				break;
			case SWE4CS:
				processSWE4CS(point, context);
				break;
			}
		}

		switch (mode) {
		case SIMPLIFY:
			exportSimplify(survey_raw);
			break;
		case EXPORT:
			exportExports();
			break;
		case SWE4CS:
			exportSWE4CS();
			break;
		}
	}

	private static void processSWE4CS(DataPoint point, Context context) {
		point = DataPoint.convert(point, context);

		SWEObservation bundle = SWEObservation.build(point, context);
		bundles.add(bundle);
	}

	private static void processExport(DataPoint point, Context context) {
		point = DataPoint.convert(point, context);
		Ontology ontology = Ontology.initiate(context, cache);
		ontology.export(point, cache);
	}

	private static void exportSimplify(JsonElement surveys) {
		System.out.println(surveys.toString());
	}

	private static void exportExports() {
		// TODO Auto-generated method stub

	}

	private static void exportSWE4CS() throws IOException, TemplateException {
		if (bundles.size() == 0)
			return;

		SWE4CS.export(cache, bundles.toArray(new SWEObservation[] {}));
	}

	private static Feature[] processGeoJSON(JsonElement data) {
		ArrayList<Feature> a = new ArrayList<>();

		JsonObject geojson = data.getAsJsonObject();
		JsonPrimitive type = geojson.getAsJsonPrimitive("type");
		if (type.getAsString().equals("Feature")) {
			a.add(new Feature(data));
		} else if (type.getAsString().equals("FeatureCollection")) {
			JsonArray objs = geojson.getAsJsonArray("features");
			for (JsonElement el : objs) {
				a.add(new Feature(el));
			}
		}

		return a.toArray(new Feature[] {});
	}

	public static JsonElement loadSource(String file) throws IOException {
		JsonParser parser = new JsonParser();

		JsonElement root = null;

		File f = new File(file);
		if (!f.exists()) {
			root = cache.getJSONDocument(file);
		} else {
			FileReader fr = new FileReader(f);
			root = parser.parse(fr);
		}

		return root;
	}
}
