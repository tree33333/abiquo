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
package com.abiquo.appliancemanager.web.servlet;

import static com.abiquo.am.data.AMRedisDao.REDIS_POOL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.resources.handler.CheckRepositoryHandler;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.exceptions.AMException;

/** Performs specific Appliance Manager checks. */
public class CheckServlet extends AbstractCheckServlet
{
    private static final long serialVersionUID = -4320236147538190023L;

    private final static Logger LOGGER = LoggerFactory.getLogger(CheckServlet.class);

    /** File where check the repository mount point. */
    private final static String MOUNT_FILE = "/etc/mtab";

    /** The expected file mark in the repositoryl. */
    private final static String REPO_MARK = AMConfiguration.getRepositoryPath()
        + ".abiquo_repository";

    @Override
    protected boolean check() throws Exception
    {
        if (!checkRedis())
        {
            throw new AMException(AMError.AM_CHECK, "No connection to Redis server");
        }

        if (!checkRepositoryMounted())
        {
            throw new AMException(AMError.MOUNT_INVALID_REPOSITORY, AMConfiguration.printConfig());
        }

        checkRepositoryMarkExistOrCreate();

        return true;
    }

    public synchronized boolean checkRedis()
    {
        Jedis redis = null;
        try
        {
            redis = REDIS_POOL.getResource();
            return "PONG".equalsIgnoreCase(redis.ping());
        }
        catch (final JedisConnectionException e)
        {
            return false;
        }
        finally
        {
            if (redis != null)
            {
                REDIS_POOL.returnResource(redis);
            }
        }
    }

    public synchronized boolean checkRepositoryMounted()
    {
        String repositoryLocatino = AMConfiguration.getRepositoryLocation();
        String repositoryMountPoint = AMConfiguration.getRepositoryPath();

        if (repositoryLocatino.startsWith("localhost")
            || repositoryLocatino.startsWith("127.0.0.1"))
        {
            LOGGER.warn("Can't validate ''abiquo.appliancemanager.repositoryLocation'' {}."
                + " Its a local repository", repositoryLocatino);
            return true;
        }

        // mtab do not contains final /
        if (repositoryLocatino.endsWith("/"))
        {
            repositoryLocatino = repositoryLocatino.substring(0, repositoryLocatino.length() - 1);
        }
        repositoryMountPoint = repositoryMountPoint.substring(0, repositoryMountPoint.length() - 1);

        final File mountFile = new File(MOUNT_FILE);
        BufferedReader mountReader = null;
        try
        {
            mountReader = new BufferedReader(new FileReader(mountFile));

            for (String line = mountReader.readLine(); line != null; line = mountReader.readLine())
            {
                if (line.contains(repositoryLocatino))
                {
                    String[] parts = line.split(" ");
                    if (repositoryLocatino.equals(parts[0])
                        && repositoryMountPoint.equals(parts[1]))
                    {
                        return true;
                    }
                    else
                    {
                        LOGGER.warn(
                            "Repository location {} present but not mounted on the expected path {} \n"
                                + line, repositoryLocatino, repositoryMountPoint);
                    }
                }
            }

            return false;
        }
        catch (FileNotFoundException e)
        {
            throw new AMException(AMError.MOUNT_FILE_NOT_FOUND, e);
        }
        catch (IOException e)
        {
            throw new AMException(AMError.MOUNT_FILE_READ_ERROR, e);
        }
        finally
        {
            if (mountReader != null)
            {
                try
                {
                    mountReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void checkRepositoryMarkExistOrCreate()
    {
        CheckRepositoryHandler check = new CheckRepositoryHandler();
        try
        {
            check.canUseRepository();
        }
        catch (AMException e)
        {
            LOGGER.warn("Repository file mark ''.abiquo_repository'' not found. Try to create it.");
            try
            {
                if (!new File(REPO_MARK).createNewFile())
                {
                    throw new AMException(AMError.CONFIG_REPOSITORY_MARK, REPO_MARK);
                }
                else
                {
                    LOGGER.info(
                        "Repository file mark ''.abiquo_repository'' created for ''{}'' in ''{}''",
                        AMConfiguration.getRepositoryLocation(), REPO_MARK);
                }
            }
            catch (IOException eC)
            {
                throw new AMException(AMError.CONFIG_REPOSITORY_MARK, REPO_MARK, eC);
            }
        }

    }
}
