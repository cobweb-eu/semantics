package ie.ucd.cobweb.semantic.swe4cs;

import java.util.HashMap;
import java.util.Map;

import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.jsonld.Context;

public class SWEObservation {

	public final String id;
	public final String collection;
	public final Timestamp time;
	public final Procedure procedure;
	public final String observedProperty;
	public final FeatureOfInterest foi;
	public final DataRecord data;

	private SWEObservation(String id, String collection, String campaign,
			Timestamp time, Procedure proc, String observedProperty,
			FeatureOfInterest foi, DataRecord data) {
		this.id = id;
		this.collection = collection;
		this.time = time;
		this.procedure = proc;
		this.observedProperty = observedProperty;
		this.foi = foi;
		this.data = data;
	}

	public static SWEObservation build(DataPoint point, Context context) {
		String id = point.id;
		Timestamp time = Timestamp.build(point);
		Procedure proc = Procedure.build(point, context);
		String observedProperty = context.base + "observedProperty";
		FeatureOfInterest foi = FeatureOfInterest.build(point, context);
		DataRecord data = DataRecord.build(point);

		String collection = point.editor;
		String campaign = context.base;
		return new SWEObservation(id, collection, campaign, time, proc,
				observedProperty, foi, data);
	}

	/***
	 * Single Item Structure:
	 */
	// Observation -id
	// -PhenomenonTimestamp -id
	// -Procedure
	// --CitizenProcess -id
	// ---Sampling Protocol
	// ---Person -id
	// -ObservedProperty
	// -FeatureOfInterest -id
	// --Type -id
	// --Location -id
	// -Result-DataRecord
	// --Name
	// --Definition
	// --Value
	public Map<String, Object> collapse(boolean single) {
		Map<String, Object> map = new HashMap<>();

		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.TIME, time.collapse());
		map.put(SWE4CS.PROCEDURE, procedure.collapse());
		map.put(SWE4CS.OBSPROP, observedProperty);
		map.put(SWE4CS.FOI, foi.collapse());

		map.put(SWE4CS.RESULT, data.collapse());
		if (!single) {
			map.put(SWE4CS.RESULT_ENC, data.encode());
		}

		return map;
	}

	public void collapseEncoder(Map<String, Object> enc) {
		data.collapseEncoder(enc);
	}

}
