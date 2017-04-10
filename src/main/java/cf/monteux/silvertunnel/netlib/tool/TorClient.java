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
import cf.monteux.silvertunnel.netlib.api.NetLayer;
import cf.monteux.silvertunnel.netlib.api.NetLayerIDs;
import cf.monteux.silvertunnel.netlib.api.NetSocket;
import cf.monteux.silvertunnel.netlib.api.util.TcpipNetAddress;
import cf.monteux.silvertunnel.netlib.util.ByteArrayUtil;
import cf.monteux.silvertunnel.netlib.util.HttpUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rove Monteux
 */
public class TorClient {

    private static final Logger logger = LogManager.getLogger("TorClient");
    public NetLayer netLayer = null;
    
    public TorClient() {
        connect();
    }
    
    public void connect() {
        logger.info("Trying to connect to the TOR network.");
        NetLayer netLayer = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR_OVER_TLS_OVER_TCPIP);
        netLayer.waitUntilReady();
        logger.info("Connected to the TOR network");
    }
    public String GET(final String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        TcpipNetAddress netAddress = new TcpipNetAddress(url.getHost(), url.getPort());
        NetSocket netSocket = netLayer.createNetSocket(null, null, netAddress);
        HttpUtil.getInstance();
        final byte[] httpResponse = HttpUtil.get(netSocket, netAddress, url.getPath(), 5000);
        netSocket.close();
        return ByteArrayUtil.showAsString(httpResponse);
    }
}
