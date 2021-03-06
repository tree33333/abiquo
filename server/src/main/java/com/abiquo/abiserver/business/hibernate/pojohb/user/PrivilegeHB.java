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

package com.abiquo.abiserver.business.hibernate.pojohb.user;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.user.Privilege;

// Generated 16-oct-2008 16:52:14 by Hibernate Tools 3.2.1.GA

/**
 * Role generated by hbm2java
 */
public class PrivilegeHB implements java.io.Serializable, IPojoHB<Privilege>
{

    private static final long serialVersionUID = -5172429643785560320L;

    private Integer idPrivilege;

    private String name;

    public Integer getIdPrivilege()
    {
        return idPrivilege;
    }

    public void setIdPrivilege(final Integer idPrivilege)
    {
        this.idPrivilege = idPrivilege;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public Privilege toPojo()
    {
        Privilege p = new Privilege();

        p.setId(idPrivilege);
        p.setName(name);

        return p;
    }
}
