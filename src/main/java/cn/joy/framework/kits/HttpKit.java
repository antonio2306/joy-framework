package cn.joy.framework.kits;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
 * 
 * @author liyy
 * @date 2014-05-20
 */
public class HttpKit {
	private static Logger logger = Logger.getLogger(HttpKit.class);

	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	private static int maxConnections = 200; // http请求最大并发连接数
	private static int maxConnectionsPerRoute = 20; // http请求最大并发连接数
	private static int socketTimeout = 20; // 超时时间，默认20秒
	private static int maxRetries = 5;// 错误尝试次数，错误异常表请在RetryHandler添加
	private static int httpThreadCount = 3;// http线程池数量

	private static HttpClient createHttpClient() {
		try {
			// HttpClients.createDefault();
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);

			// ConnManagerParams.setTimeout(params, socketTimeout*1000);
			// ConnManagerParams.setMaxConnectionsPerRoute(params, new
			// ConnPerRouteBean(maxConnectionsPerRoute));
			// ConnManagerParams.setMaxTotalConnections(params, maxConnections);

			HttpConnectionParams.setTcpNoDelay(params, true);
			HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);

			HttpConnectionParams.setConnectionTimeout(params, socketTimeout * 1000);
			HttpConnectionParams.setSoTimeout(params, socketTimeout * 1000);

			HttpProtocolParams.setUseExpectContinue(params, true);
			/*
			 * if (requestTimeout > 0)
			 * HttpConnectionParams.setConnectionTimeout(params, requestTimeout
			 * * 1000); if (soTimeout > 0)
			 * HttpConnectionParams.setSoTimeout(params, soTimeout * 1000);
			 */

			SchemeRegistry registry = new SchemeRegistry();
			// 此处不能直接使用新的Scheme的构造方法，否则会有https的验证问题
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(registry);
			cm.setMaxTotal(maxConnections);
			cm.setDefaultMaxPerRoute(maxConnectionsPerRoute);

			return new DefaultHttpClient(cm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}

	}

	public static String post(String url, String data) {
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			StringEntity entity = new StringEntity(data);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return handleResponse(httpClient, httpPost);
	}

	public static String post(String url, String data, String contentType) {
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			StringEntity entity = new StringEntity(data);
			httpPost.setEntity(entity);
			if ("xml".equals(contentType))
				httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			else if (StringKit.isNotEmpty(contentType))
				httpPost.setHeader("Content-Type", contentType);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return handleResponse(httpClient, httpPost);
	}

	public static String post(String url, byte[] data, String contentType) {
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			ByteArrayEntity entity = new ByteArrayEntity(data);
			httpPost.setEntity(entity);
			if ("xml".equals(contentType))
				httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			else if ("file".equals(contentType))
				httpPost.setHeader("Content-Type", "application/octet-stream");
			else if (StringKit.isNotEmpty(contentType))
				httpPost.setHeader("Content-Type", contentType);
		} catch (Exception e) {
			logger.error("", e);
		}

		return handleResponse(httpClient, httpPost);
	}

	public static String post(String url, Map<String, Object> datas) {
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		Set<Entry<String, Object>> temps = datas.entrySet();
		for (Entry<String, Object> temp : temps) {
			String key = temp.getKey();
			Object value = temp.getValue();
			if (value == null)
				value = "";
			if (key != null)
				params.add(new BasicNameValuePair(key, value.toString()));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return handleResponse(httpClient, httpPost);
	}

	public static String post(String url, Map<String, Object> datas, Map<String, File> files) {
		HttpClient httpClient = createHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity reqEntity = new MultipartEntity(null, null, Charset.forName("UTF-8"));
			if (files != null)
				for (Iterator<Entry<String, File>> it = files.entrySet().iterator(); it.hasNext();) {
					Entry<String, File> fileEntry = it.next();
					FileBody file = new FileBody(fileEntry.getValue(), "text/plain", "UTF-8");
					reqEntity.addPart(fileEntry.getKey(), file);
				}
			if (datas != null)
				for (Iterator<Entry<String, Object>> it = datas.entrySet().iterator(); it.hasNext();) {
					Entry<String, Object> stringEntry = it.next();
					Object val = stringEntry.getValue();
					if (val == null)
						val = "";
					StringBody str = new StringBody(val.toString(), Charset.forName("UTF-8"));
					reqEntity.addPart(stringEntry.getKey(), str);
				}
			httpPost.setEntity(reqEntity);
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

	public static byte[] getBytes(String url) {
		HttpClient httpClient = createHttpClient();
		HttpGet httpGet = new HttpGet(url);
		return handleBytesResponse(httpClient, httpGet);
	}

	public static String handleResponse(HttpClient client, HttpRequestBase request) {
		String responseText = "";
		try {
			request.setHeader("Connection", "close");
			HttpResponse response = client.execute(request);
			if (response != null) {
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					responseText = EntityUtils.toString(response.getEntity());
					EntityUtils.consume(response.getEntity());
				} else
					responseText = "statusCode:" + code;
			}
		} catch (SocketTimeoutException e) {
			logger.error("", e);
		} catch (ConnectException e) {
			logger.error("", e);
		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (request != null)
				request.abort();
		}
		return responseText;
	}

	public static byte[] handleBytesResponse(HttpClient client, HttpRequestBase request) {
		byte[] responseText = null;
		try {
			request.setHeader("Connection", "close");
			HttpResponse response = client.execute(request);
			if (response != null) {
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					responseText = EntityUtils.toByteArray(response.getEntity());
					EntityUtils.consume(response.getEntity());
				}
			}
		} catch (SocketTimeoutException e) {
			logger.error("", e);
		} catch (ConnectException e) {
			logger.error("", e);
		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (request != null)
				request.abort();
		}
		return responseText;
	}

	static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static void writeResponse(HttpServletResponse response, String content) {
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain");
			response.getWriter().write(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String encodeParam(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return param;
	}

	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		return getParameterMap(request, null);
	}

	public static Map<String, String> getParameterMap(HttpServletRequest request, String namePrefix) {
		// 参数Map
		Map<String, String[]> params = request.getParameterMap();
		// 返回值Map
		Map<String, String> returnMap = new HashMap<String, String>();
		String name = "";
		String value = "";
		boolean filterByNamePrefix = StringKit.isNotEmpty(namePrefix);
		for (Entry<String, String[]> entry : params.entrySet()) {
			name = (String) entry.getKey();
			if (filterByNamePrefix && !name.startsWith(namePrefix))
				continue;
			String[] values = entry.getValue();
			if (null == values) {
				value = "";
			} else if (values.length == 1) {
				value = values[0];
			} else {
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			}
			returnMap.put(name, value);
		}
		return returnMap;
	}

	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringKit.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (StringKit.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringKit.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("X-Real-IP");
		if (StringKit.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (StringKit.isNotEmpty(ip) && ip.length() > 15) { // "***.***.***.***".length()
															// = 15
			int index = ip.indexOf(",");
			if (index != -1)
				ip = ip.substring(0, index);
		}
		return ip;
	}

	public static void setCORS(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
	}

	public static Cookie getCookie(HttpServletRequest request, String name) {
		return null;
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {

	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {

	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value,
			int maxAge) {

	}
}
