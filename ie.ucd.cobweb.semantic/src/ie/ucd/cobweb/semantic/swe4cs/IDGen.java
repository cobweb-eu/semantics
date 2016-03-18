package ie.ucd.cobweb.semantic.swe4cs;

import java.util.HashMap;
import java.util.Map;

public class IDGen {

	private static int counter = 1;
	private static Map<String, Map<String, String>> map = new HashMap<>();

	public static String getID(String prefix, String identifier) {
		Map<String, String> collection = map.get(prefix);
		if (collection == null) {
			collection = new HashMap<>();
			map.put(prefix, collection);
		}

		String id = collection.get(identifier);
		if (id == null) {
			id = prefix + counter++;
			collection.put(identifier, id);
		}

		return id;
	}

	public static String getUID(String prefix) {
		return prefix + counter++;
	}

}
