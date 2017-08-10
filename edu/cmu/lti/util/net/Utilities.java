package edu.cmu.lti.util.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities class containing miscellaneous static methods useful for net
 * operations.
 */
public class Utilities
{
    private static final Pattern host_port_pattern = Pattern.compile
	("^([\\p{Alnum}\\.\\_]+)\\:(\\d{1,5})$");
    private static final Pattern ipv4_pattern = Pattern.compile
	("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    /**
     * Construction of Utilities objects is forbidden. This class is to be used
     * for static methods only.
     */
    private Utilities() {}

    /**
     * Parses a String containing an InetSocketAddress designation, returning a
     * valid InetSocketAddress object. A few examples of such Strings are
     * "localhost:4666", "seward.lti.cs.cmu.edu:55555", "128.59.0.1:22".
     * @param s a String containing a valid InetSocketAddress designation.
     * @return an InetSocketAddress.
     * @throws Exception if parsing of input string fails.
     */
    public static InetSocketAddress parseInetSocketAddress(String s)
	throws Exception
    {
	Matcher matcher = host_port_pattern.matcher(s);
	if (!matcher.matches())
	    throw new Exception("Invalid InetSocketAddress format");

	String host = matcher.group(1);
	int port = Integer.parseInt(matcher.group(2));

	InetAddress addr;
	matcher = ipv4_pattern.matcher(host);
	if (matcher.matches()) {
	    int int_hostip[] = new int[4];
	    int_hostip[0] = Integer.parseInt(matcher.group(1));
	    int_hostip[1] = Integer.parseInt(matcher.group(2));
	    int_hostip[2] = Integer.parseInt(matcher.group(3));
	    int_hostip[3] = Integer.parseInt(matcher.group(4));
	    byte hostip[] = new byte[4];
	    hostip[0] = (byte) (int_hostip[0]);
	    hostip[1] = (byte) (int_hostip[1]);
	    hostip[2] = (byte) (int_hostip[2]);
	    hostip[3] = (byte) (int_hostip[3]);
	    addr = InetAddress.getByAddress(hostip);
	} else {
	    addr = InetAddress.getByName(host);
	}

	return new InetSocketAddress(addr, port);
    }
}
