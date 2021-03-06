/*
 * SilverTunnel-Monteux Netlib - Java library to easily access anonymity networks
 * Copyright (c) 2009-2012 silvertunnel.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package cf.monteux.silvertunnel.netlib.layer.tor.util;

import cf.monteux.silvertunnel.netlib.api.NetLayerStatus;

/**
 * Object to hold and modify a NetLayerStatus
 * 
 * @author hapke
 */
public interface NetLayerStatusAdmin
{
	void setStatus(NetLayerStatus newStatus);

	/**
	 * Set the new status, but only, if the new readyIndicator is higher than
	 * the current one.
	 * 
	 * @param newStatus
	 */
	void updateStatus(NetLayerStatus newStatus);

	NetLayerStatus getStatus();
}
