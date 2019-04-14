package com.sg.springbootsgsty.util.httpUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons
 * -io-2.4.jar
 */
public class HttpClientUtils
{

	private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	public static final int connTimeout = 5000;
	public static final int readTimeout = 5000;
	public static final String charset = "UTF-8";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	private static HttpClient client = null;

	static
	{
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(128);
		client = HttpClients.custom().setConnectionManager(cm).build();
		// 开启fiddler代理,注意只对http生效，https会重新生成client
		// client = HttpClients.custom().setProxy(new HttpHost("127.0.0.1",
		// 8888)).setConnectionManager(cm).build();
	}

	public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return post(url, parameterStr, "application/x-www-form-urlencoded", charset, connTimeout, readTimeout);
	}

	public static String postParameters(String url, String parameterStr, String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return post(url, parameterStr, "application/x-www-form-urlencoded", charset, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return postForm(url, params, null, connTimeout, readTimeout);
	}

	public static byte[] postBytes(String url, Map<String, String> params, Map<String, String> headers) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return postFormByte(url, params, headers, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params, Map<String, String> headers) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return postForm(url, params, headers, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return postForm(url, params, headers, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		return postForm(url, params, null, connTimeout, readTimeout);
	}

	public static String get(String url) throws Exception
	{
		return get(url, charset, null, null);
	}

	public static String get(String url, String charset) throws Exception
	{
		return get(url, charset, connTimeout, readTimeout);
	}

	/**
	 * 发送一个 Post 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/xml "application/x-www-form-urlencoded"
	 *            a=1&b=2&c=3
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String url, String body, String mimeType, String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		String result = "";
		try
		{
			if (StringUtils.isNotBlank(body))
			{
				HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
				post.setEntity(entity);
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());

			HttpResponse res;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 提交form表单
	 * 
	 * @param url
	 * @param params
	 * @param connTimeout
	 * @param readTimeout
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{

		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		try
		{
			if (params != null && !params.isEmpty())
			{
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet)
				{
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
				post.setEntity(entity);
			}

			if (headers != null && !headers.isEmpty())
			{
				for (Entry<String, String> entry : headers.entrySet())
				{
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());
			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}

			int respStatus = res.getStatusLine().getStatusCode();
			if(respStatus != 200){
			    logger.warn("访问{}失败，http返回码为{}", url, respStatus);
            }

			return IOUtils.toString(res.getEntity().getContent(), charset);
		} catch (Exception e)
		{
			e.printStackTrace();
			logger.error("HttpClientUtils.postForm exception. url=" + url, e);
			return null;
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * 提交form表单
	 * 
	 * @param url
	 * @param params
	 * @param connTimeout
	 * @param readTimeout
	 * @return 返回二进制流
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static byte[] postFormByte(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{

		HttpClient client = null;
		HttpResponse res = null;
		HttpPost post = new HttpPost(url);
		try
		{
			if (params != null && !params.isEmpty())
			{
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet)
				{
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
				post.setEntity(entity);
			}

			if (headers != null && !headers.isEmpty())
			{
				for (Entry<String, String> entry : headers.entrySet())
				{
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}

			if (res.getStatusLine().getStatusCode() != 200 && res.getStatusLine().getStatusCode() != 201)
				return new byte[] {};
			return IOUtils.toByteArray(res.getEntity().getContent());
		} catch (Exception e)
		{
			logger.error(url + "__HttpClientUtils.postFormByte() exception ...", e);
			return null;
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String get(String url, String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{

		HttpClient client = null;
		HttpGet get = new HttpGet(url);
		String result = "";
		try
		{
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			get.setConfig(customReqConf.build());

			HttpResponse res = null;

			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(get);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(get);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			get.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @param redirectsEnabled
	 *            是否允许自动重定向. （默认是允许）
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static HttpResponse get(String url, String charset, Map<String, String> headers, Integer connTimeout, Integer readTimeout, boolean redirectsEnabled) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{

		HttpClient client = null;
		HttpGet get = new HttpGet(url);
		HttpResponse res = null;
		try
		{
			if (headers != null && !headers.isEmpty())
			{
				for (Entry<String, String> entry : headers.entrySet())
				{
					get.addHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setRedirectsEnabled(redirectsEnabled);
			}
			get.setConfig(customReqConf.build());

			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(get);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(get);
			}
			return res;
		} finally
		{
			get.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String get(String url, String charset, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception
	{

		HttpClient client = null;
		HttpGet get = new HttpGet(url);
		String result = "";
		try
		{
			if (headers != null && !headers.isEmpty())
			{
				for (Entry<String, String> entry : headers.entrySet())
				{
					get.addHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			get.setConfig(customReqConf.build());

			HttpResponse res = null;

			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(get);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(get);
			}

			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			get.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 从 response 里获取 charset
	 * 
	 * @param ressponse
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getCharsetFromResponse(HttpResponse ressponse)
	{
		// Content-Type:text/html; charset=GBK
		if (ressponse.getEntity() != null && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null)
		{
			String contentType = ressponse.getEntity().getContentType().getValue();
			if (contentType.contains("charset="))
			{
				return contentType.substring(contentType.indexOf("charset=") + 8);
			}
		}
		return null;
	}

	/**
	 * 创建 SSL连接
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException
	{
		try
		{
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
			{
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
					return true;
				}
			}).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier()
			{

				@Override
				public boolean verify(String arg0, SSLSession arg1)
				{
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException
				{
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException
				{
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
				{
				}

			});

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} catch (GeneralSecurityException e)
		{
			throw e;
		}
	}

	// 最好不要用这个，设置的头很特殊
	@Deprecated
	public static String postJSON(String url, String json) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);

		try
		{
			// 将JSON进行UTF-8编码,以便传输中文
			// String encoderJson = URLEncoder.encode(json, charset);

			client = HttpClientUtils.client;

			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

			StringEntity se = new StringEntity(json);
			se.setContentType(CONTENT_TYPE_TEXT_JSON);
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
			post.setEntity(se);

			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			int statusCode = res.getStatusLine().getStatusCode();
			
			return IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	public static HttpResponse postJSONForFengDai(String url, String json) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);

		try
		{
			client = HttpClientUtils.client;

			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

			StringEntity se = new StringEntity(json, charset);
			se.setContentEncoding(charset);
			se.setContentType(APPLICATION_JSON);
			post.setEntity(se);

			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			int statusCode = res.getStatusLine().getStatusCode();

			System.out.println("statusCode:" + statusCode);
			logger.info("postJSONForFengDai statusCode={}, entity={}", new Object[] { statusCode, IOUtils.toString(res.getEntity().getContent(), charset) });
			return res;
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * post提交，返回code
	 * @param url
	 * @param json
	 * @param headerMap
	 * @return
	 * @throws Exception
	 */
	public static int postJsonByCode(String url, String json, Map<String, String> headerMap) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		
		try
		{
			client = HttpClientUtils.client;
			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			if (headerMap != null && !headerMap.isEmpty())
			{
				for (Entry<String, String> header : headerMap.entrySet())
				{
					post.addHeader(header.getKey(), header.getValue());
				}
			}
			StringEntity se = new StringEntity(json, Consts.UTF_8);
			se.setContentEncoding(charset);
			se.setContentType(APPLICATION_JSON);
			post.setEntity(se);
			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			return res.getStatusLine().getStatusCode();
		}catch (Exception e)
		{
			logger.info("postJsonByCode error",e);
			return -1;
		}
		finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}
	public static String postJSON(String url, String json, Map<String, String> headerMap) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);

		try
		{
			client = HttpClientUtils.client;
			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			if (headerMap != null && !headerMap.isEmpty())
			{
				for (Entry<String, String> header : headerMap.entrySet())
				{
					post.addHeader(header.getKey(), header.getValue());
				}
			}
			StringEntity se = new StringEntity(json, Consts.UTF_8);
			se.setContentEncoding(charset);
			se.setContentType(APPLICATION_JSON);
			post.setEntity(se);
			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), charset);
		}catch (Exception e)
		{
			logger.info("postJSON error",e);
			return null;
		}
		finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}
	
	public static String postJSONCode(String url, String json, Map<String, String> headerMap) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		
		try
		{
			client = HttpClientUtils.client;
			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			if (headerMap != null && !headerMap.isEmpty())
			{
				for (Entry<String, String> header : headerMap.entrySet())
				{
					post.addHeader(header.getKey(), header.getValue());
				}
			}
			StringEntity se = new StringEntity(json, Consts.UTF_8);
			se.setContentEncoding(charset);
			se.setContentType(APPLICATION_JSON);
			post.setEntity(se);
			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			int respStatus = res.getStatusLine().getStatusCode();
			return String.valueOf(respStatus);
		}catch (Exception e)
		{
			logger.info("postJSONCode error",e);
			return null;
		}
		finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	public static String postJSON(String url, String json, Map<String, String> headerMap, Integer connTimeout, Integer readTimeout) throws Exception
	{
		HttpClient client = null;
		HttpPost post = new HttpPost(url);

		try
		{
			client = HttpClientUtils.client;
			post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
			if (headerMap != null && !headerMap.isEmpty())
			{
				for (Entry<String, String> header : headerMap.entrySet())
				{
					post.addHeader(header.getKey(), header.getValue());
				}
			}
			StringEntity se = new StringEntity(json, Consts.UTF_8);
			se.setContentEncoding(charset);
			se.setContentType(APPLICATION_JSON);
			post.setEntity(se);

			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null)
			{
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null)
			{
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());

			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			post.releaseConnection();
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	public static String postMultipart(String url, String body) throws Exception
	{
		HttpPost post = null;
		HttpClient client = null;

		try
		{
			post = new HttpPost(url);
			ContentBody contentBody = new ByteArrayBody(body.getBytes("utf-8"), "");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			HttpEntity httpEntity = builder.addPart("content", contentBody).build();
			post.setEntity(httpEntity);
			HttpResponse res = null;
			if (url.startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), charset);
		} finally
		{
			if (post != null)
			{
				post.releaseConnection();
			}
			if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient)
			{
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * post上传文件
	 * 
	 * @param url
	 * @param filesInfo
	 *            字段名-文件名：文件流 的形式
	 * @param params
	 * @return
	 * @throws Exception
	 * @autohr shuzheng_wang 2018-01-19 19:00
	 */
	public static String postMultiparts(String url, Map<String, byte[]> filesInfo, Map<String, String> params) throws Exception
	{
		String respStr = null;
		HttpPost httppost = null;
		try
		{
			httppost = new HttpPost(url);
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			// add the file params
			for (Entry<String, byte[]> fileInfo : filesInfo.entrySet())
			{
				String name = fileInfo.getKey();

				// 若name没有区分字段名和文件名，则字段名和文件名都用name
				String fieldName = name;
				String fileName = name;
				// 若name区分字段名和文件名
				String[] nameInfo = name.split("-");
				if (nameInfo != null && nameInfo.length >= 2)
				{
					fieldName = nameInfo[0];
					fileName = nameInfo[1];
				}

				byte[] fileData = fileInfo.getValue();
				multipartEntityBuilder.addBinaryBody(fieldName, fileData, ContentType.MULTIPART_FORM_DATA, fileName);
			}
			// 设置上传的其他参数
			if (params != null && params.size() > 0)
			{
				Set<String> keys = params.keySet();
				for (String key : keys)
				{
					multipartEntityBuilder.addPart(key, new StringBody(params.get(key), ContentType.create("text/plain", charset)));
				}
			}

			HttpEntity reqEntity = multipartEntityBuilder.build();
			httppost.setEntity(reqEntity);

			HttpResponse response = client.execute(httppost);

			HttpEntity resEntity = response.getEntity();
			respStr = IOUtils.toString(resEntity.getContent(), charset);
			EntityUtils.consume(resEntity);

		} finally
		{
			if (httppost != null)
			{
				httppost.releaseConnection();
			}
			// if (url.startsWith("https") && client != null
			// && client instanceof CloseableHttpClient) {
			// ((CloseableHttpClient) client).close();
			// }
		}
		return respStr;
	}

	public static String EncoderByMd5(String str) throws Exception
	{
		// 确定计算方法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder base64en = new BASE64Encoder();

		// 加密后的字符串
		String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));

		System.out.println(" --------------- Base64.encode=" + base64en.encode("idCard=421122198810146254&phone=13266547119&userName=张三&accessKey=abcedfg".getBytes("utf-8")));
		return newstr;
	}

	/**
	 * 上传文件封装
	 *
	 * @param uri
	 * @param params
	 * @param file
	 * @param fileName
	 * @param filePropertyName
	 * @return
	 */
	public static String uploadFile(String uri, Map<String, String> params, byte[] file, String fileName, String filePropertyName)
	{
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entityBuilder.addBinaryBody(filePropertyName, file, ContentType.DEFAULT_BINARY, fileName);
		for (Entry<String, String> p : params.entrySet())
		{
			entityBuilder.addTextBody(p.getKey(), p.getValue(), ContentType.DEFAULT_BINARY);
		}

		RequestBuilder requestBuilder = RequestBuilder.post(uri).setEntity(entityBuilder.build());
		return doRequest(requestBuilder.build());
	}

	/**
	 * 上传文件封装
	 *
	 * @param uri
	 * @param params
	 * @param file
	 * @param filePropertyName
	 * @return
	 */
	public static String uploadFile(String uri, Map<String, String> params, File file, String filePropertyName)
	{
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		entityBuilder.addBinaryBody(filePropertyName, file, ContentType.DEFAULT_BINARY, file.getName());
		for (Entry<String, String> p : params.entrySet())
		{
			entityBuilder.addTextBody(p.getKey(), p.getValue(), ContentType.DEFAULT_BINARY);
		}

		RequestBuilder requestBuilder = RequestBuilder.post(uri).setEntity(entityBuilder.build());
		return doRequest(requestBuilder.build());
	}

	public static String uploadMultipartFile(String uri, Map<String, String> params, File file)
	{
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("file", new FileBody(file));
		for (Entry<String, String> p : params.entrySet())
		{
			entityBuilder.addTextBody(p.getKey(), p.getValue(), ContentType.TEXT_PLAIN);
		}

		RequestBuilder requestBuilder = RequestBuilder.post(uri).setEntity(builder.build());
		return doRequest(requestBuilder.build());
	}

	/**
	 * 执行http请求
	 *
	 * @param request
	 * @return
	 */
	private static String doRequest(HttpUriRequest request)
	{
		HttpClient client = null;
		try
		{
			if (request.getURI().toString().startsWith("https"))
			{
				// 执行 Https 请求.
				client = createSSLInsecureClient();
			} else
			{
				// 执行 Http 请求.
				client = HttpClientUtils.client;
			}
			return EntityUtils.toString(client.execute(request).getEntity(), "utf-8");
		} catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(10000);

        CountDownLatch countDownLatch =new CountDownLatch(3000);
        for (int i = 0; i < 3000; i++) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String pa =null;
                    String s = null;
                    try {
                        s = HttpClientUtils.postParameters("http://112.74.39.100:9090/",pa);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(s);
                    countDownLatch.countDown();
                }
            });

        }

        countDownLatch.await();
        System.out.println("finish");



    }

}