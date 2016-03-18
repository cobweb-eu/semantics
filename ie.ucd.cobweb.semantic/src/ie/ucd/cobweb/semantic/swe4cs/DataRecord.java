package ie.ucd.cobweb.semantic.swe4cs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.utility.HtmlEscape;
import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.DataPoint.Property;

public class DataRecord {

	private static Map<String, Encoder> map = new HashMap<>();
	private Map<String, Property> properties;
	private Encoder encoder;

	private DataRecord(Map<String, Property> properties, Encoder enc) {
		this.properties = properties;
		this.encoder = enc;
	}

	public static DataRecord build(DataPoint point) {
		String identifier = point.getFingerprint();
		Map<String, Property> properties = pullProperties(point);

		Encoder enc = Encoder.get(identifier, properties.keySet());

		return new DataRecord(properties, enc);
	}

	private static Map<String, Property> pullProperties(DataPoint point) {
		Map<String, Property> properties = new HashMap<>();

		Collection<Property> props = point.properties();
		for (Property p : props) {
			properties.put(p.type, p);
		}

		return properties;
	}

	public static class Encoder {
		private static final String SEP = "@@";

		private String identifier;
		public final int length;
		private final String[] keys;

		private String id;

		public Encoder(String identifier, Collection<String> props) {
			this.identifier = identifier;
			this.id = IDGen.getID("enc", identifier);
			this.length = props.size();
			String[] keys = props.toArray(new String[] {});
			Arrays.sort(keys);
			this.keys = keys;
		}

		public static Encoder get(String identifier, Collection<String> props) {
			Encoder e = map.get(identifier);

			if (e == null) {
				e = new Encoder(identifier, props);
				map.put(identifier, e);
			} else {
				// Sanity check.
				if (e.length != props.size())
					throw new IllegalStateException(
							"Misaligned fingerprint detected while extracting Encoder");
			}

			return e;
		}

		public Map<String, Object> encode(DataRecord dataRecord) {
			Map<String, Object> map = new HashMap<>();
			List<String> vals = new ArrayList<>();

			for (String key : keys) {
				String value = dataRecord.properties.get(key).value;
				vals.add(value);
			}

			map.put(SWE4CS.ID, id);
			map.put(SWE4CS.VALUE, vals);
			map.put(SWE4CS.SEPERATOR, SEP);

			return map;
		}

		public void collapse(Map<String, Object> map) {
			if(map.containsKey(id))
				return;
			
			Map<String, Object> enc = new HashMap<>();
			enc.put(SWE4CS.ID, id);
			enc.put(SWE4CS.SEPERATOR, SEP);
			
			List<Object> props = new ArrayList<>();
			for (String key : keys) {
				Map<String, Object> entry = new HashMap<>();
				String name = getName(key);

				entry.put(SWE4CS.NAME, name);
				entry.put(SWE4CS.DEFINITION, key);
				entry.put(SWE4CS.TYPE, getType(name));

				props.add(entry);
			}
			
			enc.put(SWE4CS.RESULT, props);
			map.put(id, enc);
		}
	}

	// -Result-DataRecord
	// --Name
	// --Definition
	// --Value

	public List<Object> collapse() {
		List<Object> map = new ArrayList<>();

		for (Property p : properties.values()) {
			Map<String, Object> entry = new HashMap<>();
			String name = getName(p.type);

			entry.put(SWE4CS.NAME, name);
			entry.put(SWE4CS.DEFINITION, p.type);
			entry.put(SWE4CS.VALUE, p.value);
			entry.put(SWE4CS.TYPE, getType(name));

			map.add(entry);
		}

		return map;
	}

	// ----RecordStructure - id
	// -----Name
	// -----Definition
	// ---Result-DataArray
	// ----Ref (RecordStructure -id)
	// ----Values

	private static String getType(String name) {
		// if( map.contains(name) )
		// return map.get(name);
		return "Text";
	}

	private static String getName(String type) {
		int idx = type.lastIndexOf("#");
		if (idx > 0)
			return type.substring(idx + 1);
		return null;
	}

	public Map<String, Object> encode() {
		return encoder.encode(this);
	}

	public void collapseEncoder(Map<String, Object> map) {
		encoder.collapse(map);
	}
}
