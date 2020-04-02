/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	//TODO: Add your member variables here in WEEK 3
	private Map<GeographicPoint, MapNode> mapGrid;
	private int numVertices;
	private int numEdges;
	private Set<MapEdge> mapEdges; //righ? or list?
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		// TODO: Implement in this constructor in WEEK 3
		mapGrid = new HashMap<GeographicPoint, MapNode>();
		mapEdges = new HashSet<MapEdge>();
		numVertices = 0;
		numEdges = 0;
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		//TODO: Implement this method in WEEK 3
		return numVertices;
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		//TODO: Implement this method in WEEK 3
		return mapGrid.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		//TODO: Implement this method in WEEK 3
		return numEdges;
	}
	
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		// TODO: Implement this method in WEEK 3
		if (location == null || mapGrid.containsKey(location)) {
			return false;
		}
		MapNode node = new MapNode(location);
		mapGrid.put(location, node);
		numVertices++;
		return true;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {

		//TODO: Implement this method in WEEK 3
		if (from == null || to == null || roadName == null || roadType == null) {
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		if (!mapGrid.containsKey(to) || !mapGrid.containsKey(from)) {
			throw new IllegalArgumentException("one of these is not a valid vertex");
		}
		if (length < 0) {
			throw new IllegalArgumentException("Edge length must be equal or greater than 0");
		}
		MapEdge edge = new MapEdge(mapGrid.get(from), mapGrid.get(to), roadName, roadType, length);
		mapGrid.get(from).addNeighbor(mapGrid.get(to));	
		mapEdges.add(edge);
		numEdges++;		
	}
	

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting locatio
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 3
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());		

		if (start == null || goal == null) {
			return null;
		}

		MapNode startNode = mapGrid.get(start);
		MapNode endNode = mapGrid.get(goal);
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		boolean found = bfsSearch(startNode, endNode, parentMap, nodeSearched);

		if (!found) {
			return null;
		}

		// reconstruct the path
		return constructPath(startNode, endNode, parentMap);
	}

	private boolean bfsSearch(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {
		//change stack to queue
		HashSet<MapNode> visited = new HashSet<MapNode>();
		//Stack<MapNode> toExplore = new Stack<MapNode>();
		Queue<MapNode> toExplore = new LinkedList<MapNode>();
		//toExplore.push(start);
		toExplore.add(start);
		boolean found = false;
		while (!toExplore.isEmpty()) {
			//MapNode curr = toExplore.pop();
			MapNode curr = toExplore.poll();
			if (curr == goal) {
				found = true;
				break;
			}					
			List<MapNode> neighbors = curr.getNeighbors();
			ListIterator<MapNode> it = neighbors.listIterator(neighbors.size());						
			while (it.hasPrevious()) {
				MapNode next = it.previous();
				if (!visited.contains(next)) {
					visited.add(next);
					parentMap.put(next, curr);
					//toExplore.push(next);
					toExplore.add(next);
				}
			}		
		}
		return found;
	}
	
	private List<GeographicPoint> constructPath(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap) {
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		MapNode curr = goal;
		while (curr != start) {
			path.addFirst(curr.getLocation());
			curr = parentMap.get(curr);
		}
		path.addFirst(start.getLocation());
		return path;
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 4

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		if (start == null || goal == null) {
			return null;
		}

		MapNode startNode = mapGrid.get(start);
		MapNode endNode = mapGrid.get(goal);
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		boolean found = dikstraSearch(startNode, endNode, parentMap, nodeSearched);

		if (!found) {
			return null;
		}

		// reconstruct the path
		return constructPath(startNode, endNode, parentMap);
	}

	class MapNodeComparator implements Comparator<MapNode> {
		@Override
		public int compare(MapNode a, MapNode b) {
			if (a.getTempSearchDistance() < b.getTempSearchDistance()) {
				return -1;
			}
			if (a.getTempSearchDistance() > b.getTempSearchDistance()) {
				return 1;
			}
			return 0;
		}
	}
	
	private boolean dikstraSearch(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {		
		HashSet<MapNode> visited = new HashSet<MapNode>();
		Comparator<MapNode> comparator = new MapNodeComparator();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>(comparator);		
		//PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();	
		Double searchDistance = 0.0;
		
		//set temp distances to infinity b y iterating through mapGrid
		for (GeographicPoint gp : mapGrid.keySet()) {
			mapGrid.get(gp).setTempSearchDistance(Double.POSITIVE_INFINITY);
		}
		
		start.setTempSearchDistance(0.0);
		toExplore.add(start);
		boolean found = false;
		while (!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			searchDistance += curr.getTempSearchDistance();
			if (!visited.contains(curr)){
				visited.add(curr);
				if (curr == goal) {
					found = true;
					break;
				}
				List<MapNode> neighbors = curr.getNeighbors();
				ListIterator<MapNode> it = neighbors.listIterator(neighbors.size());
				while (it.hasPrevious()) {
					MapNode next = it.previous();
					if (!visited.contains(next)) {
						double edgeLength = getEdgeLength(curr, next);
						//System.out.println(curr.getLocation()+"\t"+next.getLocation()+"\t"+edgeLength);
						if ((curr.getTempSearchDistance() + edgeLength) < next.getTempSearchDistance()) {							
							next.setTempSearchDistance(curr.getTempSearchDistance() + edgeLength);
							//System.out.println("next.settemp :"+next.getTempSearchDistance());
							parentMap.put(next,curr);
							toExplore.add(next);							
						}												
					}
				}
			}
		}
		return found;
	}	
	
	private double getEdgeLength(MapNode from, MapNode to) {
		for (MapEdge edge : mapEdges) {
			if (edge.getTo() == to && edge.getFrom() == from) {
				//System.out.println(edge.getLength());
				return edge.getLength();
			}
		}
		return 0.0;
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal, Consumer<GeographicPoint> nodeSearched){
		// TODO: Implement this method in WEEK 4

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		if (start == null || goal == null) {
			return null;
		}

		MapNode startNode = mapGrid.get(start);
		MapNode endNode = mapGrid.get(goal);
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();

		boolean found = aStarSearchSearch(startNode, endNode, parentMap, nodeSearched);

		if (!found) {
			return null;
		}

		// reconstruct the path
		return constructPath(startNode, endNode, parentMap);
	}
	
	private boolean aStarSearchSearch(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {		
		HashSet<MapNode> visited = new HashSet<MapNode>();
		Comparator<MapNode> comparator = new MapNodeComparator();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>(comparator);		
		//PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();	
		Double searchDistance = 0.0;
		
		//set temp distances to infinity b y iterating through mapGrid
		for (GeographicPoint gp : mapGrid.keySet()) {
			mapGrid.get(gp).setTempSearchDistance(Double.POSITIVE_INFINITY);
		}
		
		start.setTempSearchDistance(0.0);
		toExplore.add(start);
		boolean found = false;
		while (!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			searchDistance += curr.getTempSearchDistance();
			if (!visited.contains(curr)){
				visited.add(curr);
				if (curr == goal) {
					found = true;
					break;
				}
				List<MapNode> neighbors = curr.getNeighbors();
				ListIterator<MapNode> it = neighbors.listIterator(neighbors.size());
				while (it.hasPrevious()) {
					MapNode next = it.previous();
					if (!visited.contains(next)) {
						double edgeLength = getEdgeLength(curr, next);
						//System.out.println(curr.getLocation()+"\t"+next.getLocation()+"\t"+edgeLength);
						if ((curr.getTempSearchDistance() + edgeLength) < next.getTempSearchDistance()) {							
							next.setTempSearchDistance(curr.getTempSearchDistance() + edgeLength);
							//System.out.println("next.settemp :"+next.getTempSearchDistance());
							parentMap.put(next,curr);
							toExplore.add(next);							
						}												
					}
				}
			}
		}
		return found;
	}		
	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		System.out.println("DONE.");
		
		// You can use this method for testing.  
		
		
		/* Here are some test cases you should try before you attempt 
		 * the Week 3 End of Week Quiz, EVEN IF you score 100% on the 
		 * programming assignment.
		 */
		
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		for (int i = 0; i < testroute.size(); i++) {
			System.out.println(testroute.get(i));
		}
		
		/*
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		*/
		
		// A very simple test using real data
		/*
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		*/
		
		
		/* Use this code in Week 3 End of Week Quiz */
		/*MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		*/
		
	}
	
}
