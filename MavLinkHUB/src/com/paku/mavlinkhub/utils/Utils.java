// $codepro.audit.disable
package com.paku.mavlinkhub.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.*;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.security.auth.x500.X500Principal;

import org.apache.http.conn.util.InetAddressUtils;

import com.paku.mavlinkhub.enums.SCREEN_SIZE;

@SuppressLint("DefaultLocale")
public class Utils {

	/**
	 * Convert byte array to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sbuf = new StringBuilder();
		for (int idx = 0; idx < bytes.length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10) sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase(Locale.US));
		}
		return sbuf.toString();
	}

	/**
	 * Get utf8 byte array.
	 * 
	 * @param str
	 * @return array of NULL if error was found
	 */
	public static byte[] getUTF8Bytes(String str) {
		try {
			return str.getBytes("UTF-8");
		}
		catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Load UTF8withBOM or any ansi text file.
	 * 
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static String loadFileAsString(String filename) throws java.io.IOException {
		final int BUFLEN = 1024;
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
			byte[] bytes = new byte[BUFLEN];
			boolean isUTF8 = false;
			int read, count = 0;
			while ((read = is.read(bytes)) != -1) {
				if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
					isUTF8 = true;
					baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
				}
				else {
					baos.write(bytes, 0, read);
				}
				count += read;
			}
			return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
		}
		finally {
			try {
				is.close();
			}
			catch (Exception ex) {
			}
		}
	}

	/**
	 * Returns MAC address of the given interface name.
	 * 
	 * @param interfaceName
	 *            eth0, wlan0 or NULL=use first interface
	 * @return mac address or empty string
	 */
	public static String getMACAddress(String interfaceName) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				if (interfaceName != null) {
					if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
				}
				byte[] mac = intf.getHardwareAddress();
				if (mac == null) return "";
				StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < mac.length; idx++)
					buf.append(String.format("%02X:", mac[idx]));
				if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
				return buf.toString();
			}
		}
		catch (Exception ex) {
		} // for now eat exceptions
		return "";
		/*try {
		    // this is so Linux hack
		    return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
		} catch (IOException ex) {
		    return null;
		}*/
	}

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase(Locale.US);
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4) return sAddr;
						}
						else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		}
		catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public static InetAddress getBroadcastAddress(Context mContext) throws IOException {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	public static SCREEN_SIZE getScreenSize(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenLayout) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return SCREEN_SIZE.SMALL;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return SCREEN_SIZE.NORMAL;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return SCREEN_SIZE.LARGE;
		case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
			return SCREEN_SIZE.XLARGE;
		default:
			return SCREEN_SIZE.UNDEF;
		}
	}

	private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

	public static boolean isDebuggable(Context ctx) {
		boolean debuggable = false;

		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature signatures[] = pinfo.signatures;

			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			for (int i = 0; i < signatures.length; i++) {
				ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
				X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
				debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
				if (debuggable) break;
			}
		}
		catch (NameNotFoundException e) {
			//debuggable variable will remain false
		}
		catch (CertificateException e) {
			//debuggable variable will remain false
		}
		return debuggable;
	}

}