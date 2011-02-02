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

// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.01 at 11:03:17 AM CET 
//


package com.abicloud.model.test.infrastructure;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Datacenter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Datacenter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Machine" type="{http://www.abicloud.com/model/test/infrastructure}Machine" maxOccurs="unbounded"/>
 *         &lt;element name="VirtualDatacenter" type="{http://www.abicloud.com/model/test/infrastructure}VirtualDatacenter" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="RemoteServicesBaseURI" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Datacenter", propOrder = {
    "machine",
    "virtualDatacenter"
})
public class Datacenter {

    @XmlElement(name = "Machine", required = true)
    protected List<Machine> machine;
    @XmlElement(name = "VirtualDatacenter", required = true)
    protected List<VirtualDatacenter> virtualDatacenter;
    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "RemoteServicesBaseURI")
    protected String remoteServicesBaseURI;

    /**
     * Gets the value of the machine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Machine }
     * 
     * 
     */
    public List<Machine> getMachine() {
        if (machine == null) {
            machine = new ArrayList<Machine>();
        }
        return this.machine;
    }

    /**
     * Gets the value of the virtualDatacenter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the virtualDatacenter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVirtualDatacenter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VirtualDatacenter }
     * 
     * 
     */
    public List<VirtualDatacenter> getVirtualDatacenter() {
        if (virtualDatacenter == null) {
            virtualDatacenter = new ArrayList<VirtualDatacenter>();
        }
        return this.virtualDatacenter;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the remoteServicesBaseURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteServicesBaseURI() {
        return remoteServicesBaseURI;
    }

    /**
     * Sets the value of the remoteServicesBaseURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteServicesBaseURI(String value) {
        this.remoteServicesBaseURI = value;
    }

}
