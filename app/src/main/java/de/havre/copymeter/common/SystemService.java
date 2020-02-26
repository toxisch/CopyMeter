package de.havre.copymeter.common;

import com.google.inject.Singleton;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 17.02.15.
 */
@Singleton
public class SystemService {
    /**
     * Get IP address from first non-localhost interface
     *
     * @param
     * @return address or empty string
     */
    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return null;
    }

    public int getFreePort() throws IOException {
        int result = -1;
        ServerSocket tempServer = null;
        try {
            tempServer = new ServerSocket(0);
            result = tempServer.getLocalPort();
        } finally {
            try {
                if (tempServer != null) {
                    tempServer.close();
                }
            } catch (IOException e) {
                // Continue closing servers.
            }
        }
        return result;
    }
}
