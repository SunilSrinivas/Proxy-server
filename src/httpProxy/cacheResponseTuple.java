package httpProxy;

import java.util.Date;
import java.util.Map;

public class cacheResponseTuple
{
	
	private String httpVersion = "", responseCode = "", responseMessage = "";
	private Map<String, String> headers;
	private Object body;
	private byte[] responseObject;
	public long date = new Date().getTime();
	
	
	public void setResponseObject(byte[] repObj)
	{
		responseObject = repObj;
	}
	public byte[] getResponseObject()
	{
		return responseObject;
	}
	
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
	public void setBody(Object data)
	{
		body = data;
	}
	//public void appendBody(StringBuffer bod)
	//{
	//	body += bod;
	//}
	public Object getBody()
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