package edu.up.cs.androidcatan.gameframework.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

/**
 * Helper-class to encode and decode IP addresses to/from a format that is
 * convenient for typing on an Android soft keyboard.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 */
public class IPCoder {

    /**
     * gets the IP address of the current device
     *
     * @return
     * 		the IP address of the current device, or and error message if
     * 		an IP address cannot be determined
     */
    public static String getLocalIpAddress() {
        try {
            // loop through the device's network interfaces and internet address until one is found
            // that is a well-formed UP address; return it when found
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IPCoder"/*this.toString()*/, ex.toString());
        }
        return "Unable to determine UP address.";
    }

    /**
     * returns address of the current device as a 64-bit non-negative number
     *
     * @return
     * 		a long value that represents the current IP address as an integer, or -1
     * 		if the IP address could not be determined
     */
    public static long getLocalIpAddressAsNumber() {
        // the value we're building
        long val = -1;

        try {
            // get the local IP address
            String s = getLocalIpAddress();
            if (!s.startsWith("Unable") && s.contains(".")) {
                // if it is well-formed, then take each piece (number-strings
                // in the range 0..255, separated by '.' characters), parse
                // then, and combine them into a number
                s += ".";
                int idx = -1;
                val = 0;
                for (int i = 0; i < 4; i++) {
                    int prev = idx;
                    idx = s.indexOf(".", idx+1);
                    int num = Integer.parseInt(s.substring(prev+1, idx));
                    val *= 256;
                    val += num;
                }
            }
        }
        catch (Exception x) {
            // return -1 if an exception occurs
            val = -1;
        }

        // return the value which has (hopefully) been converted
        return val;
    }

    // helper string, which are our base-36 "digits"
    private static String codes = "0123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * Encodes the local IP address as base-36 number, converted to a string
     *
     * @return
     * 		the encoded number-string, or "*invalid*" if the conversion could not
     * 		be done
     */
    public static String encodeLocalIP() {

        // get the numeric value we want to convert
        long val = getLocalIpAddressAsNumber();

        // if negative, return "*invalid*"
        if (val < 0) {
            return "*invalid*";
        }

        // convert to a base-36 number-string and return it
        if (val == 0) {
            return "0";
        }
        String rtnVal = "";
        do {
            rtnVal = codes.charAt((int)(val%36)) + rtnVal;
            val = val / 36;

        } while (val > 0);
        return rtnVal;
    }

    /**
     * decode the IP address back into a well-formed IP string (i.e., with
     * periods and digits
     *
     * @param codedIp
     * 		the IP code to decode
     * @return
     * 		the decoded IP, or "*invalid*" if not a valid coded (or already
     * 		decoded) IP code
     */
    public static String decodeIp(String codedIp) {

        // case-fold, so that we handle both upper- and lower-case
        codedIp = codedIp.toLowerCase();

        // if it already contains a '.', assume that it's already
        // been converted; just return it
        if (codedIp.contains(".")) {
            return codedIp;
        }

        // convert back to a base-36 long
        long val = 0;
        for (int i = 0; i < codedIp.length(); i++) {
            val *= 36;
            int thisCode = codes.indexOf(codedIp.charAt(i));
            if (thisCode < 0) {
                // if illegal character, return "*invalid*"
                return "*invalid*";
            }
            val += thisCode;
        }

        // convert the value into an IP code
        String rtnVal = "";
        for (int i = 0; i < 4; i++) {
            rtnVal = "."+(val%256) + rtnVal;
            val /= 256;
        }
        return rtnVal.substring(1); // leave off initial '.'
    }

}
