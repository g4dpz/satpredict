package com.badgersoft.satpredict.client.impl;

import com.badgersoft.satpredict.client.SatPredictClient;
import com.badgersoft.satpredict.client.dto.SatPosDTO;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class SatPredictClientImpl implements SatPredictClient {

	private String rootUrl;
	private CloseableHttpClient httpClient;

	public SatPredictClientImpl(String rootUrl) {
        if (StringUtils.isBlank(rootUrl)) {
			throw new IllegalArgumentException("root URL must be provided");
		}

        this.rootUrl = rootUrl;

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		httpClient = HttpClients.custom().setConnectionManager(cm).build();

	}

	@Override
	public SatPosDTO getPosition(long catnum, double latitude, double longitude, double altitude) {
		String json = performAPICall("/satellite/position/" + catnum + "?latitude=" + latitude + "&longitude=" + longitude + "&altitude=" + altitude);
		return new Gson().fromJson(json, SatPosDTO.class);
	}

	private String performAPICall(String apiURL) {
		try {
			String url = rootUrl + apiURL;
			HttpGet request = new HttpGet(url);
			return httpClient.execute(request, responseHandler());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ResponseHandler<String> responseHandler() {
		return new ResponseHandler<String>() {
			@Override
			public String handleResponse(final HttpResponse response) throws IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new IOException("Unexpected response status: " + status);
				}
			}
		};
	}

	@Override
	protected void finalize() throws Throwable {
		httpClient.close();
		super.finalize();
	}
}
