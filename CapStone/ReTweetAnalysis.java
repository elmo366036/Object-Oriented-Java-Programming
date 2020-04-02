package graph;



import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * @author Cooper Chastain.
 * 
 *
 */
public class ReTweetAnalysis {
	// this stores the retweets as an AdjList in the form {fromNode: [toNodes]}
	// it uses a hashMap to map the neighbors to a node
	// it uses a hashSet to list out the neighbors. This means that neighbors cannot be repeated 
	private static Map<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>(); 
	
	// this stores the retweets as a transposed AdjList {toNode: [fromNodes]}
	private static Map<Integer, HashSet<Integer>> graphTranspose = new HashMap<Integer, HashSet<Integer>>();
	
	// tracks number of edges
	static int edgeCount = 0; 
	
	// these are used to store interesting information about the tweeters, who started it, who retweeted, who ended it
	private static Set<Integer> sources = new HashSet<Integer>(); 			// sources are nodes that initiated the retweet
	private static Set<Integer> retransmitters = new HashSet<Integer>(); 	// nodes that retweeted the message (other than sources)
	private static Set<Integer> sinks = new HashSet<Integer>();				// sinks are nodes that received the retweet but we dontt
	
	// these are used to determine if there are any duplicated retweets. 
	private static boolean repeatedEdges;
	private static int repeatedEdgeCount = 0;
	
	// flag for self-loops
	private static boolean selfLoops = false;
	
	// this will track the numbers of simultaneous retweets transmitted by retweeters 
	//   of the form <# of simultaneousRetweets, <list of who is retweeting this amount>>  
	private static Map<Integer, HashSet<Integer>> simultaneousRetweets = new HashMap<Integer, HashSet<Integer>>();
	private static int maxRetweetSize = 0;
	
	// this will track the numbers of simultaneous retweets received by tweeters 
	//   of the form <# of simultaneousRetweetsReceived, <list of who received this amount of retweets>>  
	private static Map<Integer, HashSet<Integer>> simultaneousRetweetsReceived = new HashMap<Integer, HashSet<Integer>>();
	private static int maxRetweetSizeReceived = 0;
	
	// this counts the number of edges (tweets) from sources
	
	private static int sourceEdgeCount = 0;
	
	// this tracks the reach from each source node (i.e. it is used when finding (weakly) connected components
	// of a source node)
	private static Map<Integer, HashSet<Integer>> maxReachNodes = new HashMap<Integer, HashSet<Integer>>();
	private static int maxReach = 0;
	private static boolean cyclePresent = false;

	// this tracks the minimum & maximum distance from a sourceNode to a sinkNode
	private static int minDistance = Integer.MAX_VALUE;
	private static int maxDistance = 0;
	
	// from CapGraph
	public void addVertex(int num) { // O(v)
		graph.put(num, new HashSet<Integer>()); 			//initialize graph from CapGraph
		graphTranspose.put(num, new HashSet<Integer>());	//initialize transposed graph from CapGraph		
		sources.add(num); 									//initialize sources
		sinks.add(num);										//initialize sinks
		return;	
	}
	
	// from CapGraph
	public void addEdge(int to, int from) { //changed from (from, to) O(e)
		if (graph.containsKey(from) && graph.containsKey(to)) {
			// check to see if the edge is already recorded
			// if so, indicate that there are repeated edges
			// for the Higgs data, there are no repeated edges
			if (graph.get(from).contains(to)) { 
				repeatedEdges = true;
				repeatedEdgeCount++;
			}			
			graph.get(from).add(to);
			edgeCount++; // only counting edges graph. Transpose should be the same
		}		
		
		// do the same for graphTranpose
		if (graphTranspose.containsKey(from) && graphTranspose.containsKey(to)) {
			graphTranspose.get(to).add(from);
		}
		
		sources.remove(to);		//removes from sources any node that has an incoming edge
		sinks.remove(from);		//removes from sinks any node that has an outgoing edge	
		retransmitters.add(to); //adds to nodes to the list of retransmitters
	}
	
