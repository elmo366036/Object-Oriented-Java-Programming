/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import util.GraphLoader;

/**
 * @author Your name here.
 * 
 * For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.
 *
 */
public class CapGraph implements Graph {

	private Map<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>(); //maps from to to nodes
	private Map<Integer, HashSet<Integer>> graphTranspose = new HashMap<Integer, HashSet<Integer>>(); //transpose
	
	@Override
	public void addVertex(int num) {
		graph.put(num, new HashSet<Integer>());
		graphTranspose.put(num, new HashSet<Integer>());
		return;	
	}
	
	@Override
	public void addEdge(int from, int to) {
		if (graph.containsKey(from) && graph.containsKey(to)) {
			graph.get(from).add(to);
		}
		if (graphTranspose.containsKey(from) && graphTranspose.containsKey(to)) {
			graphTranspose.get(to).add(from);
		}
	}

	@Override
	public Graph getEgonet(int center) {
		//if center is not in graph return empty graph
		if (!graph.containsKey(center)) 
			return new CapGraph();
		
		//add center
		CapGraph egonet = new CapGraph();
		egonet.addVertex(center);
		
		//add all friends of center to egonet
		for (Integer vertex: graph.get(center))
			egonet.addVertex(vertex);
		
		//add edges for friend to friend connections
		for (Integer from: egonet.graph.keySet())  //iterate through all nodes of egonet
			for (Integer to: egonet.graph.keySet())// iterate through all nodes of egonet
				if (graph.get(from).contains(to))
					egonet.addEdge(from, to);
	
		return egonet;
	}		


	@Override
	public List<Graph> getSCCs() {
		List<Graph> SCCs = new ArrayList<>();
		
		//use a local copy of graph
		HashMap<Integer, HashSet<Integer>> graphForward = new HashMap<Integer, HashSet<Integer>>();
		graphForward = (HashMap<Integer, HashSet<Integer>>) graph;

		//create stack of vertices from graph nodes (keySet)
		Stack<Integer> vertices = new Stack<Integer>();		
		for (int node : graph.keySet()) {
			vertices.push(node);
		}
		
		//the result of the call to DFS is the order of nodes traversed in Step 1
		Stack<Integer> finished = DFS(graphForward, vertices);

		//need to compute the transpose of graph. already done. use a local copy of graphTranspose
		HashMap<Integer, HashSet<Integer>> graphReverse = new HashMap<Integer, HashSet<Integer>>();
		graphReverse = (HashMap<Integer, HashSet<Integer>>) graphTranspose;		
		
		//run transposed graph and reversed list through DFS. send in SCCs. do not need the result
		DFS(graphReverse, finished, SCCs);
		
		return SCCs;
	}

	//this is DFS from the lecture. 
	private Stack<Integer> DFS(HashMap<Integer, HashSet<Integer>> graphIn, Stack<Integer> vertices){
		HashSet<Integer> visited = new HashSet<Integer>();
		Stack<Integer> finished = new Stack<Integer>();
		while (!vertices.isEmpty()) {
			Integer v = vertices.pop();
			if (!visited.contains(v)) {
				DFSVisit(graphIn, v, visited, finished);
			}
		}			
		return finished;	
	}
	
	//this is DFS from the lecture with the SCC
	private Stack<Integer> DFS(HashMap<Integer, HashSet<Integer>> graphIn, Stack<Integer> vertices, List<Graph> SCCs){
		HashSet<Integer> visited = new HashSet<Integer>();
		Stack<Integer> finished = new Stack<Integer>();
		while (!vertices.isEmpty()) {
			Integer v = vertices.pop();
			CapGraph scc = new CapGraph(); //create new CapGraph scc for each strongly connected set
			if (!visited.contains(v)) {
				DFSVisit(graphIn, v, visited, finished);
				//Once DFSVisit completes, a strongly connected set is created
				//the stack finished now contains an SCC. go through it and add nodes
				while(!finished.isEmpty()) {
					int node = finished.remove(0);
					scc.addVertex(node);
					//scc.addEdge(v, node);
				}
				SCCs.add(scc); //add the scc to the SCC list
			}		
		}					
		return finished;	
	}
	
	
	//this is DFS-Visit from the lecture
	
	private void DFSVisit(HashMap<Integer, HashSet<Integer>> graphIn, Integer v, HashSet<Integer> visited, Stack<Integer> finished){
		visited.add(v);
		for (Integer n : graphIn.get(v)) {
			if (!visited.contains(n)) {
				DFSVisit(graphIn, n, visited, finished);
			}
		}
		finished.push(v);
	}

	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		return (HashMap<Integer, HashSet<Integer>>)graph;
	}
/*
	public HashMap<Integer, HashSet<Integer>> exportGraphTranspose() {
		return (HashMap<Integer, HashSet<Integer>>)graphTranspose;
	}
	
	public static void main (String[] args) {
		Graph g = new CapGraph();
		GraphLoader.loadGraph(g, "data/scc/test_4.txt");
		
		System.out.println("Adj List");
		for (int i : g.exportGraph().keySet()) {
			System.out.print(i+": ");
			for (int j : g.exportGraph().get(i)) {
				System.out.print(j+", ");
			}
			System.out.println();
		}
		System.out.println("Adj List Transpose");
		for (int i : g.exportGraphTranspose().keySet()) {
			System.out.print(i+": ");
			for (int j : g.exportGraphTranspose().get(i)) {
				System.out.print(j+", ");
			}
			System.out.println();
		}
		System.out.println("SCC");
		List<Graph> end = new ArrayList<>();
		end = g.getSCCs();
		System.out.println(end.size());
		for (Graph gr : end) {
            HashMap<Integer, HashSet<Integer>> curr = gr.exportGraph();
            
		}
		
		
	}
	*/
}
