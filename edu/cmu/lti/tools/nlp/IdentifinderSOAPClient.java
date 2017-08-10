package edu.cmu.lti.tools.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class IdentifinderSOAPClient{
  private static Logger log = Logger.getLogger( IdentifinderSOAPClient.class );
  public final static String DEFAULT_SERVER 
  	= "http://kariya.lti.cs.cmu.edu";
 // = "http://dubai.lti.cs.cmu.edu";
  public final static int DEFAULT_PORT
   = 8088;
  private static Pattern textContentPattern = Pattern.compile("<TEXT>(.*)</TEXT>"); 
  private static String inputPrefix;
  private static String inputSuffix;

  private String server = DEFAULT_SERVER;
  private int port = DEFAULT_PORT;

  private DocumentBuilder docBuilder;
  private HttpURLConnection connection;
  private BufferedWriter wout;
  private BufferedReader serverResponse;
  private URL u;


  private static Map<String, String> specialSymbolMap;

  static{
    StringBuffer sb = new StringBuffer();
    sb.append("<?xml version='1.0' encoding=\"UTF-8\"?>");  
    sb.append("<SOAP-ENV:Envelope ");
    sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
    sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
    sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
    sb.append("xmlns:ns=\"urn:bbn\">");
    sb.append("<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" id=\"_0\">");
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

  private void initialize(){
    //loadProperties();
    try {
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      
      u = new URL(server+":"+port);
      System.err.println("done initializing identifinder SOAP client");
    }catch(Exception ex){
      log.error("ERROR: failed to initialize IdentifinderSOAPClient");
      ex.printStackTrace();
    }
  }

  private void loadProperties() {
    Properties properties = new Properties();
    try {
      File userProperties = new File(System.getProperty("javelin.home")+ "/conf", getClass().getName() + ".properties");
      if(!userProperties.exists())
        throw new IOException("Missing properties file for "+ getClass().getName());
      properties.load(new FileInputStream( userProperties));
      String serverVal = (String)properties.get("server");
      if(serverVal != null)
        server = "http://"+serverVal.trim(); 
      String portVal = (String)properties.get("port");
      if(portVal != null)
        port = Integer.parseInt(portVal.trim()); 

    } catch ( Exception e ) {
        System.err.println( "Caught exception while loading properties: "
          + e.getMessage() );
        throw new RuntimeException( "Could not load properties!" );
    }
  }

  public IdentifinderSOAPClient(){
    initialize();
  }

  protected void finalize() throws Throwable {
    try {
      if(wout != null)
        wout.close();
    } finally {
      super.finalize();
    }
  }  

  public static String recoverSpecialSymbol(String s){
    if(specialSymbolMap == null)
      createSpecialSymbolMap();
    Iterator<Map.Entry<String, String>> itr = specialSymbolMap.entrySet().iterator();
    while(itr.hasNext()){
      Map.Entry<String, String> entry = itr.next();
      s = s.replaceAll(entry.getValue(), entry.getKey());
    }
    return s;
  }


  public static String replaceSpecialSymbol(String s){
    if(specialSymbolMap == null)
      createSpecialSymbolMap();
    Iterator<Map.Entry<String, String>> itr = specialSymbolMap.entrySet().iterator();
    while(itr.hasNext()){
      Map.Entry<String, String> entry = itr.next();
      s = s.replaceAll(entry.getKey(), entry.getValue());
    }
    return s;
  }

  public static void createSpecialSymbolMap() {
    specialSymbolMap = new HashMap<String, String>();
    specialSymbolMap.put("&", "&amp;");
    specialSymbolMap.put("<", "&lt;");
    specialSymbolMap.put(">", "&gt;");
    specialSymbolMap.put("\"", "&quot;");
    specialSymbolMap.put("·","&middot;");
    specialSymbolMap.put("￠","&cent;"); 
    specialSymbolMap.put("÷","&divide;"); 
    specialSymbolMap.put("μ","&#181;"); 
    specialSymbolMap.put("±","&plusmn;"); 
    specialSymbolMap.put("￡","&pound;"); 
    specialSymbolMap.put("§","&sect;"); 
    specialSymbolMap.put("￥","&yen;");
  }

  public String getIdentifinderOutput(String input){
    //System.out.println("input:"+input);
    StringBuffer sb = new StringBuffer();
    sb.append(inputPrefix);
    input = replaceSpecialSymbol(input);
    sb.append(input);
    sb.append(inputSuffix);

    String line = null;
    //System.err.println("sending request:");
    //System.err.print(sb.toString());
    StringBuffer serverSB = new StringBuffer();
    try{
      URLConnection uc = u.openConnection();
      connection = (HttpURLConnection) uc;
      
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      OutputStream out = connection.getOutputStream();
      wout = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));

      wout.write(sb.toString());
      wout.flush();
      wout.close();

      InputStream in = connection.getInputStream();
      serverResponse = new BufferedReader(new InputStreamReader(in, "UTF8"));
       
      while ((line = serverResponse.readLine()) != null){
        //System.out.println(line);
        serverSB.append(line);
        serverSB.append("\n");
      }  

      Document doc = docBuilder.parse(new ByteArrayInputStream(serverSB.toString().trim().getBytes("UTF-8")));
      NodeList nodeList = doc.getElementsByTagName("SOAP-ENV:Envelope");
      Node responseNode = nodeList.item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0);
      NodeList children = responseNode.getChildNodes();
      String text = null;
      for ( int j = 0; j < children.getLength(); j++ ) {
        Node m = children.item( j );
        String name = m.getNodeName();
        if(name.equals("#text")){
         text = m.getNodeValue();
         break;
        } 
      }  
      //System.err.println("server raw output:"+text);
      Matcher matcher = textContentPattern.matcher(text);
      if(matcher.matches()){
        text = matcher.group(1);
        //System.err.println("server output:"+text);
      }
      text = recoverSpecialSymbol(text);
      return text;
    }catch(Exception ex){
      log.error("ERROR when sending request to BBN server and getting server response");
      ex.printStackTrace();
    }
    return null;
  }


  public static void Test1() {
    try {
      IdentifinderSOAPClient client = new IdentifinderSOAPClient();
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF8"));
      String line = null;
      while( (line = br.readLine()) != null){
        System.err.println("input:"+line);
        String output = client.getIdentifinderOutput(line);
        System.err.println("server output:"+output);
      }
    }
    catch (Exception e) {
      System.err.println(e); 
      e.printStackTrace();
    }
  } 

  public static void main(String[] args) {
  
    try {
      IdentifinderSOAPClient client = new IdentifinderSOAPClient();
      String output = client.getIdentifinderOutput("小渊惠三是谁");
      System.err.println("server output:"+output);
    }
    catch (Exception e) {
      System.err.println(e); 
      e.printStackTrace();
    }
    return;
  } // end main

} 

