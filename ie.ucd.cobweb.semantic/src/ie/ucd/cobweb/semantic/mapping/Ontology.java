package ie.ucd.cobweb.semantic.mapping;

import java.util.HashMap;

import ie.ucd.cobweb.semantic.SurveyData;
import ie.ucd.cobweb.semantic.SurveyData.Property;
import ie.ucd.cobweb.semantic.cli.FileCache;
import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.jsonld.JSON;
import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Ontology {
	static final String MAPTYPE = "http://prophet.ucd.ie/ontology/cobweb/map#mapType";

	private HashMap<String, ExportType> exports;
	private HashMap<String, ValueConfig> values;

	public Ontology() {
		exports = new HashMap<>();
		values = new HashMap<>();
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

	public void loadMappings(JsonElement root) {
		JsonArray arr = root.getAsJsonArray();
		root = arr.get(0);

		JsonObject base = root.getAsJsonObject();
		arr = base.getAsJsonArray("@graph");

		for (JsonElement el : arr) {
			if (el.isJsonObject()) {
				JsonObject spec = el.getAsJsonObject();
				String key;

				// TODO: multiple concurrent keys
				key = JSON.extractArrayElement(MAPTYPE, JSON.VALUE, spec);
				if (key != null) {
					ValueMap map = ValueMap.extract(spec);
					ExportType type = getType(key);
					if (type != null && map != null)
						type.add(map);
				}

				// TODO: Do better.
				key = JSON.extractArrayElement(Configuration.TEMPLATE,
						JSON.VALUE, spec);
				if (key != null) {
					Configuration conf = Configuration.extract(spec);
					ExportType type = getType(conf.type);
					if (type != null && conf != null)
						type.setConfig(conf);
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
			}
		}
	}

	public void export(SurveyData dv, FileCache cache) {
		for (ExportType type : exports.values()) {
			System.out.println("  Exporting " + type.name);

			Exporter ex = type.new Exporter();
			for (Property prop : dv.properties()) {
				System.out.println("    Property " + prop.type);
				//System.out.println(" & " + prop.value);
				ValueConfig conf = values.get(prop.type);
				if (conf != null && conf.mappings != null) {
					type.export(prop, conf.mappings, ex);
				}
			}

			Geometry g = dv.getGeometry();
			g.export(ex);

			ex.export(type.extension,cache.getFile(type.conf.template));
		}
	}
}
