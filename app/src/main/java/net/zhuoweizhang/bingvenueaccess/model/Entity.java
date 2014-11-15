package net.zhuoweizhang.bingvenueaccess.model;

import java.util.Map;

public class Entity {
	public String type;
	public double[] location;
	public Geometry[] geometry;
	public Map<String, String> name;
	public String toString() {
		if (name == null) return super.toString();
		return super.toString() + name;
	}
}