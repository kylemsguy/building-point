package net.zhuoweizhang.bingvenueaccess;

import java.nio.charset.Charset;
import java.util.*;
import java.net.*;
import java.io.*;

import com.google.gson.Gson;

import net.zhuoweizhang.bingvenueaccess.model.*;

public class BingMapsClient {

	private static Charset utf8 = Charset.forName("UTF-8");

	private static final String API_URL = "http://dev.virtualearth.net/REST/v1";

	private Gson gson = new Gson();
	private String userAgent, apiKey;

	public BingMapsClient(String apiKey, String userAgent) {
		this.apiKey = apiKey;
		this.userAgent = userAgent;
	}

	private <T> T makeRequest(String endpoint, Class<T> responseClass) throws IOException {
		InputStream is = null;
		HttpURLConnection conn;
		int statusCode = -1;
		byte[] outRes = null;
		URL url = null;

		try {
			url = new URL(API_URL + "/" + endpoint + "?jsonso=r3&output=json&key=" + apiKey);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", userAgent);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.connect();
			statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}
			outRes = bytesFromInputStream(is, conn.getContentLength() > 0 ? conn.getContentLength() : 1024);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}

		String outString = new String(outRes, utf8);

		if (statusCode != 200) {
			throw new RuntimeException("Status: " + statusCode + ":" + outString);
		} else {
			T outResult = gson.fromJson(outString, responseClass);
			return outResult;
		}
	}

	public NearbyVenue[] getNearbyVenues(double lat, double lon, double radius) throws IOException {
		return makeRequest("VenueMaps/PointRadius/" + lat + "," + lon + "/" + radius, NearbyVenue[].class);
	}

	public VenueMap getVenue(String name) throws IOException {
		return makeRequest("JsonFilter/VenueMaps/data/" + name, VenueMap.class);
	}


	private static byte[] bytesFromInputStream(InputStream in, int startingLength) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(startingLength);
		try {
			byte[] buffer = new byte[1024];
			int count;
			while ((count = in.read(buffer)) != -1) {
				bytes.write(buffer, 0, count);
			}
			return bytes.toByteArray();
		} finally {
			bytes.close();
		}
	}
}