package ie.ucd.cobweb.semantic.swe4cs;

import java.util.HashMap;
import java.util.Map;

import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.jsonld.Context;

public class Procedure {

	public final String process;
	public final String protocol;
	public final String name;
	public final String id;

	private Procedure(String pid, String protocol, String name, String nid) {
		this.process = pid;
		this.protocol = protocol;
		this.name = name;
		this.id = nid;
	}

	public static Procedure build(DataPoint point, Context context) {
		String protocol = context.base + "samplingProtocol";
		String pid = IDGen.getID("process", protocol);
		
		String name = point.name;
		String nid = IDGen.getID("person", name);

		return new Procedure(pid, protocol, name, nid);
	}

	// -Procedure
	// --CitizenProcess -id
	// ---Sampling Protocol
	// ---Person -id
	public Map<String, Object> collapse() {
		Map<String, Object> map = new HashMap<>();
		
		map.put(SWE4CS.PROCESS, process);
		map.put(SWE4CS.PROTOCOL, protocol);
		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.NAME, name);
		
		return map;
	}

}
