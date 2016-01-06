package ie.ucd.cobweb.semantic.mapping;

import ie.ucd.cobweb.semantic.SurveyData.Property;
import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.mapping.ExportType.Exporter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExportType {

	private Map<String, ValueMap> maps;
	private Set<ValueConstant> constants;
	
	private Configuration conf;
	final String name;

	public ExportType(String name) {
		this.name = name;
		this.maps = new HashMap<>();
		this.constants = new HashSet<>();
	}

	public void add(ValueMap map) {
		this.maps.putIfAbsent(map.id, map);
	}

	public void add(ValueConstant vc) {
		constants.add(vc);
	}

	public void setConfig(Configuration conf) {
		this.conf = conf;
	}

	public void export(Property prop, String[] mappings, Exporter ex) {
		for(String mapping: mappings) {
			System.out.println("      Mapping -> " + mapping);
			ValueMap map = this.maps.get(mapping);
			if (map != null)
				map.export(prop, ex);
		}
	}

	public class Exporter {
		private HashMap<String, String> map;

		public Exporter() {
			this.map = new HashMap<>();
			for (ValueConstant c : constants) {
				map.put(c.key, c.val);
			}
		}

		public void set(String key, String value) {
			map.put(key, value);
		}

		public void export() {
			System.out.println();
			
			for(String key: map.keySet()) {
				System.out.println(String.format("  %s - %s", key, map.get(key)));
			}
		}
	}
}
