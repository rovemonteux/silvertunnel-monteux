/*
 * OnionCoffee - Anonymous Communication through TOR Network
 * Copyright (C) 2005-2007 RWTH Aachen University, Informatik IV
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
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

package cf.monteux.silvertunnel.netlib.layer.tor.circuit;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import cf.monteux.silvertunnel.netlib.layer.tor.circuit.cells.Cell;
import cf.monteux.silvertunnel.netlib.layer.tor.circuit.cells.CellDestroy;
import cf.monteux.silvertunnel.netlib.layer.tor.circuit.cells.CellRelay;
import cf.monteux.silvertunnel.netlib.layer.tor.util.TorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * reads data arriving at the TLS connection and dispatches it to the
 * appropriate circuit or stream that it belongs to.
 * 
 * @author Lexi Pimenidis
 * @author hapke
 * @author Tobias Boese
 */
class TLSDispatcherThread extends Thread
{
	/** */
	private static final Logger logger = LogManager.getLogger(TLSDispatcherThread.class);

	private final DataInputStream sin;
	private final TLSConnection tls;
	private boolean stopped;

	TLSDispatcherThread(final TLSConnection tls, final DataInputStream sin)
	{
		this.tls = tls;
		this.sin = sin;
		this.setName("TLSDispatcher for " + tls.getRouter().getNickname());
		this.start();
	}

	public void close()
	{
		this.stopped = true;
		this.interrupt();
	}

