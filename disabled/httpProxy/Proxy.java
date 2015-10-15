package httpProxy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Proxy extends Thread
{
	
	// Member Variables
	
	Socket ClientSocket = null;              // Socket to client
	Socket ServerSocket = null;                // Socket to web server
	String localHostName = null;             // Local machine name
	String localHostIP = null;               // Local machine IP address
	Config config = null;                    // Config object

	
	Proxy(Socket clientSocket, Config configObject, proxyCache cach) 
	{
		// TODO Auto-generated constructor stub
		proxyCache cache = cach;
		config = configObject;
		ClientSocket = clientSocket;
		localHostName = config.getLocalHost();
		localHostIP = config.getLocalIP();
	}

	@SuppressWarnings("deprecation")
	public void run() 
	{
		// TODO Auto-generated method stub
		
		String serverName ="";
		URL url;

        RequestHeader request = new RequestHeader();
        ResponseHeader   response   = new ResponseHeader();
				
		try
		{
			// Read Request from client
			//System.out.println("Chkpt: 0.0");
			request.parse(ClientSocket.getInputStream());
			url = new URL(request.url);
            System.out.println("Client's Requested URL:  " + url);
			
           // System.out.println("Chkpt: 0");
            
			serverName  = url.getHost();
			System.out.println("No Caching Capability!!! \nForwarding Request to Server: "+serverName);
			ServerSocket = new Socket(serverName(request.url), serverPort(request.url));
			request.url = serverUrl(request.url);
			
			//System.out.println("Chkpt: 1");
			
			
			DataOutputStream serverOut = new DataOutputStream(ServerSocket.getOutputStream());
			
			// Send Request to Web Server
			System.out.println("\nRewritten Request Header: \n");
			serverOut.writeBytes(request.toString(false));
			System.out.println("  "+request.toString(false));		// Print Outgoing Request Header.
			serverOut.flush();
			
			//System.out.println("Chkpt: 2");
			
			// Send information to Server: POST Method
			for (int i =0; i < request.contentLength; i++)
			{
			   ServerSocket.getOutputStream().write(ClientSocket.getInputStream().read());    
			}
			ServerSocket.getOutputStream().flush(); 
			
			
			
			
			//System.out.println("Chkpt: 3");
			
			DataInputStream  Din  =  new DataInputStream(ServerSocket.getInputStream());
			DataOutputStream Dout = new DataOutputStream(ClientSocket.getOutputStream());
			String str = null, tempStr=null;				
			
			//System.out.println("Chkpt: 4");
			
			// Read consecutive lines and Send them to client
			if(Din.available()>0)
				while (true)
				{
					str = Din.readLine();
					tempStr= new String(str+"\r\n");
					System.out.println("tempstr: "+tempStr);
					// Send to client
					Dout.writeBytes(tempStr);						
					
					if (str.length() <= 0) 
						break;						
				}
			Dout.flush();
			//System.out.println("Chkpt: 5");
				
				
			// HTTP Body:
			// 1. Send to client
			// 2. Cache it
			InputStream  inStream  = ServerSocket.getInputStream();
			OutputStream outStream = ClientSocket.getOutputStream();
			
			
			
			//    java.util.Scanner s = new java.util.Scanner(inStream).useDelimiter("\\A");
			  
			
			
			//String test = s.next();
			
			//System.out.print("dlkjfhskjdhfkjsd");
			//System.out.print(test);
			
			
			
			/*ObjectInput ois = new ObjectInputStream(inStream);
			Object obj = ois.readObject();
			
			ObjectOutput oos = new ObjectOutputStream(outStream);
			oos.writeObject(obj);*/
			

			//ObjectInput reader = new  ObjectInputStream(inStream);
			//Object obj = reader.readObject();
			//ObjectOutput oos = new ObjectOutputStream(System.out);
			//oos.writeObject(obj);
			
			String line, test="";
			while((line = reader.readLine()) != null)
			{
				test += line + '\n';
			}
			reader.close();
			
			System.out.print(test  + "*************");
			
			/*byte data[] = new byte[2000];
			int count;
			while ((count=inStream.read(data))>0)
			{
				// Send to client
				outStream.write(data,0,count);
				System.out.print(data.toString() + "******************\n");*/
			//}
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
			writer.write(test);
			writer.flush();
			writer.close();
			
			System.out.println("Chkpt: 6");
			outStream.flush();
				
		}
		
		catch(UnknownHostException uhe1)
		{
			System.out.println("Server Not Found.");           
            
			// Notify client browser that server not found
			try
			{
				DataOutputStream out = new DataOutputStream(ClientSocket.getOutputStream());
	            out.writeBytes(response.formServerNotFound());
	            out.flush();	
			}
			catch(Exception e1)
			{
				//System.out.println("Error(Notify-serverabsent): ");
			}
			            
		}
		catch(Exception e)
		{
			try
			{	
				// Notify client that internal error accured in proxy
				DataOutputStream out = new DataOutputStream(ClientSocket.getOutputStream());
                out.writeBytes(response.formTimeout());
                out.flush();   
			}
			catch(Exception e2)
			{
				//System.out.println("Error(Notify-internalerror): ");
			}
		}
		finally
		{
			try
			{
				ClientSocket.getOutputStream().flush();
				ClientSocket.close();
			}
			catch(Exception e3)
			{
				//System.out.println("Error(Closing-clientsocket): ");
			}
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
