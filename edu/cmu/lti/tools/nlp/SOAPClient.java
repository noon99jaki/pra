package edu.cmu.lti.tools.nlp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SOAPClient {
	private static String inputPrefix;
	private static String inputSuffix;
	static {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding=\"UTF-8\"?>");
		sb.append("<SOAP-ENV:Envelope ");
		sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
		sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
		sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
		sb.append("xmlns:ns=\"urn:bbn\">");
		sb
				.append("<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" id=\"_0\">");
		sb.append("<ns:decode>");
		sb.append("<input>");
		sb.append("<TEXT>");
		inputPrefix = sb.toString();
		sb = new StringBuffer();
		sb.append("</TEXT>");
		sb.append("</input>");
		sb.append("</ns:decode>");
		sb.append("</SOAP-ENV:Body>");
		sb.append("</SOAP-ENV:Envelope>\n");
		inputSuffix = sb.toString();
	}

	public static void test() throws Exception {
		String SOAPUrl = "http://kariya.lti.cs.cmu.edu:8088";
		// Create the connection where we're going to send the file.
		URL url = new URL(SOAPUrl);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		StringBuffer sb = new StringBuffer();
		sb.append(inputPrefix);
		sb.append("小渊惠三是谁");
		sb.append(inputSuffix);
		byte[] b = sb.toString().getBytes();
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		//  httpConn.setRequestProperty("SOAPAction",SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		// Everything's set up; send the XML that was read in to b.
		OutputStream out = httpConn.getOutputStream();
		out.write(b);
		out.close();
		// Read the response and write it to standard out.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}

	public static void main(String[] args) throws Exception {
		try {
			test();
		} catch (Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
	}

	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(InputStream in, OutputStream out) throws IOException {
		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}
