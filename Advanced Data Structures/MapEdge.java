package roadgraph;

import geography.GeographicPoint;

public class MapEdge {

	private MapNode from;
	private MapNode to;
	private String roadName;
	private String roadType;
	private double length;
	
	
	public MapEdge(MapNode from, MapNode to, String roadName, String roadType, double length ) {
		// TODO Auto-generated constructor stub
		this.from = from;
		this.to = to;
		this.roadName = roadName;
		this.roadType = roadType;
		this.length = length;
	}
	
	public MapNode getFrom() {
		return this.from;
	}

	public MapNode getTo() {
		return this.to;
	}
	
	public double getLength() {
		return this.length;
	}
}
