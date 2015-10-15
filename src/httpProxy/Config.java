package httpProxy;

public class Config {

	private String localHost;
	private String localIP;
	private String proxyMachineNameAndPort;
	
	public void setLocalHost(String hostName) 
	{	// TODO Auto-generated method stub		
		localHost = hostName;
	}
	
	public String getLocalHost() 
	{	// TODO Auto-generated method stub		
		return localHost;
	}

	public void setLocalIP(String ipAddress) 
	{	// TODO Auto-generated method stub		
		localIP = ipAddress;		
	}
	
	public String getLocalIP() 
	{	// TODO Auto-generated method stub		
		return localIP;
	}
	
	

	public void setProxyMachineNameAndPort(String string) 
	{	// TODO Auto-generated method stub		
		proxyMachineNameAndPort = string;
	}
	
	public String getProxyMachineNameAndPort()
	{	// TODO Auto-generated method stub		
		return proxyMachineNameAndPort;
	}

}