	private static void loopThroughGraph() { // O(2v) (upper bound. there won't be v nodes in the AdjLists)
											 //	the real upper bound is O(2*retransmitters)
		
		// loop through graph and evaluate outgoing edges
		for (int fromNode : graph.keySet()) {
			findMaxRetweets(fromNode);		// determine max number of retweets from one or more retweeters	
			findSourceTweets(fromNode);		// determines the number of retweets from a source and adds to the total
											//   number of retweets from sources
			findSelfLoops(fromNode);		// checks for self-loops
		}
		
		//loop through graphTranspose and evaluate incoming edges
		for (int toNode : graphTranspose.keySet()) {
			findMaxRetweetsReceived(toNode);//determine max number of retweets received by one or more retweeters
		}	
		
		// post loop processing	
		// I thought I was going to do something here but it turns out I didn't need to do any post processing
	}

	//this determines the number of retweets from a source and adds to a running total
	private static void findSourceTweets(int fromNode) {
		if (sources.contains(fromNode)) {
			sourceEdgeCount += graph.get(fromNode).size();
		}
	}

	// this determines the number of retweets (out-degree) for fromNode and maintains a max count
	private static void findMaxRetweets(int fromNode) {
		int fromNodeNeighborsSize = graph.get(fromNode).size();
		if (fromNodeNeighborsSize >= maxRetweetSize) {
			maxRetweetSize = fromNodeNeighborsSize;
			if (!simultaneousRetweets.containsKey(maxRetweetSize)) {
				HashSet<Integer> tempSet = new HashSet<Integer>();
				tempSet.add(fromNode);
				simultaneousRetweets.put(maxRetweetSize, tempSet);
			}
			else {
				simultaneousRetweets.get(maxRetweetSize).add(fromNode);
			}
		}
	}
	
	// this checks adjList of fromNode for fromNode to see if there is a self-loop
	private static void findSelfLoops(int fromNode) {
		if (graph.get(fromNode).contains(fromNode)) {
			selfLoops = true;
		}
	}
	
	// this determines the number of retweets (in-degree) to toNode and maintains a max count
	private static void findMaxRetweetsReceived(int toNode) {
		int toNodeNeighborsSize = graphTranspose.get(toNode).size();
		if (toNodeNeighborsSize >= maxRetweetSizeReceived) {
			maxRetweetSizeReceived = toNodeNeighborsSize;
			if (!simultaneousRetweetsReceived.containsKey(maxRetweetSizeReceived)) {
				HashSet<Integer> tempSet = new HashSet<Integer>();
				tempSet.add(toNode);
				simultaneousRetweetsReceived.put(maxRetweetSizeReceived, tempSet);
			}
			else {
				simultaneousRetweetsReceived.get(maxRetweetSizeReceived).add(toNode);
			}
		}
	}
	
	private static void loopThroughSources() { // O(v*(...)) as upper bound
											   // actually it will be O(v(sources)*(whatever is in the loop))
		
		// I was thinking about performing DFS on each source node and then running BFS on each one.
		// But it takes so long to run DFS I decided it is not worth the time to run DFS and then BFS. 
		//int count = 0;
		for (int sourceNode : sources) {
			//count++;
			//System.out.println("["+count+"] \t "+sourceNode);
			DFS(sourceNode); 	// this determines how many retweeters are reached by sourceNode and will check for cycles. 
								// it is taking ~40s to run DFS for all sources in higgs and I wonder if it is worth 
								//   it to then run BFS to get other information
								// this is a brute force approach
			//BFS(sourceNode);
		}
	}

