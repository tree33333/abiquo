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
// Generated on: 2012.02.23 at 01:07:44 PM CET 
//


package org.dmtf.schemas.ovf.envelope._1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.dmtf.schemas.ovf.envelope._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProductSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "ProductSection");
    private final static QName _InstallSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "InstallSection");
    private final static QName _VirtualSystemCollection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "VirtualSystemCollection");
    private final static QName _VirtualSystem_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "VirtualSystem");
    private final static QName _DiskSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "DiskSection");
    private final static QName _DeploymentOptionSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "DeploymentOptionSection");
    private final static QName _StartupSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "StartupSection");
    private final static QName _VirtualHardwareSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "VirtualHardwareSection");
    private final static QName _ResourceAllocationSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "ResourceAllocationSection");
    private final static QName _Section_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "Section");
    private final static QName _CustomNetworkSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "CustomNetworkSection");
    private final static QName _Envelope_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "Envelope");
    private final static QName _OperatingSystemSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "OperatingSystemSection");
    private final static QName _Content_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "Content");
    private final static QName _EulaSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "EulaSection");
    private final static QName _NetworkSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "NetworkSection");
    private final static QName _AnnotationSection_QNAME = new QName("http://schemas.dmtf.org/ovf/envelope/1", "AnnotationSection");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.dmtf.schemas.ovf.envelope._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProductSectionType.Property }
     * 
     */
    public ProductSectionType.Property createProductSectionTypeProperty() {
        return new ProductSectionType.Property();
    }

    /**
     * Create an instance of {@link ReferencesType }
     * 
     */
    public ReferencesType createReferencesType() {
        return new ReferencesType();
    }

    /**
     * Create an instance of {@link DeploymentOptionSectionType }
     * 
     */
    public DeploymentOptionSectionType createDeploymentOptionSectionType() {
        return new DeploymentOptionSectionType();
    }

    /**
     * Create an instance of {@link StringsType }
     * 
     */
    public StringsType createStringsType() {
        return new StringsType();
    }

    /**
     * Create an instance of {@link EnvelopeType }
     * 
     */
    public EnvelopeType createEnvelopeType() {
        return new EnvelopeType();
    }

    /**
     * Create an instance of {@link OperatingSystemSectionType }
     * 
     */
    public OperatingSystemSectionType createOperatingSystemSectionType() {
        return new OperatingSystemSectionType();
    }

    /**
     * Create an instance of {@link DeploymentOptionSectionType.Configuration }
     * 
     */
    public DeploymentOptionSectionType.Configuration createDeploymentOptionSectionTypeConfiguration() {
        return new DeploymentOptionSectionType.Configuration();
    }

    /**
     * Create an instance of {@link NetworkConfigurationType }
     * 
     */
    public NetworkConfigurationType createNetworkConfigurationType() {
        return new NetworkConfigurationType();
    }

    /**
     * Create an instance of {@link NetworkSectionType.Network }
     * 
     */
    public NetworkSectionType.Network createNetworkSectionTypeNetwork() {
        return new NetworkSectionType.Network();
    }

    /**
     * Create an instance of {@link VirtualHardwareSectionType }
     * 
     */
    public VirtualHardwareSectionType createVirtualHardwareSectionType() {
        return new VirtualHardwareSectionType();
    }

    /**
     * Create an instance of {@link InstallSectionType }
     * 
     */
    public InstallSectionType createInstallSectionType() {
        return new InstallSectionType();
    }

    /**
     * Create an instance of {@link PropertyConfigurationValueType }
     * 
     */
    public PropertyConfigurationValueType createPropertyConfigurationValueType() {
        return new PropertyConfigurationValueType();
    }

    /**
     * Create an instance of {@link DHCPOption }
     * 
     */
    public DHCPOption createDHCPOption() {
        return new DHCPOption();
    }

    /**
     * Create an instance of {@link VirtualSystemType }
     * 
     */
    public VirtualSystemType createVirtualSystemType() {
        return new VirtualSystemType();
    }

    /**
     * Create an instance of {@link AbicloudNetworkType }
     * 
     */
    public AbicloudNetworkType createAbicloudNetworkType() {
        return new AbicloudNetworkType();
    }

    /**
     * Create an instance of {@link VirtualSystemCollectionType }
     * 
     */
    public VirtualSystemCollectionType createVirtualSystemCollectionType() {
        return new VirtualSystemCollectionType();
    }

    /**
     * Create an instance of {@link IpPoolType }
     * 
     */
    public IpPoolType createIpPoolType() {
        return new IpPoolType();
    }

    /**
     * Create an instance of {@link MsgType }
     * 
     */
    public MsgType createMsgType() {
        return new MsgType();
    }

    /**
     * Create an instance of {@link ProductSectionType }
     * 
     */
    public ProductSectionType createProductSectionType() {
        return new ProductSectionType();
    }

    /**
     * Create an instance of {@link NetworkSectionType }
     * 
     */
    public NetworkSectionType createNetworkSectionType() {
        return new NetworkSectionType();
    }

    /**
     * Create an instance of {@link StringsType.Msg }
     * 
     */
    public StringsType.Msg createStringsTypeMsg() {
        return new StringsType.Msg();
    }

    /**
     * Create an instance of {@link RASDType }
     * 
     */
    public RASDType createRASDType() {
        return new RASDType();
    }

    /**
     * Create an instance of {@link StartupSectionType.Item }
     * 
     */
    public StartupSectionType.Item createStartupSectionTypeItem() {
        return new StartupSectionType.Item();
    }

    /**
     * Create an instance of {@link OrgNetworkType }
     * 
     */
    public OrgNetworkType createOrgNetworkType() {
        return new OrgNetworkType();
    }

    /**
     * Create an instance of {@link VirtualDiskDescType }
     * 
     */
    public VirtualDiskDescType createVirtualDiskDescType() {
        return new VirtualDiskDescType();
    }

    /**
     * Create an instance of {@link DHCPOptions }
     * 
     */
    public DHCPOptions createDHCPOptions() {
        return new DHCPOptions();
    }

    /**
     * Create an instance of {@link VSSDType }
     * 
     */
    public VSSDType createVSSDType() {
        return new VSSDType();
    }

    /**
     * Create an instance of {@link ResourceAllocationSectionType }
     * 
     */
    public ResourceAllocationSectionType createResourceAllocationSectionType() {
        return new ResourceAllocationSectionType();
    }

    /**
     * Create an instance of {@link AnnotationSectionType }
     * 
     */
    public AnnotationSectionType createAnnotationSectionType() {
        return new AnnotationSectionType();
    }

    /**
     * Create an instance of {@link ProductSectionType.Icon }
     * 
     */
    public ProductSectionType.Icon createProductSectionTypeIcon() {
        return new ProductSectionType.Icon();
    }

    /**
     * Create an instance of {@link DiskSectionType }
     * 
     */
    public DiskSectionType createDiskSectionType() {
        return new DiskSectionType();
    }

    /**
     * Create an instance of {@link EulaSectionType }
     * 
     */
    public EulaSectionType createEulaSectionType() {
        return new EulaSectionType();
    }

    /**
     * Create an instance of {@link FileType }
     * 
     */
    public FileType createFileType() {
        return new FileType();
    }

    /**
     * Create an instance of {@link StartupSectionType }
     * 
     */
    public StartupSectionType createStartupSectionType() {
        return new StartupSectionType();
    }

    /**
     * Create an instance of {@link DHCPServiceType }
     * 
     */
    public DHCPServiceType createDHCPServiceType() {
        return new DHCPServiceType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "ProductSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<ProductSectionType> createProductSection(ProductSectionType value) {
        return new JAXBElement<ProductSectionType>(_ProductSection_QNAME, ProductSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstallSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "InstallSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<InstallSectionType> createInstallSection(InstallSectionType value) {
        return new JAXBElement<InstallSectionType>(_InstallSection_QNAME, InstallSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualSystemCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "VirtualSystemCollection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Content")
    public JAXBElement<VirtualSystemCollectionType> createVirtualSystemCollection(VirtualSystemCollectionType value) {
        return new JAXBElement<VirtualSystemCollectionType>(_VirtualSystemCollection_QNAME, VirtualSystemCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualSystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "VirtualSystem", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Content")
    public JAXBElement<VirtualSystemType> createVirtualSystem(VirtualSystemType value) {
        return new JAXBElement<VirtualSystemType>(_VirtualSystem_QNAME, VirtualSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiskSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "DiskSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<DiskSectionType> createDiskSection(DiskSectionType value) {
        return new JAXBElement<DiskSectionType>(_DiskSection_QNAME, DiskSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeploymentOptionSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "DeploymentOptionSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<DeploymentOptionSectionType> createDeploymentOptionSection(DeploymentOptionSectionType value) {
        return new JAXBElement<DeploymentOptionSectionType>(_DeploymentOptionSection_QNAME, DeploymentOptionSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartupSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "StartupSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<StartupSectionType> createStartupSection(StartupSectionType value) {
        return new JAXBElement<StartupSectionType>(_StartupSection_QNAME, StartupSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualHardwareSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "VirtualHardwareSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<VirtualHardwareSectionType> createVirtualHardwareSection(VirtualHardwareSectionType value) {
        return new JAXBElement<VirtualHardwareSectionType>(_VirtualHardwareSection_QNAME, VirtualHardwareSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResourceAllocationSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "ResourceAllocationSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<ResourceAllocationSectionType> createResourceAllocationSection(ResourceAllocationSectionType value) {
        return new JAXBElement<ResourceAllocationSectionType>(_ResourceAllocationSection_QNAME, ResourceAllocationSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "Section")
    public JAXBElement<SectionType> createSection(SectionType value) {
        return new JAXBElement<SectionType>(_Section_QNAME, SectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbicloudNetworkType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "CustomNetworkSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<AbicloudNetworkType> createCustomNetworkSection(AbicloudNetworkType value) {
        return new JAXBElement<AbicloudNetworkType>(_CustomNetworkSection_QNAME, AbicloudNetworkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "Envelope")
    public JAXBElement<EnvelopeType> createEnvelope(EnvelopeType value) {
        return new JAXBElement<EnvelopeType>(_Envelope_QNAME, EnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperatingSystemSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "OperatingSystemSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<OperatingSystemSectionType> createOperatingSystemSection(OperatingSystemSectionType value) {
        return new JAXBElement<OperatingSystemSectionType>(_OperatingSystemSection_QNAME, OperatingSystemSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "Content")
    public JAXBElement<ContentType> createContent(ContentType value) {
        return new JAXBElement<ContentType>(_Content_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EulaSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "EulaSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<EulaSectionType> createEulaSection(EulaSectionType value) {
        return new JAXBElement<EulaSectionType>(_EulaSection_QNAME, EulaSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NetworkSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "NetworkSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<NetworkSectionType> createNetworkSection(NetworkSectionType value) {
        return new JAXBElement<NetworkSectionType>(_NetworkSection_QNAME, NetworkSectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnnotationSectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.dmtf.org/ovf/envelope/1", name = "AnnotationSection", substitutionHeadNamespace = "http://schemas.dmtf.org/ovf/envelope/1", substitutionHeadName = "Section")
    public JAXBElement<AnnotationSectionType> createAnnotationSection(AnnotationSectionType value) {
        return new JAXBElement<AnnotationSectionType>(_AnnotationSection_QNAME, AnnotationSectionType.class, null, value);
    }

}
