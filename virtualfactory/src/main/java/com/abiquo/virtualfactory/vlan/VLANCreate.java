/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.20 at 10:17:04 AM CEST 
//


package com.abiquo.virtualfactory.vlan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				SOAP body for CREATE ''vlan'' resource:
 * 				It indicates that the VLAN (''vlan'') should be created if not existing.
 * 			
 * 
 * <p>Java class for VLANCreate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VLANCreate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="vlan" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="bridgeName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="externalInterface" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VLANCreate")
public class VLANCreate {

    @XmlAttribute(namespace = "http://abiquo.com/virtualfactory/vlan.xsd", required = true)
    protected String vlan;
    @XmlAttribute(namespace = "http://abiquo.com/virtualfactory/vlan.xsd", required = true)
    protected String bridgeName;
    @XmlAttribute(namespace = "http://abiquo.com/virtualfactory/vlan.xsd", required = true)
    protected String externalInterface;

    /**
     * Gets the value of the vlan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVlan() {
        return vlan;
    }

    /**
     * Sets the value of the vlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVlan(String value) {
        this.vlan = value;
    }

    /**
     * Gets the value of the bridgeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBridgeName() {
        return bridgeName;
    }

    /**
     * Sets the value of the bridgeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBridgeName(String value) {
        this.bridgeName = value;
    }

    /**
     * Gets the value of the externalInterface property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalInterface() {
        return externalInterface;
    }

    /**
     * Sets the value of the externalInterface property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalInterface(String value) {
        this.externalInterface = value;
    }

}