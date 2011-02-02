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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VirtualDiskEnumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VirtualDiskEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="http://unknown"/>
 *     &lt;enumeration value="http://raw"/>
 *     &lt;enumeration value="http://incompatible"/>
 *     &lt;enumeration value="http://stateful"/>
 *     &lt;enumeration value="http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized"/>
 *     &lt;enumeration value="http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat"/>
 *     &lt;enumeration value="http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse"/>
 *     &lt;enumeration value="http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat"/>
 *     &lt;enumeration value="http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse"/>
 *     &lt;enumeration value="http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat"/>
 *     &lt;enumeration value="http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse"/>
 *     &lt;enumeration value="http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat"/>
 *     &lt;enumeration value="http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VirtualDiskEnumType")
@XmlEnum
public enum VirtualDiskEnumType {

    @XmlEnumValue("http://unknown")
    UNKNOWN("http://unknown"),
    @XmlEnumValue("http://raw")
    RAW("http://raw"),
    @XmlEnumValue("http://incompatible")
    INCOMPATIBLE("http://incompatible"),
    @XmlEnumValue("http://stateful")
    STATEFUL("http://stateful"),
    @XmlEnumValue("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized")
    VMDK_STREAM_OPTIMIZED("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized"),
    @XmlEnumValue("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat")
    VMDK_FLAT("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat"),
    @XmlEnumValue("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse")
    VMDK_MONOLITHIC_SPARSE("http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse"),
    @XmlEnumValue("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat")
    VHD_FLAT("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat"),
    @XmlEnumValue("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse")
    VHD_SPARSE("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse"),
    @XmlEnumValue("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat")
    VDI_FLAT("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat"),
    @XmlEnumValue("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse")
    VDI_SPARSE("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse"),
    @XmlEnumValue("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat")
    QCOW2_FLAT("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat"),
    @XmlEnumValue("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse")
    QCOW2_SPARSE("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse");
    private final String value;

    VirtualDiskEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VirtualDiskEnumType fromValue(String v) {
        for (VirtualDiskEnumType c: VirtualDiskEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
