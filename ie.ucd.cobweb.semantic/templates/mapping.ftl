<@map .data_model />

<#macro map body prefix="" >
    <#list body?keys as key>
        <#assign val=body[key] />
        <@print prefix key val />
    </#list>
</#macro>

<#macro print prefix key val>
    <#if val?is_hash>
        <@map val prefix+key+"."/>
    <#elseif val?is_sequence>
        <@list prefix+key val />
    <#else><@display prefix key />
    </#if>
</#macro>

<#macro display prefix key>
${ "$\{" + prefix + key + "}"}
</#macro>

<#macro list name val>${ "<#list " + name + " as item>" }
    <#list val as nest>
        <#if nest?is_hash>
            <@map nest "item." />
        <#elseif nest?is_sequence >
            <@list name+"item" nest />
        <#else>
  ${ "$\{item}" }
        </#if>
        <#break>
    </#list>${ "</#list>" }
</#macro>