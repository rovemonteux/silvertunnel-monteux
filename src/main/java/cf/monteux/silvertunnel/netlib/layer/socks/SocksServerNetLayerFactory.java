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
/*
 * SilverTunnel-Monteux Netlib - Java library to easily access anonymity networks
 * Copyright (c) 2013 silvertunnel-ng.org
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

package cf.monteux.silvertunnel.netlib.layer.socks;

import cf.monteux.silvertunnel.netlib.api.NetLayer;
import cf.monteux.silvertunnel.netlib.api.NetLayerFactory;
import cf.monteux.silvertunnel.netlib.api.NetLayerIDs;
import cf.monteux.silvertunnel.netlib.layer.logger.LoggingNetLayer;
import cf.monteux.silvertunnel.netlib.layer.tcpip.TcpipNetLayer;

/**
 * Factory used to manage the default instance of the SocksServerNetLayer. This
 * factory will be instantiated via default constructor.
 * 
 * Needed only by convenience-class NetFactory.
 * 
 * @author hapke
 * @author Tobias Boese
 */
public class SocksServerNetLayerFactory implements NetLayerFactory
{
	private NetLayer netLayer;

	/**
	 * @see NetLayerFactory#getNetLayerById(cf.monteux.silvertunnel.netlib.api.NetLayerIDs)
	 * 
	 * @param netLayerId valid netLayerId (check {@link NetLayerIDs})
	 * @return the requested NetLayer if found; null if not found; it is not
	 *         guaranteed that the type is SocksServerNetLayer
	 */
	@Override
	public synchronized NetLayer getNetLayerById(final NetLayerIDs netLayerId)
	{
		if (netLayerId == NetLayerIDs.SOCKS_OVER_TCPIP)
		{
			if (netLayer == null)
			{
				// create a new netLayer instance
				final NetLayer tcpipNetLayer = new TcpipNetLayer();
				final NetLayer loggingTcpipNetLayer = new LoggingNetLayer(tcpipNetLayer, "upper tcpip  ");

				final NetLayer socksProxyNetLayer = new SocksServerNetLayer(loggingTcpipNetLayer);

				netLayer = socksProxyNetLayer;
			}
			return netLayer;
		}

		// unsupported netLayerId
		return null;
	}
}
