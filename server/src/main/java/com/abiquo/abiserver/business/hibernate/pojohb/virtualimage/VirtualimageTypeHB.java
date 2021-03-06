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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualimage;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageType;
import com.abiquo.model.enumerator.HypervisorType;

public class VirtualimageTypeHB implements java.io.Serializable, IPojoHB<VirtualImageType>
{

    private static final long serialVersionUID = -1350548200049954363L;

    private int id;

    private String extension;

    private HypervisorType type;

    /**
     * 0 - Not statefull 1 - Statefull
     */
    private int idType;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public int getIdType()
    {
        return idType;
    }

    public void setIdType(int idType)
    {
        this.idType = idType;
    }

    public VirtualImageType toPojo()
    {
        VirtualImageType virtualImageType = new VirtualImageType();

        virtualImageType.setId(id);
        virtualImageType.setExtension(extension);
        virtualImageType.setHypervisorType(new HyperVisorType(type));
        virtualImageType.setIdType(idType);

        return virtualImageType;
    }

    public HypervisorType getType()
    {
        return type;
    }

    public void setType(HypervisorType type)
    {
        this.type = type;
    }
}
