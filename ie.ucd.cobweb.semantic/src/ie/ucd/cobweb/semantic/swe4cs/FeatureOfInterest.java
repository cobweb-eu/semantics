package ie.ucd.cobweb.semantic.swe4cs;

import java.util.HashMap;
import java.util.Map;

import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.geojson.Geometry;
import ie.ucd.cobweb.semantic.jsonld.Context;

public class FeatureOfInterest {

	private String id;
	private Geometry geo;
	private String type;
	private String gid;
	String sampled;

	public FeatureOfInterest(String id, Geometry geo, String type, String sampled, String gid) {
		this.id = id;
		this.geo = geo;
		this.type = type;
		this.gid = gid;
		this.sampled=sampled;
	}

	public static FeatureOfInterest build(DataPoint point, Context context) {
		String foi = IDGen.getUID("foi");

		Geometry geo = point.getGeometry();

		// TODO: Switch based on geo.
		String type = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

		String gid = IDGen.getUID("loc");
		String sampled = context.base + "sampledFeature";

		return new FeatureOfInterest(foi, geo, type, sampled, gid);
	}

	// -FeatureOfInterest -id
	// --Type -id
	// --Location -id
	public Map<String, Object> collapse() {
		Map<String, Object> map = new HashMap<>();

		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.TYPE, type);
		map.put(SWE4CS.SAMPLED, sampled);
		map.put(SWE4CS.SHAPE, geo.collapse(gid));

		return map;
	}
}
