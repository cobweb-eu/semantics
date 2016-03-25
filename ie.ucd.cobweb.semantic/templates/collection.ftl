<#--include "mapping.ftl"><#-- -->
<?xml version="1.0" encoding="UTF-8"?>
<cs:CitizenScienceObservationCollection 
        xmlns:xlink="http://www.w3.org/1999/xlink" 
        xmlns:gco="http://www.isotc211.org/2005/gco" 
        xmlns:gmd="http://www.isotc211.org/2005/gmd" 
        xmlns:gml="http://www.opengis.net/gml/3.2" 
        xmlns:swe="http://www.opengis.net/swe/2.0" 
        xmlns:sml="http://www.opengis.net/sensorml/2.0" 
        xmlns:gss="http://www.isotc211.org/2005/gss" 
        xmlns:cs="http://www.opengis.org/citizenscience/1.1" 
        xmlns:om="http://www.opengis.net/om/2.0" 
        xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" 
        xmlns:sf="http://www.opengis.net/sampling/2.0" 
        xmlns:gts="http://www.isotc211.org/2005/gts" 
        xmlns:gsr="http://www.isotc211.org/2005/gsr" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="data/citizenScience.xsd" 
        gml:id="${ID}">
  <cs:resultTime>
    <gml:TimeInstant gml:id="day1">
      <gml:timePosition>${time}</gml:timePosition>
    </gml:TimeInstant>
  </cs:resultTime>
  <cs:metadata>
    <#--#list meta as campaign-->
    <cs:CitizenScienceMetadata gml:id="${meta.ID}">
      <cs:samplingCampaign>${meta.campaign}</cs:samplingCampaign>
    </cs:CitizenScienceMetadata>
    <#--/#list-->
  </cs:metadata>
  <cs:procedure>
    <#--#list procedure as campaign-->
    <cs:CampaignProcess gml:id="${procedure.ID}">
      <cs:samplingProtocol>${procedure.protocol}</cs:samplingProtocol>
      <cs:member>
        <cs:CitizenProcess gml:id="survey">
          <#list procedure.people as person>
          <cs:recordedBy>
            <cs:CitizenScientist gml:id="${person.ID}">
              <cs:givenName>${person.name}</cs:givenName>
              <cs:lastName></cs:lastName>
            </cs:CitizenScientist>
          </cs:recordedBy>
          </#list>
        </cs:CitizenProcess>
      </cs:member>
    </cs:CampaignProcess>
    <#--/#list-->
  </cs:procedure>
  
  <cs:observedProperty xlink:href="${observedProperty}" />
  <#if exporter?size = 1>
  <cs:result>
    <swe:DataArray>
      <swe:elementCount>
        <swe:Count>
          <swe:value>${exporter?size}</swe:value>
        </swe:Count>
      </swe:elementCount>
      <#assign counter = 1>
      <#list exporter as exporter>
      <swe:elementType name="resultRecordStructure${counter}"><#assign counter = counter+1>
        <swe:DataRecord id="${exporter.ID}">
          <#list exporter.results as field>
          <swe:field name="${field.name}">
            <swe:${field.type} definition="${field.definition}"/>
          </swe:field>
          <#--break><#-- -->
          </#list>
        </swe:DataRecord>
      </swe:elementType>
      <swe:encoding>
        <swe:TextEncoding tokenSeparator="${exporter.seperator}" blockSeparator="&#32;" decimalSeparator="."/>
      </swe:encoding>
      <#--break><#-- -->
      </#list>
    </swe:DataArray>
  </cs:result>
  </#if>
  <cs:featureOfInterest xlink:href="${featureOfInterest}"/>
  <#list results as item>
  <cs:member>
    <cs:CitizenScienceObservation gml:id="${item.ID}">
      <om:phenomenonTime>
        <gml:TimeInstant gml:id="${item.time.ID}">
          <gml:timePosition>${item.time.time}</gml:timePosition>
        </gml:TimeInstant>
      </om:phenomenonTime>
      <om:resultTime xlink:href="${item.time.ID}" />
      <!-- procedure and observedProperty shall be removed, as they are promoted to collection level -->
      <om:procedure/>
      <om:observedProperty/>
      <!-- The collection is a simple collection of instantenous observations, thus the FoI implements SF_Point. -->
      <om:featureOfInterest>
        <sams:SF_SpatialSamplingFeature gml:id="${item.featureOfInterest.ID}">
          <sf:type xlink:href="${item.featureOfInterest.type}"/>
          <sf:sampledFeature xlink:href="${item.featureOfInterest.sampled}"/>
          <sams:shape>
            ${item.featureOfInterest.shape.shape}
          </sams:shape>
        </sams:SF_SpatialSamplingFeature>
      </om:featureOfInterest>
      <#if exporter?size = 1>
      <om:result xsi:type="swe:DataArrayPropertyType">
        <swe:DataArray>
          <swe:elementCount/>
          <swe:elementType name="resultType" xlink:href="${item.results.ID}"/>
          <swe:values><#list item.resultsE.value as value>${value?html}<#sep>${item.results.seperator}</#list></swe:values>
        </swe:DataArray>
      </om:result>
      <#else>
      <om:result>
        <swe:DataRecord>
          <#list item.results as result>
          <swe:field name="${result.name}">
            <swe:${result.type} definition="${result.definition}">
              <swe:value>${result.value?html}</swe:value>
            </swe:${result.type}>
          </swe:field>
          </#list>
        </swe:DataRecord>   
      </om:result>       
      </#if>
    </cs:CitizenScienceObservation>
  </cs:member>
  <#--break><#-- -->
  </#list>
</cs:CitizenScienceObservationCollection>