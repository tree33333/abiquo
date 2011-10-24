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

package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaCategoryDAO")
/* package */class CategoryDAO extends DefaultDAOBase<Integer, Category>
{
    public CategoryDAO()
    {
        super(Category.class);
    }

    public CategoryDAO(final EntityManager entityManager)
    {
        super(Category.class, entityManager);
    }

    public Category findDefault()
    {
        return getSingleResult(createCriteria(isDefault()));
    }

    public Category findByName(final String categoryName)
    {
        return findUniqueByProperty(Category.NAME_PROPERTY, categoryName);
    }

    public boolean existCategoryWithSameName(final String name)
    {
        return existsAnyByCriterions(equalsName(name));
    }

    private static Criterion equalsName(final String name)
    {
        return Restrictions.eq(Category.NAME_PROPERTY, name);
    }

    private Criterion isDefault()
    {
        return Restrictions.eq(Category.DEFAULT_PROPERTY, true);
    }

}
