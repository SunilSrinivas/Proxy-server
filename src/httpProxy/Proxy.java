package httpProxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Proxy extends Thread
{
	
	// Member Variables
	
	Socket ClientSocket = null;              // Socket to client
	Socket ServerSocket = null;                // Socket to web server
	String localHostName = null;             // Local machine name
	String localHostIP = null;               // Local machine IP address
	Config config = null;                    // Config object
	proxyCache cache;
	RequestHeader request;
	String address;

	
	Proxy(Socket clientSocket, Config configObject, proxyCache cach, String addr) 
	{
		// TODO Auto-generated constructor stub
		
		config = configObject;
		ClientSocket = clientSocket;
		localHostName = config.getLocalHost();
		localHostIP = config.getLocalIP();
		cache = cach;
		address = addr;
		
	}
	

	public void run() 
	{				

			try {
				request = new RequestHeader();
				request.parse(ClientSocket.getInputStream());
				
				System.out.print(address + ": " +request.method + " " +request.url + "\n");
	            DataOutputStream ClientResponse = new DataOutputStream(ClientSocket.getOutputStream());
	            BufferedReader ClientRequest = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
	        

        		
        		
	            	if((request.method.equalsIgnoreCase("GET") || request.method.equalsIgnoreCase("HEAD")))
	            	{

	            		if(cache.inCache(request.toString()))
	            		{
	            		
	            			byte[] buffer = cache.getCache(request.toString());
	            			
		            		ByteArrayInputStream bos = new ByteArrayInputStream(buffer);
		            		ObjectInputStream os = new ObjectInputStream(bos);
	         
		            		int read;
	            			byte[] buf = new byte[1024];
	            			System.out.print("From Cache\n");
		            		while((read = os.read(buf, 0, 1024)) != -1)
		            		{
	            			ClientResponse.write(buf, 0, read);
		            		}
	            			ClientResponse.flush();

	       
	            		}
	            		else 
	            		{
	  
	            			URL url = new URL(request.url);
	                    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	            			connection.setRequestMethod(request.method);
	            			connection.setDoInput(true);
	            			connection.setDoOutput(true);
	            			HttpURLConnection.setFollowRedirects(true);
	            			InputStream inStream = connection.getInputStream();    

	            			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
	            			byte buff[] = new byte[1024];
	            			int read;
	            		
	            			cacheResponseTuple response = new cacheResponseTuple();
	            		
	            			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	            			ObjectOutputStream outStream = new ObjectOutputStream(byteStream);
	            
	            			try{
	            				while ( (read = inStream.read( buff, 0, 1024 )) != -1 )
	            				{
	            			
	            					outStream.write(buff, 0, read);
	            					ClientResponse.write( buff, 0, read );
	 
	            				}
	            				
	            				/////////////////////////////////////////
	            				Map<String, String> headers = new HashMap<String, String>();
	            				Map<String, List<String>> tmp = connection.getHeaderFields();
	            				Iterator<String> i = tmp.keySet().iterator();
	            				String key = i.next();
	            				
	            				StringTokenizer firstHeader = new StringTokenizer(tmp.get(key).toString());
	            				
	            				response.setHTTPVersion("HTTP/"+ connection.HTTP_VERSION);
	            				response.setResponseCode(String.valueOf(connection.getResponseCode()));
	            				response.setResponseMessage(connection.getResponseMessage());
	            				response.setBody(connection.getContent());
	            				
	            				while(i.hasNext())
	            				{
	            					key = i.next();


	            					String tmpLine = "";
	            					Iterator<String> j = (tmp.get(key)).iterator();
	            					while(j.hasNext())
	            					{
	            						tmpLine += j.next() + " ";
	            						
	            					}
	            					headers.put(key, tmpLine);
	            					
	            				}
	            				response.setHeaders(headers);
	            				/////////////////////////////////////////
	            				
	            				
	            				
	            				
	            			} catch (IOException e)
	            			{
	            			
	            				
	            				
	            			}
	            			finally {
	            					response.setResponseObject(byteStream.toByteArray());

	            					cache.putCache(request.toString(), response);
	            	
	            	
	            				}
	            
	            				reader.close();
	            			
	            		}
	            		

	            		ClientResponse.flush();
	            		ClientResponse.close();
	            		ClientRequest.close();
	            		
	            	
	            	}
	            	else if(request.method.equalsIgnoreCase("POST"))
	            	{
	                    URL url = new URL(request.url);
	                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	            		connection.setRequestMethod("POST");
	            		HttpURLConnection.setFollowRedirects(true);
	            		connection.setDoInput(true);
	            		connection.setDoOutput(true);
	            		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
	            		writer.write(request.toString(false));
	            		writer.close();
	            		
	            	}
	            	else
	            	{
	            		URL url = new URL(request.url);
	                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	            		connection.setRequestMethod("GET");
	            		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
	            		writer.write("HTTP/1.1 501 Method Not Implemented\r\n\r\n");
	            		writer.close();
	            		
	            		
	            	}
	            		
	            		ClientResponse.flush();
	            		ClientResponse.close();
	            		ClientRequest.close();
	            		
	            		
	            	
	            	

	        } catch (IOException e) {
	            
	        }
		}

	


	private String serverUrl(String url) 
	{
		// TODO Auto-generated method stub
		int i = url.indexOf("//");   
        if (i< 0) 
        	return url;   

        url = url.substring(i+2);
        i = url.indexOf("/");   
        if (i< 0) 
        	return url;  

        return url.substring(i); 
	}

	private int serverPort(String url) 
	{
		// TODO Auto-generated method stub
		
		// chop to "server.name:x/thing"
        int i = url.indexOf("//");   
        if (i< 0) return 80;  
        url = url.substring(i+2);
       
        // chop to  server.name:xx
        i = url.indexOf("/");
        if (0 < i) url = url.substring(0,i);
 
        // chop XX
        i = url.indexOf(":");
        if (0 < i)
		{
            return Integer.parseInt(url.substring(i).trim());
        }
       
        return 80;  
	}

	private String serverName(String url) 
	{
		// TODO Auto-generated method stub
		
		// chop to "server.name:x/thing"
        int i = url.indexOf("//");   
        if (i< 0) return "";  
        url = url.substring(i+2);
       
        // chop to  server.name:xx
        i = url.indexOf("/");
        if (0 < i) url = url.substring(0,i);
 
        // chop to server.name
        i = url.indexOf(":");
        if (0 < i) url = url.substring(0,i);
       
        return url;  
	}

}
