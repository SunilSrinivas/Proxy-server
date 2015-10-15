package httpProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import proxyCache.cacheResponseTuple;


public class proxyCache {
	
	
	CacheChecker cacheCheck = new CacheChecker();
	Cache cache = new Cache();
	

	private class cacheResponseTuple
	{
		
		private String httpVersion = "", responseCode = "", responseMessage = "";
		private Map<String, String> headers;
		private StringBuffer body;
		private byte[] responseObject;
		
		
		public void setHTTPVersion(String ver)
		{
			httpVersion = ver;
		}
		public String getHTTPVersion()
		{
			return httpVersion;
		}
		public void setResponseCode(String code)
		{
			responseCode = code;
		}
		public String getResponseCode()
		{
			return responseCode;
		}
		public void setResponseMessage(String mess)
		{
			responseMessage = mess;
		}
		public String getResponseMessage()
		{
			return responseMessage;
		}
		public void setBody(StringBuffer data)
		{
			body = data;
		}
		//public void appendBody(StringBuffer bod)
		//{
		//	body += bod;
		//}
		public StringBuffer getBody()
		{
			return body;
		}
		public void setHeaders(Map<String, String> head)
		{
			headers = head;
		}
		public void setHeader(String key, String value)
		{
			headers.remove(key);
			headers.put(key, value);
		}
		public void addHeader(String key, String value)
		{
			headers.put(key, value);
		}
		public void removeHeader(String key)
		{
			headers.remove(key);
		}

		public String getHeader(String head)
		{
			return headers.get(head);
		}
		
		
		public StringBuffer toStringBuffer()
		{
			StringBuffer tmp = new StringBuffer();
			tmp.append(getHTTPVersion() + " ");
			tmp.append(getResponseCode() + " ");
			tmp.append(getResponseMessage());
			tmp.append("\r\n");
			
			for(String i : headers.keySet())
			{
				tmp.append(i + ": " + getHeader(i) + "\r\n");
			}
			tmp.append("\r\n");
			tmp.append(getBody());
			tmp.append("\r\n\r\n");			
			return tmp;
		}
		
		
		
	}
	
	
	public class CacheChecker
	{
		
		
		
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
		
		private boolean isExpired(RequestHeader request)
		{
			
			if(getLastModified(request) > (new Date().getTime()))
			{
				return true;
			}
			else
			{
				return false;
			}
			
		}
		
		public void checkCache(RequestHeader request)
		{
			if(isExpired(request))
			{
				cache.expireCache(request);
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
			//System.out.print(header[0] + " " + header[1] + '\n');

		}
		tmpCacheObject.setHeaders(headers);


		int i = 0;

		
		while(tmp.hasNext())
		{
			body += tmp.nextLine() + '\n';
		}

		body += "\r\n";
				
		//tmpCacheObject.setBody(body);
		
		return tmpCacheObject;
	}
	
	
	private class  Cache 
	{
		
		private ConcurrentHashMap<RequestHeader, Map<cacheResponseTuple> table = new ConcurrentHashMap<RequestHeader,Map<cacheResponseTuple,byte[]>>();
		
		
		public boolean checkCache(RequestHeader key)
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
		
		public byte[] getCache(RequestHeader key)
		{
			if(table.containsKey(key))
			{
				return table.get(key).;
			}
			else
			{
				return null;
			}
		}
		
		public void expireCache(RequestHeader request)
		{
			table.remove(request);
			
		}

		public void putCache(RequestHeader request, byte[] response)
		{

				if((table.putIfAbsent(request, response)) == null)
				{
					updateCache(request, response);
				}
				else
				{
					table.replace(request, response);
				}
			
		}
		
		public void updateCache(RequestHeader request, byte[] response)
		{
			table.replace(request, response);
		}
		
		private boolean isFresh(RequestHeader request)
		{
			return true;
		}
	}
	
	
	public boolean inCache(RequestHeader request)
	{

			if( cache.checkCache(request))
			{
				return true;
			}
		
			return false;
	}
	
	public byte[] getCache(RequestHeader request)
	{
		return cache.getCache(request);
	}

	public void putCache(RequestHeader request, byte[] response)
	{
		cache.putCache(request, response);
	}
	public void updateCache(RequestHeader request, byte[] response)
	{
		cache.updateCache(request, response);
	}
	
}
