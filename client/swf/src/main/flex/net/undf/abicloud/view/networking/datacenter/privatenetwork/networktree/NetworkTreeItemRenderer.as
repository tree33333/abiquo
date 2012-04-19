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

package net.undf.abicloud.view.networking.datacenter.privatenetwork.networktree
{
    import mx.controls.treeClasses.TreeItemRenderer;

    import net.undf.abicloud.vo.networking.VlanNetwork;
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    public class NetworkTreeItemRenderer extends TreeItemRenderer
    {
        public function NetworkTreeItemRenderer()
        {
            super();
        }

        override public function set data(value:Object):void
        {
            super.data = value;

            if (value is NetworkTreeItem)
            {
                listData.label = NetworkTreeItem(value).enterprise.name;
                setStyle("fontWeight", "bold");
                setStyle("fontStyle", "normal");
            }
            else if (value is VirtualDataCenter)
            {
                listData.label = VirtualDataCenter(value).name;
                setStyle("fontWeight", "normal");
                setStyle("fontStyle", "normal");
            }
            else if (value is VlanNetwork)
            {
                listData.label = VlanNetwork(value).networkName;
                if (VlanNetwork(value).defaultNetwork)
                {
                    setStyle("fontStyle", "italic");
                }
            }
        }
    }
}