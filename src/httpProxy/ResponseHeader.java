package httpProxy;

public class ResponseHeader 
{
	
	static String CR = "\r\n";
	static String HTTP_PROTOCOL = "HTTP/1.0";
	static String HTTP_SERVER = "Java Proxy Server";
	
	String lastModified = "";	
	String extraErrorString = "";
	long contentLength = 0;
	
	
	private String formError(String Error, String Description) 
	{
		// TODO Auto-generated method stub
		
		String body=formErrorBody(Error,Description);
		String header =new String();

		header +=HTTP_PROTOCOL +" " + Error + CR;
		header +="Server: " + HTTP_SERVER   + CR;
		header +="MIME-version: 1.0"        + CR;
		header +="Content-type: text/html"  + CR;
		   
		if (0 < lastModified.length())
			header +="Last-Modified: " + lastModified +CR;
		
		header +="Content-Length: " + String.valueOf(body.length())+ CR;
		
		
		header += CR;
		header += body;
		   
		return header;
	}
	
	
	
	private String formErrorBody(String Error, String Description) 
	{
		// TODO Auto-generated method stub
		String out;
		   //HTML Error Body
		out  ="<HTML><HEAD><TITLE>";
		out += Error ; 
		out +="</TITLE></HEAD>";
		out +="<BODY><center><H3>" + Error +"</H3><BR />";
		out +="<P>"+Description+"</P></center></BODY></HTML>";                      
		
		return out;
	}




	// Proxy failed to locate requested server.
	public String formServerNotFound() {
		// TODO Auto-generated method stub
		
		return formError("503 Gateway timeout","The requested server was not found");
	}
	
	// Request taking too long.
	public String formTimeout() {
		// TODO Auto-generated method stub
		
		return formError("503 Gateway timeout","The connection timed out");
	}

}
