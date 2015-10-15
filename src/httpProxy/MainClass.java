package httpProxy;
/*
 * Main Class that runs the proxy.
 * For every request it starts a seperate thread which claas the user-defined proxy.java class over rides the library class proxy.java. Start function is native.
 *  
*/

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;




public class MainClass extends Thread 
{

	
	static ServerSocket MainSocket = null;
	static Config config; 
	
	final static int mainPort = 5153;
	final static int maxmainport = 65536;
    static proxyCache cache;
	
	public static void main(String[] args) 
	{	
		int defaultPort;
		cache = new proxyCache();
		
		switch (args.length)
		{
			case 0: defaultPort = mainPort;
					break;

			case 1: try
					{
						defaultPort = Integer.parseInt(args[0]);
					}
					catch (NumberFormatException e)
					{
						System.out.println("Error: Invalid daemon port");
						return;
					}
					if (defaultPort > maxmainport)
					{
						System.out.println("Error: Invalid daemon port");
						return;
					}
					break;

			default:System.out.println("Usage: Proxy [daemon port]");
					return;
		}
		
		try
		{			
			// Create Config class Object,this calls the constructor to the config class
			       config = new Config();
			//java.net.inetaddress represents an internet protocol address
			//get localhost returns the address of the local host,get hostname gets the host name of this IP address
			       config.setLocalHost(InetAddress.getLocalHost().getHostName());
			//converts the IP address to a string
			       String tmp = InetAddress.getLocalHost().toString();
			//substring returns a new string that is a substring of this string
			       config.setLocalIP(tmp.substring(tmp.indexOf('/')+1));
			       config.setProxyMachineNameAndPort(InetAddress.getLocalHost().getHostName()+":"+defaultPort);			
		    // Creats a server socket bound to the specified port
			       MainSocket = new ServerSocket(defaultPort);
			       System.out.println(defaultPort);
			
			//Main Continous Loop -> Listen on mainsocket and pass request to new threads as it comes inn
			         while(true)
			           {
				//Listens for a connection to be made to this socket and accepts it. The method blocks until a 
				// connection is made. 
                       Socket ClientSocket = MainSocket.accept();
 
                //spawning a new thread for every accept to connection       
				       Proxy thread = new Proxy(ClientSocket, config, cache, tmp);
				// Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread. 
				// The result is that two threads are running concurrently: the current thread (which returns from the call 
				//  to the start method) and the other thread (which executes its run method).      
				       thread.start();
			}
		}
		
		catch(Exception e1)
		{
			System.out.println("Error(Setting up Proxy): ");
		}
		finally
		{
			try
			{
		//Closes this socket. Any thread currently blocked in accept() will throw a SocketException. 
		//If this socket has an associated channel then the channel is closed as well.
		    MainSocket.close();
			}
			catch(Exception e2)
			{
				System.out.println("Error(Closing MainSocket)");
			}
			
		}

	}

}
