package cn.joy.framework.kits;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
/**
 * Http操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class HttpKit{
	private static Logger logger = Logger.getLogger(HttpKit.class);
	
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; //8KB
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int maxConnections = 200; //http请求最大并发连接数
    private static int maxConnectionsPerRoute = 20; //http请求最大并发连接数
    private static int socketTimeout = 20; //超时时间，默认20秒
    private static int maxRetries = 5;//错误尝试次数，错误异常表请在RetryHandler添加
    private static int httpThreadCount = 3;//http线程池数量

	private static HttpClient createHttpClient() {
		try {
			//HttpClients.createDefault();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			
			ConnManagerParams.setTimeout(params, socketTimeout*1000);
	        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(maxConnectionsPerRoute));
	        ConnManagerParams.setMaxTotalConnections(params, maxConnections);

	        HttpConnectionParams.setTcpNoDelay(params, true);
	        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);
	        
	        HttpConnectionParams.setConnectionTimeout(params, socketTimeout*1000);  
	        HttpConnectionParams.setSoTimeout(params, socketTimeout*1000);  

			HttpProtocolParams.setUseExpectContinue(params, true);
			/*
			 * if (requestTimeout > 0)
			 * HttpConnectionParams.setConnectionTimeout(params, requestTimeout
			 * * 1000); if (soTimeout > 0)
			 * HttpConnectionParams.setSoTimeout(params, soTimeout * 1000);
			 */

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}

	}
	
	public static String post(String url, Map<String,Object> datas){
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		Set<Entry<String,Object>> temps = datas.entrySet();
		for(Entry<String,Object> temp : temps){
			String key = temp.getKey();
			Object value = temp.getValue();
			if(value==null)	value = "";
			if(key!=null)
				params.add(new BasicNameValuePair(key, value.toString()));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		return handleResponse(httpClient, httpPost);
	}

	public static String get(String url) {
		HttpClient httpClient = createHttpClient();
		HttpGet httpGet = new HttpGet(url);
		return handleResponse(httpClient, httpGet);
	}

	public static String handleResponse(HttpClient client, HttpRequestBase request) {
		String responseText = "";
		try {
			request.setHeader("Connection", "close");
			HttpResponse response = client.execute(request);
			if (response != null) {
				int code = response.getStatusLine().getStatusCode();
				if (code == 200){
					responseText = EntityUtils.toString(response.getEntity());
					EntityUtils.consume(response.getEntity());
				}else
					responseText = "statusCode:"+code;
			}
		} catch (SocketTimeoutException e) {
			logger.error("", e);
		} catch (ConnectException e) {
			logger.error("", e);
		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		} finally{
			if (request != null)
				request.abort();
		}
		return responseText;
	}
	
	static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port,
					autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
	
	public static void writeResponse(HttpServletResponse response, String content){
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain");
			response.getWriter().write(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
