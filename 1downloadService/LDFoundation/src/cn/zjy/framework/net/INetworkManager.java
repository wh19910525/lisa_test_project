package cn.zjy.framework.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public interface INetworkManager 
{
	/** 网络类型 **/
	public enum NetworkType{Mobile, Wifi, Unknown};
	
	/** 开启一个HTTP链接。 **/
	HttpURLConnection openUrl(String urlStr) throws SocketTimeoutException;
	
	/** 开启一个HTTP链接。 **/
	HttpURLConnection openGzipUrl(String urlStr) throws SocketTimeoutException;
	
	/** 开启一个HTTP链接。 **/
	HttpURLConnection openUrl(String urlStr, String requestMethod) throws SocketTimeoutException;
	
	/** 开启一个HTTP链接。 **/
	HttpURLConnection openGZipUrl(String urlStr, String requestMethod) throws SocketTimeoutException;
	
	void writeParams(HttpURLConnection conn, HashMap<String, String> params) throws IOException ;
	
	/** 启动链接并将RespondCode值返回。 */
	int connect(HttpURLConnection httpConn);
	
	/**
	 * 将指定的HTTP链接内容存储到指定的的文件中。<br/>
	 * 返回值仅当参考。<br/>
	 * 
	 * @param httpConn
	 * @param filePath
	 *            指定存储的文件路径。
	 */
	boolean download2File(HttpURLConnection httpConn, String filePath);
	
	/**
	 * 将指定的HTTP链接内容存储到指定的的文件中。<br/>
	 * 返回值仅当参考。<br/>
	 * 
	 * @param httpConn
	 * @param filePath
	 *            指定存储的文件路径。
	 */
	boolean download2File(HttpURLConnection httpConn, String filePath, ProgressListener listener);
	
	/**
	 * 读取HttpURLConnection的数据并关闭相关流。<br/>
	 * 用于读取小数据流，且无法在读取过程中中止流操作。<br/>
	 */
	byte[] fetchData(HttpURLConnection httpConn);
	
	/**
	 * 读取HttpURLConnection的数据并关闭相关流。<br/>
	 * 用于读取小数据流，且无法在读取过程中中止流操作。<br/>
	 */
	byte[] fetchGzipData(HttpURLConnection httpConn);
}
