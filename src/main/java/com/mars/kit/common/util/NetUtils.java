package com.mars.kit.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;



/**
 *@FileName  NetUtils.java
 *@Date  16-5-17 下午3:13
 *@author Colley
 *@version 1.0
 */
public class NetUtils {
    public static String getIpStr() {
        InetAddress inetAddress = getIp();

        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }

        return "Unknown";
    }

    

    public static InetAddress getIp() {
        InetAddress ip = null;
        try {
        	Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        if (ip == null) {
            try {
                ip = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return ip;
    }
}
