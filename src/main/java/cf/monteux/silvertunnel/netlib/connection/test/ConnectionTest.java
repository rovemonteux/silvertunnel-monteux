package cf.monteux.silvertunnel.netlib.connection.test;

import java.io.IOException;

import cf.monteux.silvertunnel.netlib.api.NetFactory;
import cf.monteux.silvertunnel.netlib.api.NetLayerIDs;
import cf.monteux.silvertunnel.netlib.api.NetSocket;
import cf.monteux.silvertunnel.netlib.api.util.TcpipNetAddress;
import cf.monteux.silvertunnel.netlib.util.ByteArrayUtil;
import cf.monteux.silvertunnel.netlib.util.HttpUtil;

public class ConnectionTest {

	public static void main(String[] args) throws IOException
	{
	    final String TORCHECK_HOSTNAME = "httptest.silvertunnel-ng.org";
	    final TcpipNetAddress TORCHECK_NETADDRESS = new TcpipNetAddress(TORCHECK_HOSTNAME, 80);
	    final NetSocket topSocket = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR_OVER_TLS_OVER_TCPIP)
	            .createNetSocket(null, null, TORCHECK_NETADDRESS);
	    HttpUtil.getInstance();
	    final byte[] httpResponse = HttpUtil.get(topSocket, TORCHECK_NETADDRESS, "/checktor.php", 5000);
	    String httpResponseStr = ByteArrayUtil.showAsString(httpResponse);
	    System.out.println("http response body: " + httpResponseStr);
	    if ("Congratulations. Your browser is configured to use Tor.".equals(httpResponseStr))
	        {
	        System.out.println("works");
	    }
	    else
	    {
	        System.out.println("something went wrong");
	    }
	}
}
