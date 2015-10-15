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

	/**
	 * @param args
	 */
	
	static ServerSocket MainSocket = null;
	static Config config; 
	
	final static int mainPort = 5152;
	final static int maxmainport = 65536;
	
	public static void main(String[] args) 
	{	// TODO Auto-generated method stub
		int defaultPort;
		

		
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
			// Create Config class Object  
			config = new Config();
			config.setLocalHost(InetAddress.getLocalHost().getHostName());
			String tmp = InetAddress.getLocalHost().toString();
			config.setLocalIP(tmp.substring(tmp.indexOf('/')+1));
			config.setProxyMachineNameAndPort(InetAddress.getLocalHost().getHostName()+":"+defaultPort);			
			
						
			// Create The Main Socket
			MainSocket = new ServerSocket(defaultPort);
			
			
			// All Done :) !!!

			proxyCache cache = new proxyCache();
			
			// .Main Continous Loop -> Listen on mainsocket and pass request to new threads as it comes inn
			while(true)
			{
				Socket ClientSocket = MainSocket.accept();
				
				Proxy thread = new Proxy(ClientSocket, config, cache);
				thread.start();
			}
		}
		
		catch(Exception e1)
		{
		}
		finally
		{
			try
			{
				MainSocket.close();
			}
			catch(Exception e2)
			{
			}
			
		}

	}

}
