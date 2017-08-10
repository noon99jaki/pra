package edu.cmu.lti.nlp.chinese;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SOAPClient {

	
	
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Integer port ;
		public String server;
		public Param(Class c) {//throws IOException{
			super(c);
			port = getInt("port",8088);
			server=getString("server","http://kariya.lti.cs.cmu.edu");
			//"http://dubai.lti.cs.cmu.edu"
		}
	}
	

	
  public void createSpecialSymbolMap() {
    m_specialSymbol = new HashMap<String, String>();
    m_specialSymbol.put("&", "&amp;");
    m_specialSymbol.put("<", "&lt;");
    m_specialSymbol.put(">", "&gt;");
    m_specialSymbol.put("\"", "&quot;");
    m_specialSymbol.put("·","&middot;");
    m_specialSymbol.put("￠","&cent;"); 
    m_specialSymbol.put("÷","&divide;"); 
    m_specialSymbol.put("μ","&#181;"); 
    m_specialSymbol.put("±","&plusmn;"); 
    m_specialSymbol.put("￡","&pound;"); 
    m_specialSymbol.put("§","&sect;"); 
    m_specialSymbol.put("￥","&yen;");
  }
	protected Map<String, String> m_specialSymbol;

  protected Pattern textContentPattern = Pattern.compile("<TEXT>(.*)</TEXT>"); 
  protected String inputPrefix=
		"<?xml version='1.0' encoding=\"UTF-8\"?>"
    +"<SOAP-ENV:Envelope "
    +"xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
    +"xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" "
    +"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
    +"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
    +"xmlns:ns=\"urn:bbn\">"
    +"<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" id=\"_0\">"
    +"<ns:decode>"
    +"<input>"
    +"<TEXT>";
  protected String inputSuffix= 
  	"</TEXT>"
  	+"</input>"
  	+"</ns:decode>"
  	+"</SOAP-ENV:Body>"
  	+"</SOAP-ENV:Envelope>\n"; 

  protected DocumentBuilder docBuilder;
  protected HttpURLConnection conn;
  protected BufferedWriter bw;
  protected BufferedReader br;
  protected URL url;

	public Param p=null;//new Param();
	public SOAPClient(Class c){
		p=new Param(c);
		createSpecialSymbolMap();
		connect();

  }	

  public String recoverSpecialSymbol(String s){
  	for(  Map.Entry<String, String> e : m_specialSymbol.entrySet())
      s = s.replaceAll(e.getValue(), e.getKey());    
    return s;
  }


  public String replaceSpecialSymbol(String s){
  	for(  Map.Entry<String, String> e : m_specialSymbol.entrySet())
      s = s.replaceAll(e.getKey(), e.getValue());    
    return s;
  }
  protected void finalize() throws Throwable {
    try {
      if(bw != null)    bw.close();
    } finally {
      super.finalize();
    }
  }  
	public void connect(){
    try {
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();        
      url = new URL(p.server+":"+p.port);
      System.out.println("done initializing identifinder SOAP client");
      

    }catch(Exception e){
      e.printStackTrace();
    }	
    return;
	}
  protected String pushPopRaw(String input){
    String line = null;
    //System.err.println("sending request:");
    //System.err.print(sb.toString());
    StringBuffer sbSever = new StringBuffer();
    try{
      //URLConnection uc = 
      conn = (HttpURLConnection) url.openConnection();      
      conn.setDoOutput(true);
      conn.setDoInput(true);
      //conn.setAllowUserInteraction(false);
      
      conn.setRequestMethod("POST");
      bw = new BufferedWriter(new OutputStreamWriter(
      		conn.getOutputStream(), "UTF8"));
      
      bw.write(input);
      bw.flush();
      //bw.close();
      
      br = new BufferedReader(new InputStreamReader(
      		conn.getInputStream(), "UTF8"));
      while ((line = br.readLine()) != null){
        //System.out.println(line);
        sbSever.append(line);
        sbSever.append("\n");
      } 
      br.close();   
      return sbSever.toString();
    }
    catch(Exception ex){
      System.err.println("ERROR when sending request to SOAP server and getting server response");
      System.err.println(ex);
      ex.printStackTrace();
    }  
    return null;
  }
  
  
  public String pushPop(String input){
    input="小渊惠三是谁";

    //System.out.println("input:"+input);
    StringBuffer sb = new StringBuffer();
    sb.append(inputPrefix);
    input = replaceSpecialSymbol(input);
    sb.append(input);
    sb.append(inputSuffix);    
    String response = pushPopRaw(sb.toString());
    
    String text = null;
    try{
       Document doc = docBuilder.parse(new ByteArrayInputStream(
      		response.trim().getBytes("UTF-8")));

      NodeList nodeList = doc.getElementsByTagName("SOAP-ENV:Envelope");
      Node responseNode = nodeList.item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0);
      NodeList children = responseNode.getChildNodes();
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
    }catch(Exception ex){
      System.err.println(ex);
      ex.printStackTrace();
    }
    
    return text;
    
  }
}