	private static void DFS(int sourceNode) { // O(e + v) as upper bound
		// this runs a modified form of DFS on sourceNode to identify how many retweeters are reached from it, 
		//   if there is a cycle, and the min and max distances from the source to a sink
		// if there is a cycle, then there is a SCC. 
		// This is iterative DFS using Stack stack. I HATE recursion.
		// I am using HashSet pathList to track the path from source. This can be used to determine
		//   if there is a cycle or not and to determine the path length (distance).  It is much faster to 
		//   do a .contains() on a HashSet than a Stack. (O(1) compared with O(size of stack))

		// this implements DFS with cycle detection & Max/Min distance tracking
		Stack<Integer> stack = new Stack<Integer>();		//use this for DFS traversal
		HashSet<Integer> pathList = new HashSet<Integer>();	//use this to track visited nodes for cycle detection
		HashSet<Integer> visited = new HashSet<Integer>();	//use this for DFS traversal
		stack.add(sourceNode);
		pathList.add(sourceNode);
		visited.add(sourceNode); 
		while (!stack.isEmpty()) {	
			int currNode = stack.pop();
			pathList.remove(currNode);
			for (int neighbor : graph.get(currNode)) {
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					stack.push(neighbor);
					pathList.add(neighbor);
					//if the neighbor is a sink, the size of pathList tell us the distance from source
					if (sinks.contains(neighbor)) {
						if (pathList.size() > maxDistance) {
							maxDistance = pathList.size();
						}
						if (pathList.size() < minDistance) {
							minDistance = pathList.size();
						}
					}
				}
				else {
					if (pathList.contains(neighbor)) {
						cyclePresent = true;
					}
				}
			}			
		}
	
		// I was going to maintain a HashMap of the relationships between all the sources and the nodes they can reach,
		//   but I think the size of the data is too large and probably would need to be put into 
		//   a database or stored as a file. 
		// reach.put(sourceNode, visited); // unfortunately, this is huge and causes the program to crash
		
		//this tracks the number of nodes reached by sourceNode and compares with a maximum
		
