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

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rove Monteux
 */
public class TorTest {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        if (System.getProperty("java.util.logging.config.file") == null) {
            try {
                java.util.logging.LogManager.getLogManager().readConfiguration(TorTest.class.getResourceAsStream("/log/logging.properties"));
            } catch (IOException e) {
                final Exception ex = e;
                System.err.println("LogManager configuration failed: " + ex);
                ex.printStackTrace();
            } catch (NullPointerException e) {
                final Exception ex = e;
                System.err.println("The default logging properties file, /log/logging.properties, is missing.");
                ex.printStackTrace();
            }
        }
        String httpResponseStr = "";
        try {
            TorClient torClient = new TorClient();
            httpResponseStr = torClient.GET("https://check.torproject.org/");
        } catch (Exception e) {
            logger.error("An error occured while trying to connect via TOR.", e);
        }
        System.out.println("http response body: " + httpResponseStr);
        if (httpResponseStr.contains("ongratulations")) {
            System.out.println("TOR works.");
        } else {
            System.out.println("Something went wrong, see stack trace.");
        }
    }

}
