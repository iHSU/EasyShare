package security.container.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpClientUtil {

	private final static Log logger = LogFactory.getLog(HttpClientUtil.class);

	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpclient = null;
	private final static int connectTimeout = 10000;

	static {
		try {
			SSLContext sslContext = SSLContexts.custom().useTLS().build();
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} }, null);
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https",
							new SSLConnectionSocketFactory(sslContext)).build();

			connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			httpclient = HttpClients.custom().setConnectionManager(connManager)
					.build();
			
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom()
					.setTcpNoDelay(true).build();
			connManager.setDefaultSocketConfig(socketConfig);
			
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom()
					.setMaxHeaderCount(200).setMaxLineLength(2000).build();
			
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(200);
			connManager.setDefaultMaxPerRoute(20);
			
		} catch (KeyManagementException e) {
			logger.error("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException", e);
		}
	}

//	public static String postJsonBody(String url, int timeout,
//			Map<String, Object> map, String encoding) {
//		HttpPost post = new HttpPost(url);
//		try {
//			post.setHeader("Content-type", "application/json");
//			RequestConfig requestConfig = RequestConfig.custom()
//					.setSocketTimeout(timeout).setConnectTimeout(timeout)
//					.setConnectionRequestTimeout(timeout)
//					.setExpectContinueEnabled(false).build();
//			post.setConfig(requestConfig);
//
//			String str1 = JsonUtil.objectToJson(map).replace("\\", "");
//			post.setEntity(new StringEntity(str1, encoding));
//			logger.info("[HttpUtils Post] begin invoke url:" + url
//					+ " , params:" + str1);
//			CloseableHttpResponse response = httpclient.execute(post);
//			try {
//				HttpEntity entity = response.getEntity();
//				try {
//					if (entity != null) {
//						String str = EntityUtils.toString(entity, encoding);
//						logger.info("[HttpUtils Post]Debug response, url :"
//								+ url + " , response string :" + str);
//						return str;
//					}
//				} finally {
//					if (entity != null) {
//						entity.getContent().close();
//					}
//				}
//			} finally {
//				if (response != null) {
//					response.close();
//				}
//			}
//		} catch (UnsupportedEncodingException e) {
//			logger.error("UnsupportedEncodingException", e);
//		} catch (Exception e) {
//			logger.error("Exception", e);
//		} finally {
//			post.releaseConnection();
//		}
//		return "";
//	}

	@SuppressWarnings("deprecation")
	public static String get(String url, Map<String, String> params, String encode) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is " + value,
						e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);

		try {
			CloseableHttpResponse response = httpclient.execute(get);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						responseString = EntityUtils.toString(entity, encode);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format(
						"[HttpUtils Get]get response error, url:%s",
						sb.toString()), e);
				return responseString;
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format(
					"[HttpUtils Get]Debug url:%s , response string %s:",
					sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format(
					"[HttpUtils Get]invoke get timout error, url:%s",
					sb.toString()), e);
			return responseString;
		} catch (Exception e) {
			logger.error(
					String.format("[HttpUtils Get]invoke get error, url:%s",
							sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		return responseString;
	}

	@SuppressWarnings("deprecation")
	public static boolean get(String url, Map<String, String> params, String encode, String path) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is " + value,
						e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);

		try {
			CloseableHttpResponse response = httpclient.execute(get);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						if (entity.getContentLength() == 0) {
							return false;
						}
						InputStream is = entity.getContent();
						OutputStream os = new FileOutputStream(path);;
						int num;
						byte[] buf = new byte[1024];
						while((num = is.read(buf)) > 0){
							os.write(buf, 0, num);
						}
						os.close();
						is.close();
						return true;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format(
						"[HttpUtils Get]get response error, url:%s",
						sb.toString()), e);
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format(
					"[HttpUtils Get]Debug url:%s , response string %s:",
					sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format(
					"[HttpUtils Get]invoke get timout error, url:%s",
					sb.toString()), e);
		} catch (Exception e) {
			logger.error(
					String.format("[HttpUtils Get]invoke get error, url:%s",
							sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		
		return false;
	}
	
	

	/**
	 * HTTPS
	 * 
	 * @param reqURL
	 * @param params
	 * @return
	 */
	public static String post(String url,	Map<String, String> params) {

		String responseContent = null;

		HttpPost httpPost = new HttpPost(url);
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();

			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams,
					Consts.UTF_8));
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
			
				HttpEntity entity = response.getEntity(); 
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity,
								Consts.UTF_8);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI()
					+ ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;

	}
	
	public static String upload(String url, Map<String, String> params, String path) {
		File file = new File(path);
		String responseContent = null;
		if(!file.exists()) {
			return "File Not Exist!";
		}
		HttpPost httpPost = new HttpPost(url);
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setCharset(Charset.forName("UTF-8"));
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("upload_file", new FileBody(file));
			
			for (String name : params.keySet()) {
				String value = params.get(name);
				builder.addTextBody(name, value, ContentType.TEXT_PLAIN);
			}
			httpPost.setEntity(builder.build());
			httpPost.setConfig(requestConfig);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				
				HttpEntity entity = response.getEntity(); 
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity,
								Consts.UTF_8);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI()
					+ ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;
	}
	
	public static String upload(String url, Map<String, String> params, String path, CountingHttpEntity che) {
		File file = new File(path);
		String responseContent = null;
		if(!file.exists()) {
			return "File Not Exist!";
		}
		HttpPost httpPost = new HttpPost(url);
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setCharset(Charset.forName("UTF-8"));
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("upload_file", new FileBody(file));
			
			for (String name : params.keySet()) {
				String value = params.get(name);
				builder.addTextBody(name, value, ContentType.TEXT_PLAIN);
			}
			che.setHttpEntity(builder.build());
			httpPost.setEntity(che);
			httpPost.setConfig(requestConfig);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				
				HttpEntity entity = response.getEntity(); 
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity,
								Consts.UTF_8);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI()
					+ ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean download(String url, Map<String, String> params, String encode, Transfer transfer, String path) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is " + value,
						e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);

		try {
			CloseableHttpResponse response = httpclient.execute(get);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						if (entity.getContentLength() == 0) {
							return false;
						}
						transfer.setAmount(entity.getContentLength());
						InputStream is = entity.getContent();
						OutputStream os = new FileOutputStream(path);;
						int num;
						byte[] buf = new byte[1024];
						while((num = is.read(buf)) > 0){
							os.write(buf, 0, num);
							transfer.setTransferred(transfer.getTransferred() + num);
						}
						os.close();
						is.close();
						return true;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format(
						"[HttpUtils Get]get response error, url:%s",
						sb.toString()), e);
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format(
					"[HttpUtils Get]Debug url:%s , response string %s:",
					sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format(
					"[HttpUtils Get]invoke get timout error, url:%s",
					sb.toString()), e);
		} catch (Exception e) {
			logger.error(
					String.format("[HttpUtils Get]invoke get error, url:%s",
							sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static long getFileLength(String url, Map<String, String> params, String encode) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectTimeout).build();

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		for (Entry<String, String> entry : params.entrySet()) {
			if (i == 0 && !url.contains("?")) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			String value = entry.getValue();
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("encode http get params error, value is " + value,
						e);
				sb.append(URLEncoder.encode(value));
			}
			i++;
		}
		logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);

		try {
			CloseableHttpResponse response = httpclient.execute(get);
			try {
				HttpEntity entity = response.getEntity();
				logger.info("*******************"+entity.getContentLength());
				if (entity != null) {
					return entity.getContentLength();
				}
			} catch (Exception e) {
				logger.error(String.format(
						"[HttpUtils Get]get response error, url:%s",
						sb.toString()), e);
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info(String.format(
					"[HttpUtils Get]Debug url:%s , response string %s:",
					sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format(
					"[HttpUtils Get]invoke get timout error, url:%s",
					sb.toString()), e);
		} catch (Exception e) {
			logger.error(
					String.format("[HttpUtils Get]invoke get error, url:%s",
							sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		
		return 0;
	}

	
	
}