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

package cf.monteux.silvertunnel.netlib.adapter.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import cf.monteux.silvertunnel.netlib.adapter.url.impl.net.http.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Support of URL handling.
 * 
 * @author hapke
 * @author Tobias Boese
 */
public class URLUtil
{
	/** */
	private static final Logger logger = LogManager.getLogger(URLUtil.class);

	/**
	 * Returns a {@link java.net.URLConnection URLConnection} instance that
	 * represents a connection to the remote object referred to by the
	 * {@code URL}.
	 * 
	 * @param factory
	 * @param url
	 *            http or https URL
	 * @return a {@link java.net.URLConnection URLConnection} linking to the
	 *         URL.
	 * @exception IOException
	 *                if an I/O exception occurs.
	 * @see java.net.URL#openConnection()
	 */
	public static URLConnection openConnection(final URLStreamHandlerFactory factory, final URL url) throws IOException
	{
		logger.info("openConnection start with url={}", url);
		final URLStreamHandler handler = factory.createURLStreamHandler(url.getProtocol());
		final URLConnection result = ((HttpHandler) handler).openConnection(url, null);
		logger.info("openConnection end with result={}", result);
		return result;
	}
}
