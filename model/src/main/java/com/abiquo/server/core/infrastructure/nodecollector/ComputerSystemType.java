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
// Generated on: 2010.12.24 at 11:14:06 AM CET 
//


package com.abiquo.server.core.infrastructure.nodecollector;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Class computer system POJO contains all the shared capabilities for physical and virtual systems
 *             
 * 
 * <p>Java class for ComputerSystemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ComputerSystemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ram" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="resources" type="{http://abiquo.com/server/core/infrastructure/nodecollector}ResourceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComputerSystemType", propOrder = {
    "name",
    "cpu",
    "ram",
    "resources"
})
@XmlSeeAlso({
    VirtualSystemDto.class,
    HostDto.class
})
public abstract class ComputerSystemType {

    @XmlElement(required = true)
    protected String name;
    protected long cpu;
    protected long ram;
    @XmlElement(nillable = true)
    protected List<ResourceType> resources;

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
     * Gets the value of the cpu property.
     * 
     */
    public long getCpu() {
        return cpu;
    }

    /**
     * Sets the value of the cpu property.
     * 
     */
    public void setCpu(long value) {
        this.cpu = value;
    }

    /**
     * Gets the value of the ram property.
     * 
     */
    public long getRam() {
        return ram;
    }

    /**
     * Sets the value of the ram property.
     * 
     */
    public void setRam(long value) {
        this.ram = value;
    }

    /**
     * Gets the value of the resources property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resources property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResources().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceType }
     * 
     * 
     */
    public List<ResourceType> getResources() {
        if (resources == null) {
            resources = new ArrayList<ResourceType>();
        }
        return this.resources;
    }

}
