/*
 * silvertunnel.org Netlib - Java library to easily access anonymity networks
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
package cf.monteux.silvertunnel.netlib.layer.tor.circuit;

import java.io.IOException;

import cf.monteux.silvertunnel.netlib.layer.tor.util.TorException;

public interface HiddenServicePortInstance
{
	/**
	 * @return true if this port instance is still open/valid/listening.
	 */
	boolean isOpen();

	/**
	 * @return listening port
	 */
	int getPort();

	/**
	 * @return hidden service hostname 
	 */
	String getHostname();
	
	/**
	 * @return short hidden service hostname, ie. without the onion postfix
	 */
	String getShortHostname();
	
	/**
	 * Create a new (TCP)Stream and assign it to the circuit+streamId specified.
	 * 
	 * @param circuit
	 * @param streamId
	 * @throws TorException
	 */
	void createStream(Circuit circuit, int streamId) throws TorException,
			IOException;

	/**
	 * @return HiddenServiceInstance that belongs to this object
	 */
	HiddenServiceInstance getHiddenServiceInstance();

	/**
	 * @param hiddenServiceInstance that belongs to this object
	 */
	void setHiddenServiceInstance(HiddenServiceInstance hiddenServiceInstance);
}
