package com.yzx.lifeassistants.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @Description: HttpClient工具类
 * @author: yzx
 * @date: 2015-11-23
 */
public class HttpClientUtil {

	/**
	 * 
	 * @Description: 发送请求，参数：网络地址、存储请求数据的集合（集合为空是GET请求，不为空为POST请求）、回调接口
	 */
	public static void sendRequest(final String addressRequest,
			List<NameValuePair> nameValueList, IHttpCallBack iHttpCallBack) {
		final String address = addressRequest;
		final List<NameValuePair> list = nameValueList;
		final IHttpCallBack listener = iHttpCallBack;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// StringBuilder response = new StringBuilder();
				HttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(
						CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 8000);
				HttpResponse res = null;
				try {
					if (list == null) {
						HttpGet get = new HttpGet(address);
						res = httpClient.execute(get);
					} else {
						HttpPost post = new HttpPost(address);
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
								list, "UTF-8");
						post.setEntity(entity);
						res = httpClient.execute(post);
					}
					// BufferedReader read = new BufferedReader(
					// new InputStreamReader(res.getEntity().getContent()));
					// String line = read.readLine();
					// while ((line = read.readLine()) != null) {
					// response.append(line);
					// }
					String response = EntityUtils.toString(res.getEntity(),
							"UTF-8");
					if (listener != null) {
						// 回调onFinish()方法
						// listener.onFinish(response.toString());
						listener.onFinish(response);
					}
				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				}
			}
		}).start();
	}

}
