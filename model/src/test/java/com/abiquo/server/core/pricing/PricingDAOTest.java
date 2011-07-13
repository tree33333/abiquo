package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class PricingDAOTest extends DefaultDAOTestBase<PricingDAO, Pricing>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected PricingDAO createDao(EntityManager entityManager)
    {
        return new PricingDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Pricing> createEntityInstanceGenerator()
    {
        return new PricingGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public PricingGenerator eg()
    {
        return (PricingGenerator) super.eg();
    }

    
}
