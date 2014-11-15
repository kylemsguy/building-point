package net.zhuoweizhang.bingvenueaccess;

import java.util.*;

import net.zhuoweizhang.bingvenueaccess.model.*;

import com.javadocmd.simplelatlng.*;
import com.javadocmd.simplelatlng.util.*;

public class VenueUtils {

	public static List<Entity> rayCast(VenueMap map, double lat, double lon, double yawInDegrees, double radiusInKm, double stepInKm) {
		LatLng loc = new LatLng(lat, lon);
		Floor floor = getFloor(map);
		List<Entity> allNamedEntities = filterNamedEntities(floor);
		List<Entity> theEntities = new ArrayList<Entity>();
		LatLng tmp = new LatLng(0, 0);
		for (double dist = 0; dist < radiusInKm; dist += stepInKm) {
			LatLng newloc = LatLngTool.travel(loc, yawInDegrees, dist, LengthUnit.KILOMETER);
			for (int i = 0; i < allNamedEntities.size(); i++) {
				Entity e = allNamedEntities.get(i);
				// assumption that coordinates are close enough to cartesian
				if (pointInPolygon(e.geometry[0].latitudes, e.geometry[0].longitudes, newloc.getLatitude(), newloc.getLongitude())) {
					if (!theEntities.contains(e)) theEntities.add(e);
					break;
				}
			}
		}
		return theEntities;
	}

	public static Floor getFloor(VenueMap map) {
		for (Floor f: map.floors) {
			if (f.entities.length != 0 && f.type != null) return f;
		}
		return null;
	}

	public static List<Entity> filterNamedEntities(Floor floor) {
		List<Entity> out = new ArrayList<Entity>();
		for (Entity e: floor.entities) {
			if (e.name != null) {
				out.add(e);
			}
		}
		return out;
	}

	/* http://alienryderflex.com/polygon/ */
	public static boolean pointInPolygon(double[] polyX, double[] polyY, double x, double y) {
		int j = polyX.length - 1;
		boolean oddNodes = false;
		for (int i = 0; i < polyX.length; i++) {
			if ((polyY[i]< y && polyY[j]>=y
			||   polyY[j]< y && polyY[i]>=y)
			&&  (polyX[i]<=x || polyX[j]<=x)) {
				if (polyX[i]+(y-polyY[i])/(polyY[j]-polyY[i])*(polyX[j]-polyX[i])<x) {
					oddNodes = !oddNodes;
				}
			}
			j = i;
		}
		return oddNodes;
	}
}