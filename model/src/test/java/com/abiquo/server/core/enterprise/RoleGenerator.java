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

package com.abiquo.server.core.enterprise;

import java.util.Collection;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RoleGenerator extends DefaultEntityGenerator<Role>
{
    PrivilegeGenerator privilegeGenerator;

    public RoleGenerator(final SeedGenerator seed)
    {
        super(seed);
        privilegeGenerator = new PrivilegeGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Role obj1, final Role obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Role.NAME_PROPERTY, Role.BLOCKED_PROPERTY,
            Role.ENTERPRISE_PROPERTY);
    }

    @Override
    public Role createUniqueInstance()
    {
        Privilege p = privilegeGenerator.createInstance();
        return createInstance(p);

    }

    public Role createInstance()
    {
        String name = newString(nextSeed(), Role.NAME_LENGTH_MIN, Role.NAME_LENGTH_MAX);
        boolean blocked = false;

        Role role = new Role(name, blocked);

        return role;
    }

    public Role createInstance(final Privilege... privileges)
    {
        Role role = createInstance();
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Role entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Collection<Privilege> privileges = entity.getPrivileges();
        for (Privilege privilege : privileges)
        {
            privilegeGenerator.addAuxiliaryEntitiesToPersist(privilege, entitiesToPersist);
            entitiesToPersist.add(privilege);
        }

    }
}
