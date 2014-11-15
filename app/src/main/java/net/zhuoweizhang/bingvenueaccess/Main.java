package net.zhuoweizhang.bingvenueaccess;

import net.zhuoweizhang.bingvenueaccess.model.*;

public class Main {
	public static void main(String[] args) throws Exception {
		BingMapsClient client = new BingMapsClient("AiGyFKAL6AMsIccErosDUwQAGJKEzi-_pn8TZDGuXJqYRCW4mk5GKAG3itCF9AWF", "Bing venue access");
		NearbyVenue[] venues = client.getNearbyVenues(40.3505768, -74.6522335, 1000);
		System.out.println(venues[0].metadata.MapId);
		VenueMap venue = client.getVenue(venues[0].metadata.MapId);
		System.out.println();
	}
}