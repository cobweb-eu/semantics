package ie.ucd.cobweb.semantic.swe4cs;

import java.util.HashMap;
import java.util.Map;

import ie.ucd.cobweb.semantic.DataPoint;

public class Timestamp {

	public final String time;
	public final String id;

	private Timestamp(String time, String id) {
		this.time = time;
		this.id = id;
	}

	public static Timestamp build(DataPoint point) {
		String time = point.timestamp;
		String id = IDGen.getUID("time");
		return new Timestamp(time, id);
	}

	public Map<String, Object> collapse() {
		Map<String, Object> map = new HashMap<>();

		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.TIME, time);

		return map;
	}

}
