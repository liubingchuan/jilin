package com.xitu.app.test.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;


public class Request {

	private HttpClient client;

	public Request(String charset) {
		client = new DefaultHttpClient();
		if (charset != null) {
			client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charset);
		}
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
		client.getParams().setParameter("http.protocol.single-cookie-header", true);
		
	}

	/**
	 * 处理可能出现的压缩格式
	 * 
	 * @param method
	 * @return byte[]
	 */
	public synchronized byte[] getResponseBody(HttpResponse response) {
		try {
			Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");
			HttpEntity entity = response.getEntity();
			if (contentEncodingHeader != null) {
				String contentEncoding = contentEncodingHeader.getValue();
				if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
					GZIPInputStream gzipInput = null;
					try {
						gzipInput = new GZIPInputStream(entity.getContent());
					} catch (EOFException e) {
						
					}
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[256];
					int n;
					while ((n = gzipInput.read(buffer)) >= 0) {
						out.write(buffer, 0, n);
					}
					return out.toByteArray();
				}
			}
			return EntityUtils.toByteArray(entity);
		} catch (Exception e) {
			
		}

		return null;
	}
	
	/**
	 * 发出post请求
	 * 
	 * @param url
	 * @param form_data
	 * @param charset
	 * @param headerMap
	 * @return
	 */
	public synchronized String postRequest(String url,String query, String charset) {
		HttpPost postMethod;
		
		try {
			postMethod = new HttpPost(url);
            StringEntity input = new StringEntity(query, charset);
            input.setContentType("application/json");
            postMethod.setEntity(input);

			HttpResponse httpResponse = client.execute(postMethod);
			int requestStatus = httpResponse.getStatusLine().getStatusCode();
			if (requestStatus == HttpStatus.SC_OK) {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, charset);
				return html;
			} else {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, charset);
				
				
				//System.out.println(requestStatus + "\t" + url);
				//System.out.println(html);
				
				return html;
			}
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally {
			try {
				//postMethod.abort();
			} catch (Exception e) {
			}
		}

		return null;
	}
	
	/**
	 * 发出post请求
	 * 
	 * @param url
	 * @param form_data
	 * @param charset
	 * @param headerMap
	 * @return
	 */
//	public synchronized String postDeleteRequest(String url,String query, String charset) {
//		
//		HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
//	        
//		try {
//			
//            StringEntity input = new StringEntity(query, charset);
//            input.setContentType("application/json");
//            httpDeleteWithBody.setEntity(input);
//
//			HttpResponse httpResponse = client.execute(httpDeleteWithBody);
//			int requestStatus = httpResponse.getStatusLine().getStatusCode();
//			if (requestStatus == HttpStatus.SC_OK) {
//				byte[] temp = getResponseBody(httpResponse);
//				String html = new String(temp, charset);
//				return html;
//			} else {
//				byte[] temp = getResponseBody(httpResponse);
//				String html = new String(temp, charset);
//				
//				logger.info(requestStatus + "\t" + url);
//				logger.error(html);
//				
//				System.out.println(requestStatus + "\t" + url);
//				System.out.println(html);
//				
//				return html;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("error",e);
//		} finally {
//			try {
//				httpDeleteWithBody.abort();
//			} catch (Exception e) {
//			}
//		}
//
//		return null;
//	}
	
	
}
