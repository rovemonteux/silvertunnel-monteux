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

package cf.monteux.silvertunnel.netlib.adapter.url.impl.net.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** open an http input stream given a URL. */
public class Handler extends java.net.URLStreamHandler
{
	/** */
	private static final Logger logger = LogManager.getLogger(Handler.class);

	protected String proxy;
	protected int proxyPort;

	@Override
	protected int getDefaultPort()
	{
		return 80;
	}

	public Handler()
	{
		proxy = null;
		proxyPort = -1;
	}

	public Handler(final String proxy, final int port)
	{
		this.proxy = proxy;
		this.proxyPort = port;
	}

	@Override
	protected java.net.URLConnection openConnection(final URL u) throws IOException
	{
		return openConnection(u, null);
	}

	@Override
	protected java.net.URLConnection openConnection(final URL u, final Proxy p)
			throws IOException
	{
		logger.warn("Handler.openConnection(URL u, Proxy p): not implemented - must be overwritten");
		throw new UnsupportedOperationException("Method not implemented.");
	}
}
