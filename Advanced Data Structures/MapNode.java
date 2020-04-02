package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import geography.GeographicPoint;

public class MapNode {
	private GeographicPoint location;
	private List<MapNode> neighbors;
	private Double tempSearchDistance; 

	public MapNode(GeographicPoint location) {
		// TODO Auto-generated constructor stub
		this.location = location;
		this.neighbors = new LinkedList<MapNode>();
		this.tempSearchDistance = Double.POSITIVE_INFINITY; 
	}

	public void setTempSearchDistance(double searchDistance) {
		this.tempSearchDistance = searchDistance;
		return;
	}
	
	public List<MapNode> getNeighbors() {
		// TODO Auto-generated method stub
		return neighbors;
	}
	
	public void addNeighbor(MapNode to) {
		neighbors.add(to);
		return;
	}
	
	public GeographicPoint getLocation() {
		return this.location;
	}

	public Double getTempSearchDistance() {
		return this.tempSearchDistance;
	}
	
}
