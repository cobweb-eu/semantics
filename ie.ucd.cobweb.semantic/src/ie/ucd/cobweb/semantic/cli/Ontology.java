package ie.ucd.cobweb.semantic.cli;

import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;

import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.jsonld.Context;
import ie.ucd.cobweb.semantic.jsonld.JSON;
import ie.ucd.cobweb.semantic.jsonld.OWL;
import ie.ucd.cobweb.semantic.mapping.Configuration;
import ie.ucd.cobweb.semantic.mapping.ExportType;
import ie.ucd.cobweb.semantic.mapping.ValueConfig;
import ie.ucd.cobweb.semantic.mapping.ValueConstant;
import ie.ucd.cobweb.semantic.mapping.ValueMap;
import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Ontology implements Serializable {
	private static final long serialVersionUID = -3482278333598221858L;

	static final String MAPTYPE = "http://prophet.ucd.ie/ontology/cobweb/map#mapType";

	private HashMap<String, ExportType> exports;
	private HashMap<String, ValueConfig> values;
	private HashSet<String> imports;

	public Ontology() {
		exports = new HashMap<>();
		values = new HashMap<>();
		imports = new HashSet<>();
	}

	private ExportType getType(String mappingKey) {
		if (mappingKey == null)
			return null;

		ExportType type = exports.get(mappingKey);
		if (type == null) {
			type = new ExportType(mappingKey);
			exports.put(mappingKey, type);
		}
		return type;
	}

	public void loadMappings(JsonElement root, FileCache cache) {
		JsonArray arr = root.getAsJsonArray();
		root = arr.get(0);

		JsonObject base = root.getAsJsonObject();

		String url = JSON.extractID(base);
		if (imports.contains(url)) {
			// Avoid cyclical imports.
			return;
		}
		imports.add(url);
		arr = base.getAsJsonArray("@graph");

		for (JsonElement el : arr) {
			if (el.isJsonObject()) {
				JsonObject spec = el.getAsJsonObject();
				String key;

				// TODO multiple concurrent keys
				key = JSON.extractArrayElement(MAPTYPE, JSON.VALUE, spec);
				if (key != null) {
					ValueMap map = ValueMap.extract(spec);
					ExportType type = getType(key);
					if (type != null && map != null)
						type.add(map);
				}

				// TODO Redo.
				key = JSON.extractArrayElement(Configuration.TEMPLATE,
						JSON.VALUE, spec);
				if (key != null) {
					Configuration conf = Configuration.extract(spec);
					ExportType type = getType(conf.type);
					if (type != null && conf != null)
						type.setConfig(conf);
				}
				// TODO Also redo.
				key = JSON.extractArrayElement(Configuration.TEMPLATE_COLLECTION,
						JSON.VALUE, spec);
				if (key != null) {
					//TODO Extract supercollection identifier.
				}

				key = JSON.extractArrayElement(ValueConstant.KEY, JSON.VALUE,
						spec);
				if (key != null) {
					ValueConstant vc = ValueConstant.extract(spec);
					ExportType type = getType(vc.type);
					if (type != null && vc != null)
						type.add(vc);
				}

				key = JSON.extractArrayElement(ValueConfig.MAPPING, JSON.ID,
						spec);
				if (key != null) {
					ValueConfig config = ValueConfig.extract(spec);
					values.putIfAbsent(config.id, config);
				}

				String[] imports = JSON.extractArrayIDs(OWL.IMPORTS, spec);
				loadImports(imports, cache);
			}
		}
	}

	private void loadImports(String[] imports, FileCache cache) {
		for (String imp : imports) {
			load(imp, cache);
		}
	}

	public static Ontology initiate(Context context, FileCache cache) {
		Ontology o = cache.getOntology(context.base);
		return o;
	}

	protected void load(String base, FileCache cache) {
		if (cache == null)
			return;

		JsonElement root = cache.getJSONDocument(base);
		loadMappings(root, cache);
	}

	public void export(DataPoint point, FileCache cache) {
		for (ExportType type : exports.values()) {
			System.out.println("  Exporting " + type.name);

			Exporter ex = type.new Exporter();
			for (DataPoint.Property prop : point.properties()) {
				System.out.println("    Property " + prop.type);
				// System.out.println(" & " + prop.value);
				ValueConfig conf = values.get(prop.type);
				if (conf != null && conf.mappings != null) {
					type.export(prop, conf.mappings, ex);
				}
			}

			Geometry g = point.getGeometry();
			g.export(ex);

			ex.export(type.extension, type.getConf().template, cache);
		}
	}
}
