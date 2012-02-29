//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.23 at 01:07:44 PM CET 
//


package org.dmtf.schemas.wbem.wscim._1.common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ConsumerVisibility;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.MappingBehavior;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ResourceType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticRecoveryAction;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticShutdownAction;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticStartupAction;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for cimAnySimpleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cimAnySimpleType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anySimpleType">
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cimAnySimpleType", propOrder = {
    "value"
})
@XmlSeeAlso({
    ResourceType.class,
    ConsumerVisibility.class,
    MappingBehavior.class,
    AutomaticRecoveryAction.class,
    AutomaticStartupAction.class,
    AutomaticShutdownAction.class
})
public class CimAnySimpleType {

    @XmlValue
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "anySimpleType")
    protected String value;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}