		int reachCount = visited.size() - 1; //remove the source from the count
		if (reachCount > maxReach) {
			HashSet<Integer> sourceNodesMaxReach = new HashSet<Integer>();
			sourceNodesMaxReach.add(sourceNode);
			maxReachNodes.put(reachCount, sourceNodesMaxReach);
			maxReach = reachCount;
		}
		else if (reachCount == maxReach) {
			HashSet<Integer> sourceNodesMaxReach = maxReachNodes.get(maxReach);
			sourceNodesMaxReach.add(sourceNode);
			maxReachNodes.put(maxReach, sourceNodesMaxReach);
		}	
	}

	//from GraphLoader
    public static void loadGraph(ReTweetAnalysis rta, String filename) {
        Set<Integer> seen = new HashSet<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } 
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding NEW
        // vertices as they are found and connecting them with edges.
        while (sc.hasNextInt()) {
            int v1 = sc.nextInt();
            int v2 = sc.nextInt();
            if (!seen.contains(v1)) {
                rta.addVertex(v1);
                seen.add(v1);
            }
            if (!seen.contains(v2)) {
                rta.addVertex(v2);
                seen.add(v2);
            }
            rta.addEdge(v1, v2);
        }     
        sc.close();
    }
	
    
	public static void main (String[] args) {
		
		ReTweetAnalysis rta = new ReTweetAnalysis();
		
		// begin phase 1: obtaining information during the creation of the graph
		System.out.println("+--- BEG: Data Processed During Creation of AdjList ---------------------------+");
		long startTime = System.nanoTime();
		loadGraph(rta, "data/twitter_higgs.txt");
		//loadGraph(rta, "data/twitter_combined.txt");
		//loadGraph(rta, "data/small_test_graph.txt"); //
		//loadGraph(rta, "data/smallTest.txt"); //				
		long endTime = System.nanoTime();
		double estTime = (endTime - startTime)/(1000000000.0);			
		System.out.println();
		System.out.println("The number of nodes (tweeters & retweeters) is \t \t \t \t"+graph.size());
		System.out.println("The number of source nodes (indegree = 0) is \t \t \t \t"+sources.size());
		System.out.println("The number of sink nodes (outdegree = 0) is \t \t \t \t"+sinks.size());	
		System.out.println("The number of retweeters (total nodes - sources) is \t \t \t"+retransmitters.size());
		System.out.println("The number of edges (retweets) is \t \t \t \t \t"+edgeCount);		
		System.out.print("Are there any repeated edges (tweets) in the dataset? \t \t \t");
		if (!repeatedEdges) {
			System.out.println("NO");
		}
		else {
			System.out.println("YES \t Consider a different data structure for the edges");
			System.out.println("\t There are "+repeatedEdgeCount+" repeated edges (tweets). \t");
		}			
		System.out.print("Are there any self-looped edges (tweets) in the dataset? \t \t");
		if (!selfLoops) {
			System.out.println("NO");
		}
		else {
			System.out.println("YES \t Consider a different data structure for the edges");
		}
		System.out.println();
		System.out.println("Estimated number of computations: \t \t \t \t \tO(v + e) plus file overhead");
		System.out.println("Estimated time (seconds) to completion: \t \t \t \t" +estTime);
		System.out.println();
		System.out.println("+--- END: Data Processed From Creation of AdjList -----------------------------+");			
		System.out.println();
		System.out.println();

		// begin phase 2: obtain information through a single pass through the adjacency list
		System.out.println("+--- BEG: Data Processed From Looping Through AdjList & AdjList Transposed ----+");	
		startTime = System.nanoTime();
		loopThroughGraph();		
		endTime = System.nanoTime();
		estTime = (endTime - startTime)/(1000000000.0);		
		System.out.println();
		System.out.println("The largest number of retweets from a node is \t \t \t \t"+maxRetweetSize);
		HashSet<Integer> retweetList = (HashSet<Integer>) simultaneousRetweets.get(maxRetweetSize);
		System.out.print("   from: \t \t \t \t \t \t \t \t");
		for (Integer i : retweetList) {
			System.out.print("["+i+"]\t");
			if (sources.contains(i)) {
				System.out.println("SOURCE");
			}
			else {
				System.out.println("Not a SOURCE");
			}
		}	
		System.out.println("The largest number of retweets received by one or more retweeter(s) is \t"+maxRetweetSizeReceived);
		HashSet<Integer> retweetListReceived = (HashSet<Integer>) simultaneousRetweetsReceived.get(maxRetweetSizeReceived);
		System.out.print("   to following tweeter(s): \t \t \t \t \t \t");
		for (Integer i : retweetListReceived) {
			System.out.print("["+i+"]\t");
			if (sinks.contains(i)) {
				System.out.println("SINK");
			}
			else {
				System.out.println("Not a SINK");
			}
		}	
		System.out.println("The total number of retweets from sources is \t \t \t \t"+sourceEdgeCount);
		System.out.println("The total number of retweets from retweeters is \t \t \t"+(edgeCount - sourceEdgeCount));
		System.out.println();
		System.out.println("Estimated number of computations: \t \t \t \t \tO(2 * v(retransmitters))");
		System.out.println("Estimated time (seconds) to completion: \t \t \t \t" +estTime);
		System.out.println();		
		System.out.println("+--- END: Data Processed From Looping Through AdjList & AdjList Transposed  ---+");		
		System.out.println();
		System.out.println();
		
		// begin phase 3: if there are sources, obtain information from running DFS on each of them 
		if (sources.size() > 0) {
			System.out.println("+--- BEG: Data Processed From Looping Through Source Nodes --------------------+");	
			startTime = System.nanoTime();
			loopThroughSources();
			endTime = System.nanoTime();
			estTime = (endTime - startTime)/1000000000.0;			
			System.out.println();
			System.out.println("The maximum reach from any source node is: \t \t \t \t"+maxReach);
			System.out.print("   from the following source nodes: \t \t \t \t \t");
			HashSet<Integer> maxSourceReach = (HashSet<Integer>) maxReachNodes.get(maxReach);
			for (Integer i : maxSourceReach) {
				System.out.print("["+i+"]\t");
			}
			System.out.println();
			System.out.print("The average number of retweeters reached from a source is \t \t");
			int count = 0;
			int total = 0;
			for (Integer i : maxReachNodes.keySet()) {
				count++;
				total += i;
			}
			System.out.println(total/count);
			System.out.println("The minimum distance from a source to a sink is \t \t \t"+minDistance);
			System.out.println("The maximum distance from a source to a sink is \t \t \t"+maxDistance);
			System.out.print("Are there any cycles in the dataset? \t \t \t \t \t");
			if (!cyclePresent) {
				System.out.println("NO");
			}
			else {
				System.out.println("YES");
			}
			System.out.println();
			System.out.println("Estimated number of computations: \t \t \t \t \tO(v(sources) * (e + v))");
			System.out.println("Estimated time (seconds) to completion: \t \t \t \t" +estTime);
			System.out.println();
			System.out.println("+--- END: Data Processed From Looping Through Source Nodes --------------------+");			
		}
	}	
}