package cn.zjy.framework.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkManagerImpl implements INetworkManager
{
    private static INetworkManager _instance;
    private final Context _context;

    public static INetworkManager getInstance(Context context)
    {
	if (_instance == null)
	{
	    _instance = new NetworkManagerImpl(context);
	}
	return _instance;
    }

    private NetworkManagerImpl(Context context)
    {
	this._context = context.getApplicationContext();
    }

    @Override
    public HttpURLConnection openUrl(String urlStr) throws SocketTimeoutException
    {
	return openUrl(urlStr, null);
    }

    @Override
    public HttpURLConnection openUrl(String urlStr, String requestMethod) throws SocketTimeoutException
    {
	URL urlURL = null;
	HttpURLConnection httpConn = null;
	try
	{
	    urlURL = new URL(urlStr);
	    // 需要android.permission.ACCESS_NETWORK_STATE
	    // 在没有网络的情况下，返回值为null。
	    NetworkInfo networkInfo = ((ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE))
		    .getActiveNetworkInfo();
	    // 如果是使用的运营商网络
	    if (networkInfo != null)
	    {
//		if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
//		{
//		    // 获取默认代理主机ip
//		    String host = android.net.Proxy.getDefaultHost();
//		    // 获取端口
//		    int port = android.net.Proxy.getDefaultPort();
//		    if (host != null && port != -1)
//		    {
//			// 封装代理連接主机IP与端口号。
//			InetSocketAddress inetAddress = new InetSocketAddress(host, port);
//			// 根据URL链接获取代理类型，本链接适用于TYPE.HTTP
//			java.net.Proxy.Type proxyType = java.net.Proxy.Type.valueOf(urlURL.getProtocol().toUpperCase(
//				Locale.getDefault()));
//			java.net.Proxy javaProxy = new java.net.Proxy(proxyType, inetAddress);
//			httpConn = (HttpURLConnection) urlURL.openConnection(javaProxy);
//		    }
//		    else
//		    {
//			httpConn = (HttpURLConnection) urlURL.openConnection();
//		    }
//		}
//		else
//		{
		    httpConn = (HttpURLConnection) urlURL.openConnection();
//		}
		if (requestMethod != null)
		{
		    if (requestMethod.equalsIgnoreCase("POST"))
		    {
			httpConn.setDoOutput(true);
		    }
		    httpConn.setRequestMethod(requestMethod);
		}
		httpConn.setConnectTimeout(Constants.NETWORK_OPEN_MAXTIME_SHORT);
		httpConn.setReadTimeout(Constants.NETWORK_READ_MAXTIME_SHORT);
		httpConn.setDoInput(true);
	    }
	}
	catch (NullPointerException npe)
	{
	    npe.printStackTrace();
	}
	catch (MalformedURLException e)
	{
	    e.printStackTrace();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	return httpConn;
    }

    @Override
    public HttpURLConnection openGzipUrl(String urlStr) throws SocketTimeoutException
    {
	return openGZipUrl(urlStr, null);
    }

    @Override
    public HttpURLConnection openGZipUrl(String urlStr, String requestMethod) throws SocketTimeoutException
    {
	URL urlURL = null;
	HttpURLConnection httpConn = null;
	try
	{
	    urlURL = new URL(urlStr);
	    // 需要android.permission.ACCESS_NETWORK_STATE
	    // 在没有网络的情况下，返回值为null。
	    NetworkInfo networkInfo = ((ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE))
		    .getActiveNetworkInfo();
	    // 如果是使用的运营商网络
	    if (networkInfo != null)
	    {
		if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
		{
//		    // 获取默认代理主机ip
//		    String host = android.net.Proxy.getDefaultHost();
//		    // 获取端口
//		    int port = android.net.Proxy.getDefaultPort();
//		    if (host != null && port != -1)
//		    {
//			// 封装代理連接主机IP与端口号。
//			InetSocketAddress inetAddress = new InetSocketAddress(host, port);
//			// 根据URL链接获取代理类型，本链接适用于TYPE.HTTP
//			java.net.Proxy.Type proxyType = java.net.Proxy.Type.valueOf(urlURL.getProtocol().toUpperCase(
//				Locale.getDefault()));
//			java.net.Proxy javaProxy = new java.net.Proxy(proxyType, inetAddress);
//			httpConn = (HttpURLConnection) urlURL.openConnection(javaProxy);
//		    }
//		    else
//		    {
			httpConn = (HttpURLConnection) urlURL.openConnection();
//		    }
		}
		else
		{
		    httpConn = (HttpURLConnection) urlURL.openConnection();
		}
		if (requestMethod != null)
		{
		    if (requestMethod.equalsIgnoreCase("POST"))
		    {
			httpConn.setDoOutput(true);
		    }
		    httpConn.setRequestMethod(requestMethod);
		}
		// httpConn.setRequestProperty("Accept-Encoding",
		// "gzip, deflate");
		httpConn.setRequestProperty("Accept-Encoding", "gzip");
		httpConn.setConnectTimeout(Constants.NETWORK_OPEN_MAXTIME_SHORT);
		httpConn.setReadTimeout(Constants.NETWORK_READ_MAXTIME_SHORT);
		httpConn.setDoInput(true);
	    }
	}
	catch (NullPointerException npe)
	{
	    npe.printStackTrace();
	}
	catch (MalformedURLException e)
	{
	    e.printStackTrace();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	return httpConn;
    }

    @Override
    public void writeParams(HttpURLConnection conn, HashMap<String, String> params) throws IOException
    {
	final StringBuilder sb = new StringBuilder();
	Set<Entry<String, String>> entrys = params.entrySet();
	for (Entry<String, String> entry : entrys)
	{
	    if (sb.length() > 0)
	    {
		sb.append("&");
	    }
	    sb.append(entry.getKey() + "=");
	    sb.append(entry.getValue());
	}
	final BufferedOutputStream output = new BufferedOutputStream(conn.getOutputStream());
	output.write(sb.toString().getBytes());
	output.close();

    }

    @Override
    public int connect(HttpURLConnection httpConn)
    {
	int code = -1;
	if (httpConn != null)
	{
	    try
	    {
		httpConn.connect();
		code = httpConn.getResponseCode();
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
	return code;
    }
    
    @Override
    public boolean download2File(HttpURLConnection httpConn, String filePath)
    {
	return download2File(httpConn, filePath, ProgressListener.NULL);
    }

    @Override
    public boolean download2File(HttpURLConnection httpConn, String filePath, ProgressListener listener)
    {
	boolean result = true;
	File file = new File(filePath);
	File parent = file.getParentFile();
	if (parent != null)
	{
	    parent.mkdirs();
	}
	FileOutputStream fos = null;
	byte[] data = new byte[1024];
	int readLength = -1;
	int totalLength = httpConn.getContentLength();
	int currentLength = 0;
	BufferedInputStream is = null;
	try
	{
	    fos = new FileOutputStream(file);
	    is = new BufferedInputStream(httpConn.getInputStream());
	    while ((readLength = is.read(data)) != -1)
	    {
		fos.write(data, 0, readLength);
		currentLength += readLength;
		listener.progress(totalLength, currentLength);
		fos.flush();
	    }
	    fos.flush();
	}
	catch (IOException ie)
	{
	    result = false;
	    ie.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (is != null)
		{
		    is.close();
		}
		if (fos != null)
		{
		    fos.close();
		}
	    }
	    catch (IOException ie)
	    {}
	    httpConn.disconnect();
	}
	return result;
    }

    @Override
    public byte[] fetchData(HttpURLConnection httpConn)
    {
	byte[] data = null;
	ByteArrayOutputStream baos = null;
	InputStream is = null;
	int read = -1;
	try
	{
	    baos = new ByteArrayOutputStream();
	    is = httpConn.getInputStream();
	    while ((read = is.read()) != -1)
	    {
		baos.write(read);
	    }
	    data = baos.toByteArray();
	}
	catch (IOException ie)
	{
	    ie.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (is != null)
		{
		    is.close();
		}
		if (baos != null)
		{
		    baos.close();
		}
		if (httpConn != null)
		{
		    httpConn.disconnect();
		}
	    }
	    catch (IOException ie)
	    {}
	    if(httpConn!=null)
	    {
	    	httpConn.disconnect();
	    }
	}
	return data;
    }

    @Override
    public byte[] fetchGzipData(HttpURLConnection httpConn)
    {
	byte[] data = null;
	ByteArrayOutputStream baos = null;
	InputStream is = null;
	int read = -1;
	try
	{
	    baos = new ByteArrayOutputStream();
	    is = new GZIPInputStream(httpConn.getInputStream());
	    while ((read = is.read()) != -1)
	    {
		baos.write(read);
	    }
	    data = baos.toByteArray();
	}
	catch (IOException ie)
	{
	    ie.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (is != null)
		{
		    is.close();
		}
		if (baos != null)
		{
		    baos.close();
		}
		if (httpConn != null)
		{
		    httpConn.disconnect();
		}
	    }
	    catch (IOException ie)
	    {}
	    if(httpConn!=null)
	    {
	    	httpConn.disconnect();
	    }
	}
	return data;
    }
}
