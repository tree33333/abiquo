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

package com.abiquo.abiserver.pojo.user;

import java.math.BigDecimal;

import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.enterprise.RoleDto;

public class Role implements IPojo<RoleHB>
{
    /* ------------- DB Values for Roles ------------- */
    public final static int SYS_ADMIN = 1;

    public final static int USER = 2;

    public final static int ENTERPRISE_ADMIN = 3;

    /* ------------- Public atributes ------------- */
    private int id;

    private String shortDescription;

    private String largeDescription;

    private BigDecimal securityLevel;

    /* ------------- Constructor ------------- */
    public Role()
    {
        id = 0;
        shortDescription = "";
        largeDescription = "";
        securityLevel = new BigDecimal(0);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getLargeDescription()
    {
        return largeDescription;
    }

    public void setLargeDescription(String largeDescription)
    {
        this.largeDescription = largeDescription;
    }

    public BigDecimal getSecurityLevel()
    {
        return securityLevel;
    }

    public void setSecurityLevel(BigDecimal securityLevel)
    {
        this.securityLevel = securityLevel;
    }

    public RoleHB toPojoHB()
    {
        RoleHB roleHB = new RoleHB();

        roleHB.setIdRole(id);
        roleHB.setShortDescription(shortDescription);
        roleHB.setLargeDescription(largeDescription);
        roleHB.setSecurityLevel(securityLevel);
        return roleHB;
    }

    public static Role create(RoleDto dto)
    {
        Role role = new Role();
        role.setId(dto.getId());
        role.setShortDescription(dto.getShortDescription());
        role.setLargeDescription(dto.getLargeDescription());
        role.setSecurityLevel(new BigDecimal(dto.getSecurityLevel()));

        return role;
    }
}
