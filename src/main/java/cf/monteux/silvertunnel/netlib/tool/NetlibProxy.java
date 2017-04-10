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

package cf.monteux.silvertunnel.netlib.tool;

import java.io.IOException;
import java.io.InputStream;

import cf.monteux.silvertunnel.netlib.api.NetFactory;
import cf.monteux.silvertunnel.netlib.api.NetLayerIDs;
import cf.monteux.silvertunnel.netlib.api.NetServerSocket;
import cf.monteux.silvertunnel.netlib.api.NetSocket;
import cf.monteux.silvertunnel.netlib.api.util.TcpipNetAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command line tool that starts a proxy that connects to a NetLayer.
 * 
 * The connection client-proxy is uses TCP/IP, the connection
 * proxy-restOfTheWorld uses the specified NetLayer.
 * 
 * Command line arguments: [optional_listen_address:listen_port] [net_layer_id]
 * [prop1=value1] [prop2=value2] ...
 * 
 * Examples: java -cp ... cf.monteux.silvertunnel.netlib.tool.NetlibProxy 1080
 * socks_over_tcpip java -cp ... cf.monteux.silvertunnel.netlib.tool.NetlibProxy
 * 127.0.0.1:1080 socks_over_tcpip java -cp ...
 * cf.monteux.silvertunnel.netlib.tool.NetlibProxy [::1/128]:1080 socks_over_tcpip
 * (IPv6 - not yet implemented) java -cp ...
 * cf.monteux.silvertunnel.netlib.tool.NetlibProxy 127.0.0.1:1080
 * socks_over_tor_over_tls_over_tcpip java -cp ...
 * -DNetLayerBootstrap.skipTor=true cf.monteux.silvertunnel.netlib.tool.NetlibProxy
 * 1080 socks_over_tcpip TcpipNetLayer.backlog=10
 * 
 * @author hapke
 * @author Tobias Boese
 */
public class NetlibProxy
{
	/** */
	private static final Logger logger = LogManager.getLogger(NetlibProxy.class);

	private static boolean startedFromCommandLine = true;
	private static volatile boolean started = false;
	private static volatile boolean stopped = false;
	private static NetServerSocket netServerSocket;

	/**
	 * Start the program, but not from command line.
	 * 
	 * @param argv
	 */
	public static void start(final String[] argv)
	{
		startedFromCommandLine = false;
		main(argv);
	}

	/**
	 * Start the program from command line.
	 * 
	 * @param argv
	 */
	public static void main(final String[] argv)
	{
		stopped = false;
		started = false;
		if (argv.length < 1)
		{
			logger.error("NetProxy: insufficient number of command line arguments: you must specify [listen_port] and [net_layer_id] at least");
			System.exit(1);
			return;
		}

		// open server port
		try
		{
			// parse listen address and port
			final String listenAddressPortArg = argv[0];
			final TcpipNetAddress localListenAddress = new TcpipNetAddress(
					listenAddressPortArg);

			// open server port
			netServerSocket = NetFactory.getInstance()
					.getNetLayerById(NetLayerIDs.TCPIP)
					.createNetServerSocket(null, localListenAddress);

		}
		catch (final Exception e)
		{
			logger.error("NetlibProxy: could not open server port", e);
			if (startedFromCommandLine)
			{
				logger.error("System.exit(2)");
				System.exit(2);
			}
			return;
		}
		started = true;

		// parse the netLayerId
		final NetLayerIDs lowerLayerNetLayerId = NetLayerIDs.getByValue(argv[1]);

		// handle incoming connections (listen endless)
		try
		{
			while (!stopped)
			{
				final NetSocket upperLayerNetSocket = netServerSocket.accept();
				new NetProxySingleConnectionThread(upperLayerNetSocket,
						lowerLayerNetLayerId).start();
			}
		}
		catch (final Exception e)
		{
			started = false;
			final String msg = "NetlibProxy: server-wide problem while running";
			if (stopped)
			{
				logger.info(msg);
			}
			else
			{
				logger.error(msg, e);
			}
			if (startedFromCommandLine)
			{
				logger.error("System.exit(3)");
				System.exit(3);
			}
			return;
		}
	}

	/**
	 * (Try to) close/exit the program.
	 */
	public static void stop()
	{
		logger.info("NetlibProxy: will be stopped now");
		stopped = true;
		started = false;

		// close server socket
		try
		{
			netServerSocket.close();
		}
		catch (final IOException e)
		{
			logger.warn("Exception while closing the server socket",
					e);
		}
	}

	/**
	 * Retrieve the current state.
	 * 
	 * @return true=proxy server port is open
	 */
	public static boolean isStarted()
	{
		return started;
	}

	// /////////////////////////////////////////////////////
	// test code
	// /////////////////////////////////////////////////////

	public static void testConnection() throws Exception
	{
		logger.info("(client) connect client to server");
		final TcpipNetAddress remoteAddress = new TcpipNetAddress(
				"www.google.de", 80);
		final NetSocket client = NetFactory.getInstance()
				.getNetLayerById(NetLayerIDs.TCPIP)
				.createNetSocket(null, null, remoteAddress);

		logger.info("(client) send data client->server");
		client.getOutputStream().write("GET /\n\n".getBytes());
		client.getOutputStream().flush();

		logger.info("(client) read data from server");
		final byte[] dataReceivedByClient = readDataFromInputStream(100,
				client.getInputStream());

		logger.info("(client) finish connection");
		client.close();
	}

	public static byte[] readDataFromInputStream(int maxResultSize,
			InputStream is) throws IOException
	{
		final byte[] tempResultBuffer = new byte[maxResultSize];

		int len = 0;
		do
		{
			if (len >= tempResultBuffer.length)
			{
				// logger.info("result buffer is full");
				break;
			}
			final int lastLen = is.read(tempResultBuffer, len,
					tempResultBuffer.length - len);
			if (lastLen < 0)
			{
				// logger.info("end of result stream");
				break;
			}
			len += lastLen;
		}
		while (true);

		// copy to result buffer
		final byte[] result = new byte[len];
		System.arraycopy(tempResultBuffer, 0, result, 0, len);

		return result;
	}
}