	@Override
	public void run()
	{
		boolean dispatched = false;
		while (!stopped)
		{
			
			// read next data-packet
			Cell cell = null;
			try
			{
				cell = new Cell(sin);
			}
			catch (final IOException e)
			{
				if (e instanceof SocketTimeoutException)
				{
					logger.debug("TLSDispatcher.run: {} connection error: socket timeout", this.getName(), e);
					continue; // SocketTimeout should not be a showstopper here
				}
				else
				{
					logger.info("TLSDispatcher.run: connection error: " + e.getMessage(), e);
				}
				stopped = true;
				break;
			}
			// padding cell?
			if (cell.isTypePadding())
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("TLSDispatcher.run: padding cell from {}", tls.getRouter().getNickname());
				}
			}
			else
			{
				dispatched = false;
				final int cellCircId = cell.getCircuitId();
				// dispatch according to circID
				final Circuit circ = tls.getCircuit(cellCircId);
				if (circ != null)
				{
					// check for destination in circuit
					if (cell.isTypeRelay())
					{
						CellRelay relay = null;
						try
						{
							// found a relay-cell! Try to strip off
							// symmetric encryption and check the content
							relay = new CellRelay(circ, cell);
							if (logger.isDebugEnabled())
							{
								logger.debug("relay.getRelayCommandAsString()="
										+ relay.getRelayCommandAsString());
							}

							// dispatch to stream, if a stream-ID is given
							final int streamId = relay.getStreamId();
							if (streamId != 0)
							{
								final Stream stream = circ.getStreams().get(streamId);
								if (logger.isDebugEnabled())
								{
									logger.debug("dispatch to stream with streamId="
											+ streamId + ", stream=" + stream);
								}
								if (stream != null)
								{
									dispatched = true;
									if (logger.isDebugEnabled())
									{
										logger.debug("TLSDispatcher.run: data from "
												+ tls.getRouter().getNickname()
												+ " dispatched to circuit "
												+ circ.getId()
												+ "/stream "
												+ streamId);
									}
									stream.processCell(relay);
								}
								else if (circ
										.isUsedByHiddenServiceToConnectToRendezvousPoint()
										&& relay.isTypeBegin())
								{
									// new stream requested on a circuit that
									// was already established to the rendezvous
									// point
									circ.handleHiddenServiceStreamBegin(relay,
											streamId);
								}
								else
								{
									// do nothing
									if (logger.isDebugEnabled())
									{
										logger.debug("else: circ.isUsedByHiddenServiceToConnectToRendezvousPoint()="
												+ circ.isUsedByHiddenServiceToConnectToRendezvousPoint()
												+ ", relay.getRelayCommand()="
												+ relay.getRelayCommand());
									}
								}
							}
							else
							{
								// relay cell for stream id 0: dispatch to
								// circuit
								if (relay.isTypeIntroduce2())
								{
									if (circ.isUsedByHiddenServiceToConnectToIntroductionPoint())
									{
										if (logger.isDebugEnabled())
										{
											logger.debug("TLSDispatcher.run: introduce2 from "
													+ tls.getRouter()
															.getNickname()
													+ " dispatched to circuit "
													+ circ.getId()
													+ " (stream ID=0)");
										}
										try
										{
											dispatched = circ.handleIntroduce2(relay);
										}
										catch (final IOException e)
										{
											logger.info("TLSDispatcher.run: error handling intro2-cell: "
													+ e.getMessage());
										}
									}
									else
									{
										// do nothing
										if (logger.isDebugEnabled())
										{
											logger.debug("else isTypeIntroduce2: from "
													+ tls.getRouter()
															.getNickname()
													+ " dispatched to circuit "
													+ circ.getId()
													+ " (stream ID=0)");
										}
									}
								}
								else
								{
									if (logger.isDebugEnabled())
									{
										logger.debug("TLSDispatcher.run: data from "
												+ tls.getRouter().getNickname()
												+ " dispatched to circuit "
												+ circ.getId()
												+ " (stream ID=0)");
									}
									dispatched = true;
									circ.processCell(relay);
								}
							}
						}
						catch (final TorException e)
						{
							logger.warn("TLSDispatcher.run: TorException "
									+ e.getMessage()
									+ " during dispatching cell");
						}
						catch (final Exception e)
						{
							logger.warn(
									"TLSDispatcher.run: Exception "
											+ e.getMessage()
											+ " during dispatching cell", e);
						}
					}
					else
					{
						// no relay cell: cell is there to control circuit
						if (cell.isTypeDestroy())
						{
							if (logger.isDebugEnabled())
							{
								try
								{
									logger.debug("TLSDispatcher.run: received DESTROY-cell from "
											+ tls.getRouter().getNickname()
											+ " for circuit "
											+ circ.getId()
											+ " reason : "
											+ ((CellDestroy) cell).getReason());
								}
								catch (ClassCastException exception)
								{
									logger.debug("TLSDispatcher.run: received DESTROY-cell from "
											+ tls.getRouter().getNickname()
											+ " for circuit "
											+ circ.getId()
											+ " reason : " + CellDestroy.getReason(cell.getPayload()[0]));
								}
							}
							if (cell.getPayload()[0] == CellDestroy.REASON_END_CIRC_TOR_PROTOCOL)
							{
								logger.warn("got a DestroyCell with Reason protocol violation from " + circ);
							}
							dispatched = true;
							circ.close(true);
						}
						else
						{
							if (logger.isDebugEnabled())
							{
								logger.debug("TLSDispatcher.run: data from "
										+ tls.getRouter().getNickname()
										+ " dispatched to circuit "
										+ circ.getId());
							}
							dispatched = true;
							try
							{
								circ.processCell(cell);
							}
							catch (TorException exception)
							{
								logger.warn("got Exception while processing cell", exception);
							}
						}
					}
				}
				else
				{
					logger.info("TLSDispatcher.run: received cell for circuit "
							+ cellCircId + " from "
							+ tls.getRouter().getNickname()
							+ ". But no such circuit exists.");
				}
			}
			if (!dispatched)
			{
				// used to be WARNING, but is given too often to be of $REAL
				// value, like a warning should
				if (logger.isDebugEnabled())
				{
					logger.debug("TLSDispatcher.run: data from "
							+ tls.getRouter().getNickname()
							+ " could not get dispatched");
				}
				if (logger.isDebugEnabled())
				{
					logger.debug("TLSDispatcher.run: " + cell.toString());
				}
			}
		}
	}
}
