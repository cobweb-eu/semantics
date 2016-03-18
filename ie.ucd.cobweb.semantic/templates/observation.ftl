<?xml version="1.0" encoding="UTF-8"?>
<cs:CitizenScienceObservation
        xmlns:xlink="http://www.w3.org/1999/xlink" 
        xmlns:gco="http://www.isotc211.org/2005/gco" 
        xmlns:gmd="http://www.isotc211.org/2005/gmd" 
        xmlns:gml="http://www.opengis.net/gml/3.2" 
        xmlns:swe="http://www.opengis.net/swe/2.0" 
        xmlns:om="http://www.opengis.net/om/2.0" 
        xmlns:sf="http://www.opengis.net/sampling/2.0" 
        xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" 
        xmlns:sml="http://www.opengis.net/sensorml/2.0" 
        xmlns:gts="http://www.isotc211.org/2005/gts" 
        xmlns:gsr="http://www.isotc211.org/2005/gsr" 
        xmlns:gss="http://www.isotc211.org/2005/gss" 
        xmlns:cs="http://www.opengis.org/citizenscience/1.1" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="data/citizenScience.xsd" 
        gml:id="${ID}">
  <om:phenomenonTime>
    <gml:TimeInstant gml:id="${time.ID}">
      <gml:timePosition>${time.time}</gml:timePosition>
    </gml:TimeInstant>
  </om:phenomenonTime>
  <om:resultTime xlink:href="#${time.ID}"/>
  <om:procedure>
    <cs:CitizenProcess gml:id="${procedure.process}">
      <cs:samplingProtocol>${procedure.protocol}</cs:samplingProtocol>
      <cs:recordedBy>
        <cs:CitizenScientist gml:id="${procedure.ID}">
          <cs:givenName>${procedure.name?html}</cs:givenName>
          <cs:lastName></cs:lastName>
        </cs:CitizenScientist>
      </cs:recordedBy>
    </cs:CitizenProcess>
  </om:procedure>
  <om:observedProperty xlink:href="${observedProperty}"/>
  <om:featureOfInterest>
    <sams:SF_SpatialSamplingFeature gml:id="${featureOfInterest.ID}">
      <sf:type xlink:href="${featureOfInterest.type}"/>
      <sf:sampledFeature xlink:href="${featureOfInterest.sampled}"/>
      <sams:shape>
        ${featureOfInterest.shape.shape}
      </sams:shape>
    </sams:SF_SpatialSamplingFeature>
  </om:featureOfInterest>
  <om:result>
    <swe:DataRecord>
<#list results as result>
      <swe:field name="${result.name}">
        <swe:${result.type} definition="${result.definition}">
          <swe:value>${result.value?html}</swe:value>
        </swe:${result.type}>
      </swe:field>
</#list>
    </swe:DataRecord>
  </om:result>
</cs:CitizenScienceObservation>
