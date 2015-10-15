package httpProxy;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;







public class proxyCache {
	
	
	Cache cache = new Cache();
	cacheChecker cacheCheck = new cacheChecker();
	
	final static int CACHE_LIFETIME = 300;
	
	
	public void proxyCache()
	{
		

		cacheCheck.start();
		
	}
	
	
	public class cacheChecker extends Thread
	{
		
		public cacheChecker()
		{
		}
		
		
		private long getLastModified(RequestHeader request)
		{
			
			URL url;
			try {
				url = new URL(request.url);

        	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setFollowRedirects(true);
			connection.connect();
			
			return connection.getIfModifiedSince();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
	
			} catch (IOException e) {

			}
			return 1;
		}
		private long getContentLength(RequestHeader request)
		{
			
			URL url;
			try {
				url = new URL(request.url);

        	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setFollowRedirects(true);
			connection.connect();
			
			return connection.getContentLengthLong();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
	
			} catch (IOException e) {

			}
			return 1;
		}
		
		private boolean isExpired(RequestHeader request)
		{
			if(getContentLength(request) != request.contentLength)
			{
				return true;
			}
			else if(getLastModified(request) > (new Date().getTime()))
			{
				return true;
			}
			else if( Long.parseLong(cache.getCache(request.toString(false)).getHeader("Expires")) < (new Date().getTime())) 
			{
				return true;
			}
			else if( (cache.getCache(request.toString(false)).date + CACHE_LIFETIME) < (new Date().getTime()))
			{
				return true;
			}
			return false;
			
		}
		
		public void checkCache(RequestHeader request)
		{
			if(isExpired(request))
			{
				cache.expireCache(request.toString(false));
			}
			
		}
		
		
		public void run()
		{
			while(true)
			{
			
				for(Object request: cache.getKeys())
				{
					checkCache(((RequestHeader) request));
					
					
				}
				try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
		}
	}
	
	
	
	private cacheResponseTuple convertToCacheResponseTuple(String response)
	{

		
		cacheResponseTuple tmpCacheObject = new cacheResponseTuple();
		HashMap<String, String> headers = new HashMap<String, String>();
		String body = "";
		//String tmp[] = response.split("\\n", 1);
	
		Scanner tmp = new Scanner(response);
		
		StringTokenizer line = new StringTokenizer(tmp.nextLine());
		
		
		tmpCacheObject.setHTTPVersion(line.nextToken());
		tmpCacheObject.setResponseCode(line.nextToken());
		tmpCacheObject.setResponseMessage(line.nextToken());
				

	
		while(tmp.hasNext())
		{
			String tmpLine = tmp.nextLine();
			if (tmpLine.equalsIgnoreCase(""))
			{
				break;
			}
			String header[] = tmpLine.split(":");
			headers.put(header[0], header[1]);

		}
		tmpCacheObject.setHeaders(headers);


		int i = 0;

		
		while(tmp.hasNext())
		{
			body += tmp.nextLine() + '\n';
		}

		body += "\r\n";
				
		
		return tmpCacheObject;
	}
	
	
	private class  Cache 
	{
		
		private ConcurrentHashMap<String, cacheResponseTuple> table = new ConcurrentHashMap<String,cacheResponseTuple>();
		
		
		public boolean checkCache(String key)
		{	

			if(table.containsKey(key))
			{

				return true;
		
			}
			else
			{
				return false;
			}
		}
		
		public cacheResponseTuple getCache(String key)
		{
			if(table.containsKey(key))
			{
				return table.get(key);
			}
			else
			{
				return null;
			}
		}
		
		public void expireCache(String request)
		{
			
			table.remove(request);
			
		}

		public void putCache(String request, cacheResponseTuple response)
		{

				if(table.containsKey(request))
				{

					updateCache(request, response);
				}
				else
				{
					table.putIfAbsent(request, response);
				}
			
		}
		
		public void updateCache(String request,cacheResponseTuple response)
		{
			table.replace(request, response);
		}
		
		private boolean isFresh(RequestHeader request)
		{
			return true;
		}
		
		public  Set getKeys()
		{
			return table.keySet();
		}
	}
	
	
	public boolean inCache(String request)
	{
			if( cache.checkCache(request))
			{
				return true;
			}
			else
			{
		
			return false;
			}
	}
	
	public byte[] getCache(String request)
	{
	
		return cache.getCache(request).getResponseObject();
	}

	public void putCache(String request, cacheResponseTuple response)
	{
		cache.putCache(request, response);
	}
	public void updateCache(String request, cacheResponseTuple response)
	{
		cache.updateCache(request, response);
	}
	
}
