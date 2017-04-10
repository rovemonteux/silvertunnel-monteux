/**
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

package cf.monteux.silvertunnel.netlib.layer.tor.circuit.cells;

import cf.monteux.silvertunnel.netlib.layer.tor.circuit.Circuit;
import cf.monteux.silvertunnel.netlib.layer.tor.util.TorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * used to create a CREATE_FAST cell.
 * 
 * @author Tobias Boese
 */
public class CellCreateFast extends Cell
{
	/** */
	private static final Logger logger = LogManager.getLogger(CellCreateFast.class);

	/**
	 * creates a CREATE_FAST-CELL.
	 * 
	 * @param circuit
	 *            the circuit that is to be build with this cell
	 */
	public CellCreateFast(final Circuit circuit) throws TorException
	{
		super(circuit, Cell.CELL_CREATE_FAST);
		System.arraycopy(circuit.getRouteNodes()[0].getDhXBytes(), 0, payload, 0, 20);
	}
}
