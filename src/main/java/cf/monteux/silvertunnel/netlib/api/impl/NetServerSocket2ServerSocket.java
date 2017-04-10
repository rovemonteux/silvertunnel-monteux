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

package cf.monteux.silvertunnel.netlib.api.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;

import cf.monteux.silvertunnel.netlib.api.NetServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrap a NetServerSocket to be a ServerSocket.
 * 
 * @author hapke
 */
public class NetServerSocket2ServerSocket extends ServerSocket
{
	/** */
	private static final Logger logger = LogManager.getLogger(NetServerSocket2ServerSocket.class);

	private final NetServerSocket netServerSocket;

	public NetServerSocket2ServerSocket(NetServerSocket netServerSocket)
			throws IOException
	{
		super();
		this.netServerSocket = netServerSocket;
	}

	@Override
	public void bind(SocketAddress endpoint) throws IOException
	{
		throw new SocketException("Already bound");
	}

	@Override
	public void bind(SocketAddress endpoint, int backlog) throws IOException
	{
		throw new SocketException("Already bound");
	}

	@Override
	public InetAddress getInetAddress()
	{
		logger.warn("method empty/not implemented");
		return null;
	}

	@Override
	public int getLocalPort()
	{
		logger.warn("method empty/not implemented");
		return -1;
	}

	@Override
	public SocketAddress getLocalSocketAddress()
	{
		logger.warn("method empty/not implemented");
		return null;
	}

	@Override
	public Socket accept() throws IOException
	{
		return new NetSocket2Socket(netServerSocket.accept());
	}

	@Override
	public void close() throws IOException
	{
		netServerSocket.close();
	}

	@Override
	public ServerSocketChannel getChannel()
	{
		logger.warn("method empty/not implemented");
		return null;
	}

	@Override
	public boolean isBound()
	{
		return true;
	}

	@Override
	public boolean isClosed()
	{
		logger.warn("method empty/not implemented");
		return false;
	}

	@Override
	public synchronized void setSoTimeout(int timeout) throws SocketException
	{
		logger.warn("method empty/not implemented");
	}

	@Override
	public synchronized int getSoTimeout() throws IOException
	{
		logger.warn("method empty/not implemented");
		return -1;
	}

	@Override
	public void setReuseAddress(final boolean on) throws SocketException
	{
		logger.warn("method empty/not implemented");
	}

	@Override
	public synchronized void setReceiveBufferSize(final int size)
			throws SocketException
	{
		logger.warn("method empty/not implemented");
	}

	@Override
	public synchronized int getReceiveBufferSize() throws SocketException
	{
		logger.warn("method empty/not implemented");
		return -1;
	}

	@Override
	public void setPerformancePreferences(final int connectionTime, final int latency,
			final int bandwidth)
	{
		logger.warn("method empty/not implemented");
	}
}
