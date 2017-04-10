/*
 * SilverTunnel-Monteux Netlib - Java library to easily access anonymity networks
 * Copyright (c) 2017 Rove Monteux
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
package cf.monteux.silvertunnel.netlib.tool;

import cf.monteux.silvertunnel.netlib.api.NetFactory;
import cf.monteux.silvertunnel.netlib.api.NetLayerIDs;
import cf.monteux.silvertunnel.netlib.api.NetSocket;
import cf.monteux.silvertunnel.netlib.api.util.TcpipNetAddress;
import cf.monteux.silvertunnel.netlib.util.ByteArrayUtil;
import cf.monteux.silvertunnel.netlib.util.HttpUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Rove Monteux
 */
public class TorClient {

    public static String GET(final String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        final TcpipNetAddress netAddress = new TcpipNetAddress(url.getHost(), url.getPort());
        final NetSocket topSocket = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR_OVER_TLS_OVER_TCPIP).createNetSocket(null, null, netAddress);
        HttpUtil.getInstance();
        final byte[] httpResponse = HttpUtil.get(topSocket, netAddress, url.getPath(), 5000);
        return ByteArrayUtil.showAsString(httpResponse);
    }
}
