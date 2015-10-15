package httpProxy;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.StringTokenizer;


public class RequestHeader {

	
	public long date = new Date().getTime();
	public  String method = new String();		// HTTP Request Methods: Get, Post, Head etc...
	public String url = new String();
	public String version = new String();		// HTTPS Version : HTTP/1.0
	public String userAgent = new String();	// Client's browser's name.
	public String referer = new String();		// Requesting documents that contained the url link.
	public String accept= new String(); 		// MIME types client accepts
	public int contentLength = -1; 			// Length of content: POST Method.
	public int oldContentLength = -1;			// content length of a remote copy of the requested object.
	public String contentType = new String();	// Type of content - Request Header: POST Method.
	public boolean pragmaNoCache = false;		// No Cached version to be sent.
	public String ifModifiedSince = new String();		// Internet address date of the remote copy.	
	public String authorization = new String();		// Clients authorization.
	public String unrecognized = new String();			// Unrecognised item in header
	private String host= new String();
	private String proxyconnection = new String();
	private String acceptencoding = new String();
	private String acceptlanguage = new String();
	private String acceptcharset = new String();
	
	static String CR ="\r\n";
	
	
	// Parse a http header from stream -> true if successfull.
	public boolean parse(InputStream inputStream)
	{
		// TODO Auto-generated method stub
		
		// Read line by line
		DataInputStream lines;
	    StringTokenizer st;
	    
	    try 
		   {
	           lines = new DataInputStream(inputStream);
	           st = new StringTokenizer(lines.readLine());
	       }
		  catch (Exception e) 
		   {
	           return false;
	       }
		
	    //HTTP COMMAND LINE < <METHOD==get> <URL> <HTTP_VERSION> >
	      
	       method = getToken(st).toUpperCase();
	       url    = getToken(st);
	       version= getToken(st);
	       
	       
	       
	       while (true) 
		   {
	           try 
			   {
	               st = new StringTokenizer(lines.readLine());
	           } 
			   catch (Exception e) 
			   {
	               return false;
	           }
	           String Token = getToken(st); 
	           
	           // look for termination of HTTP command
	           if (Token.length()==0)
	               break;
	           
	           if (Token.equalsIgnoreCase("Host:")) 
	           {	               
	               host = getRemainder(st);           
	           }
	           if (Token.equalsIgnoreCase("Proxy_Connection:")) 
	           {	               
	               proxyconnection = getRemainder(st);           
	           }
	           if (Token.equalsIgnoreCase("Accept-Encoding:")) 
	           {	               
	               acceptencoding = getRemainder(st);           
	           }
	           if (Token.equalsIgnoreCase("Accept-Language:")) 
	           {	               
	               acceptlanguage = getRemainder(st);           
	           }
	           if (Token.equalsIgnoreCase("Accept-Charset:")) 
	           {	               
	               acceptcharset = getRemainder(st);           
	           }
	           
	           // line =<User-Agent: <Agent Description>>
	           if (Token.equalsIgnoreCase("USER-AGENT:")) 
	           {	               
	               userAgent = getRemainder(st);           
	           }	           
	           // line=<Accept: <Type>/<Form>
	           else if (Token.equalsIgnoreCase("ACCEPT:")) 
	           {	               
	               accept += " " + getRemainder(st);
	           } 
	           // line =<Referer: <URL>>
	           else if (Token.equalsIgnoreCase("REFERER:")) 
	           {	           
	               referer = getRemainder(st);
	           } 
	           // Pragma: <no-cache>
	           else if (Token.equalsIgnoreCase("PRAGMA:")) 
	           {	               
	               Token = getToken(st);
	               if (Token.equalsIgnoreCase("NO-CACHE"))
	                   pragmaNoCache = true;
	               else
	                   unrecognized += "Pragma:" + Token + " "+getRemainder(st) +"\n";            
	           } 
	           // Authenticate: Basic UUENCODED
	           else if (Token.equalsIgnoreCase("AUTHORIZATION:")) 
	           {	               
	               authorization=  getRemainder(st);
	           } 
	           // line =<If-Modified-Since: <http date>
               // *** Conditional GET replaces HEAD method ***
	           else if (Token.equalsIgnoreCase("IF-MODIFIED-SINCE:")) 
	           {	               
	              String str = getRemainder(st);
	              int index = str.indexOf(";");
	              if (index == -1) 
	              {
	                  ifModifiedSince  =str;
	              } 
	              else 
	              {
	                  ifModifiedSince  =str.substring(0,index);	                  
	                  index = str.indexOf("=");
	                  if (index != -1) 
	                  {
	                      str = str.substring(index+1);
	                      oldContentLength =Integer.parseInt(str);
	                  }
	              }
	           } 
	           else if (Token.equalsIgnoreCase("CONTENT-LENGTH:")) 
	           {
	               Token = getToken(st);
	               contentLength =Integer.parseInt(Token);
	           } 
	           else if (Token.equalsIgnoreCase("CONTENT-TYPE:")) 
	           {
	               contentType = getRemainder(st);
	           } 
	           else
	           {  
	               unrecognized += Token + " " + getRemainder(st) + CR;
	           }
	       }
	       return true;
	}

	// Send to next function.
	public String toString() 
	{
		return toString(true);
	}
	
	// Header->String conversion
	public String toString(boolean sendUnknown) 
	{
		// TODO Auto-generated method stub
		String Request; 
		
		if (method.length() == 0)
            method = "GET";
		
       Request = method +" "+ url + " "+version + CR;
       
       
       if (host.length() > 0)
           Request +="Host:" + host + CR;
       
       if (proxyconnection.length() > 0)
           Request +="Proxy-Connection:" + proxyconnection + CR;
       
       if (userAgent.length() > 0)
           Request +="User-Agent:" + userAgent + CR;

       if (referer.length() > 0)
           Request+= "Referer:"+ referer  + CR;

       if (pragmaNoCache)
           Request+= "Pragma: no-cache" + CR;

       if (ifModifiedSince.length() > 0)
           Request+= "If-Modified-Since: " + ifModifiedSince + CR;
           
       if (accept.length() > 0)
           Request += "Accept: " + accept + CR;
       else 
           Request += "Accept: */"+"* \r\n";
       
       if (acceptencoding.length() > 0);
           Request +="Accept-Encoding:" + acceptencoding + CR;
       
       if (acceptlanguage.length() > 0)
           Request +="Accept-Language:" + acceptlanguage + CR;
       
       if (acceptcharset.length() > 0)
           Request +="Accept-Charset:" + acceptcharset + CR;
       
       if (contentType.length() > 0)
           Request += "Content-Type: " + contentType   + CR;

       if (contentLength > 0)
           Request += "Content-Length: " + contentLength + CR;
                           
       
       if (authorization.length() !=0)
           Request += "Authorization: " + authorization + CR;

       if (sendUnknown) 
       {
           if (0 != unrecognized.length())
               Request += unrecognized;
       }   
      
       Request += CR;
       
       return Request;
	}
	
	// Returns the next token in the string
	// I/P: Partially tokanized string
	// O/P: The Next Token
	String  getToken(StringTokenizer st)
	{
		String str ="";
		
		if  (st.hasMoreTokens())
			str =st.nextToken();
		
		return str; 
	}   
	
	String  getRemainder(StringTokenizer st)
	{
		String str ="";
		
		if  (st.hasMoreTokens())
			str =st.nextToken();
		
		while (st.hasMoreTokens())
		{
			str +=" " + st.nextToken();
		}
		
		return str;
	}
	
	

}
