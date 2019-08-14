/**
 * @author UCSD MOOC development team and YOU
 *
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between
 *
 */
package graph;


//import geography.GeographicPoint;
import util.GraphLoader;
//import util.PathGraphLoader;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author UCSD MOOC development team and YOU
 *
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between
 *
 */
public class CapGraph implements  Graph {
    // Maintain both nodes and edges as you will need to
    // be able to look up nodes by lat/lon or by roads
    // that contain those nodes.
    //private HashMap<Integer, CapNode> pointNodeMap;
    protected HashSet<CapEdge> edges;
    protected HashMap<Integer, CapNode> personNodeMap = new HashMap<Integer, CapNode>();
    protected HashMap<Integer, HashSet<Integer>> capGraphMap;
    //private Stack<Integer> S = new Stack<>();
    //private HashSet<Integer> visited;
    protected Stack<Integer> vertices;
    protected Stack<Integer> finished;
    protected HashSet<Integer> newVisited;
    protected Stack<Integer> newFinished;
    protected List<Graph> sccGraphs;
    protected Stack<Integer> oldFinished;
    //private Stack<Integer> newVertices;



    public static void main(String[] args)
    {
        System.out.print("Making a new map...");
        graph.CapGraph theMap = new graph.CapGraph();
        System.out.print("DONE. \nLoading the map...");
        GraphLoader.loadGraph(theMap,"C:\\Users\\sneddors\\projects\\SocialNetworks\\data\\facebook_1000.txt");  //loadRoadMap("data/testdata/simpletest.map", theMap);
        System.out.println("DONE.");

    }

    public HashMap<Integer, HashSet<Integer>> getGraph() {
        return capGraphMap;
    }

    public HashMap<Integer, CapNode> getPersonNodeMap() {
        return personNodeMap;
    }

    public CapGraph()
    {
        //capGraphMap = new HashMap<Integer, CapNode>();
        edges = new HashSet<CapEdge>();
        double totalDist = 0;
        capGraphMap = new HashMap<Integer, HashSet<Integer>>();
    }

    public CapGraph(HashSet<Integer> vertexes)
    {
        //capGraphMap = new HashMap<Integer, CapNode>();
        edges = new HashSet<CapEdge>();
        double totalDist = 0;
        capGraphMap = new HashMap<Integer, HashSet<Integer>>();
        for (Integer i:vertexes) {
            this.addVertex(i);
        }
    }



    public CapGraph makeGraph(HashSet<Integer> graphSet) {
        CapGraph ncg = new CapGraph();
        for (Integer i:graphSet) {
            ncg.addVertex(i);
            HashSet<Integer> iConnect = getNeighbors(i);
            for (Integer j:iConnect)  {
                ncg.addVertex(j);
                ncg.addEdge(i,j);
            }
        }
        return ncg;
    }

    public void ranIDE(Double myratio) {
        personNodeMap = this.getPersonNodeMap();
        Random rand = new Random();
        for (Integer i:personNodeMap.keySet()) {
            Float r = rand.nextFloat();
            CapNode cn = personNodeMap.getOrDefault(i, new CapNode(i));
            if (r <= myratio) cn.setIDE("eclipse");
            else cn.setIDE("netbeans");
            personNodeMap.put(i, cn);
        }
    }

    public List<Integer> bfs(Integer start,
                             Integer goal,
                             Consumer<Integer> nodeSearched)
    {
        // Setup - check validity of inputs
        if (start == null || goal == null)
            throw new NullPointerException("Cannot find route from or to null node");
        CapNode startNode = personNodeMap.get(start);
        CapNode endNode = personNodeMap.get(goal);
        if (startNode == null) {
            System.err.println("Start node " + start + " does not exist");
            return null;
        }
        if (endNode == null) {
            System.err.println("End node " + goal + " does not exist");
            return null;
        }

        // setup to begin BFS
        HashMap<CapNode, CapNode> parentMap = new HashMap<CapNode, CapNode>();
        Queue<CapNode> toExplore = new LinkedList<CapNode>();
        HashSet<CapNode> visited = new HashSet<CapNode>();
        toExplore.add(startNode);
        CapNode next = null;

        while (!toExplore.isEmpty()) {
            next = toExplore.remove();

            // hook for visualization
            nodeSearched.accept(next.getPerson());

            if (next.equals(endNode)) break;
            Set<Integer> neighborsInt = getNeighbors(next.getPerson());
            Set<CapNode> neighbors = new HashSet<>();
            for (Integer i:neighborsInt) {
                neighbors.add(new CapNode(i));
            }
            for (CapNode neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, next);
                    toExplore.add(neighbor);
                }
            }
        }
        if (!next.equals(endNode)) {
            System.out.println("No path found from " +start+ " to " + goal);
            return null;
        }
        // Reconstruct the parent path
        List<Integer> path =
                reconstructPath(parentMap, startNode, endNode);
        return path;
    }

    private List<Integer> reconstructPath(HashMap<CapNode, CapNode> parentMap,
                    CapNode start, CapNode goal)
    {
        LinkedList<Integer> path = new LinkedList<Integer>();
        CapNode current = goal;

        while (!current.equals(start)) {
            path.addFirst(current.getPerson());
            current = parentMap.get(current);
        }

        // add start
        path.addFirst(start.getPerson());
        return path;
    }

    @Override
    public List<Graph> getSCCs() {
        vertices = new Stack<>();
        for (Integer i:getVertices()) {
            vertices.add(i);
        }
        finished = dfs(this, vertices);
        CapGraph gTrans = new CapGraph();

        for(Integer v : getVertices()) {
            gTrans.addVertex(v);
            for(Integer neighbor : capGraphMap.get(v)) {
                gTrans.addVertex(neighbor);
                gTrans.addEdge(neighbor, v);
            }
        }

        return dfsGraphs(gTrans, finished);
    }


    public List<Graph> dfsGraphs(CapGraph g, Stack<Integer> newVertices) {
        newVisited = new HashSet<>();
        sccGraphs = new ArrayList<>();

        while (!newVertices.empty()) {
            Integer v = newVertices.pop();

            if (!newVisited.contains(v)) {
                CapGraph scc = new CapGraph();
                scc.addVertex(v);
                dfsVisitGraphs(g, v, newVisited, scc);
                sccGraphs.add(scc);
            }
        }
        return sccGraphs;
    }

    private void dfsVisitGraphs(CapGraph g, Integer v, HashSet<Integer> newVisited, CapGraph scc) {
        newVisited.add(v);
        for (Integer i : g.getNeighbors(v)) {
            if (!newVisited.contains(i)) {
                dfsVisitGraphs(g, i, newVisited, scc);
            }
        }
        scc.addVertex(v);
        return;
    }


    public Stack<Integer> dfs(CapGraph g, Stack<Integer> vertices) {
        HashSet<Integer> visited = new HashSet<>();
        Stack<Integer> finished = new Stack<>();

        while (!vertices.empty()) {
            Integer v = vertices.pop();

            if (!visited.contains(v)) {
                dfsVisit(g, v, visited, finished);
            }
        }
        return finished;
    }

    public void dfsVisit(CapGraph g, Integer v, HashSet<Integer> visited, Stack<Integer> finished) {
        visited.add(v);
        for (Integer i:g.getNeighbors(v)) {
            if (!visited.contains(i)) {
                dfsVisit(g,i,visited,finished);
            }
        }
        finished.push(v);
        return;
    }


    public HashSet<Integer> getNeighbors(Integer v) {
        return capGraphMap.get(v);

    }



    /* (non-Javadoc)
     * @see graph.PathGraph#addVertex(int)
     */
    public void addVertex(int num) throws IllegalArgumentException {
        addVertex((Integer)num);
    }

    public void addVertex(CapNode num) throws IllegalArgumentException {
        addVertex((Integer)num.getPerson());
    }


    //@Override
    public void addVertex(Integer num) throws IllegalArgumentException  {
        // check node is valid
        if (num == null)
            throw new NullPointerException("addEdge: pt1:"+num.toString()+"is not an integer");
        CapNode numNode = new CapNode(num, "aperson", "netbeans");
        personNodeMap.put(num, numNode);
        Set<Integer> keys = capGraphMap.keySet();
        if (!capGraphMap.keySet().contains(num)) {capGraphMap.put(num, new HashSet<>());}
    }

    public void addVertex(Integer num, Integer myDist) throws IllegalArgumentException  {
        // check node is valid
        if (num == null)
            throw new NullPointerException("addEdge: pt1:"+num.toString()+"is not an integer");
        CapNode numNode = new CapNode(num, myDist);
        personNodeMap.put(num, numNode);
        Set<Integer> keys = capGraphMap.keySet();
        if (!capGraphMap.keySet().contains(num)) {capGraphMap.put(num, new HashSet<>());}
    }

    //@Override
    public void addVertex(Integer num, String myIDE) throws IllegalArgumentException  {
        // check node is valid
        if (num == null)
            throw new NullPointerException("addEdge: pt1:"+num.toString()+"is not an integer");
        CapNode numNode = new CapNode(num, "aperson", myIDE);
        personNodeMap.put(num, numNode);
        Set<Integer> keys = capGraphMap.keySet();
        if (!capGraphMap.keySet().contains(num)) {capGraphMap.put(num, new HashSet<>());}
    }



    public void addEdge(int from, int to) throws IllegalArgumentException  {
        // TODO Auto-generated method stub
        // check nodes are valid
        if (!getVertices().contains(from))
            throw new NullPointerException("addEdge: pt1:"+from+"is not in graph");
        if (!getVertices().contains(to))
            throw new NullPointerException("addEdge: pt2:"+to+"is not in graph");

        if (!capGraphMap.containsKey(from)) {
            HashSet<Integer> hs = new HashSet<>();
            hs.add(to);
            capGraphMap.put(from, hs);
            CapEdge newEdge = new CapEdge(new CapNode(from),new CapNode(to));
            edges.add(newEdge);
        }
        else {
            HashSet<Integer> hs = capGraphMap.get(from);
            if (!hs.contains(to)) {
                hs.add(to);
                capGraphMap.put(from, hs);
                CapEdge newEdge = new CapEdge(new CapNode(from),new CapNode(to));
                edges.add(newEdge);
            }
        }
    }

    public CapEdge findEdge(int from, int to) {
        for (CapEdge ce:edges) {
            if (ce.getStartNode().getPerson() == from && ce.getEndNode().getPerson() == to) {
                return ce;
            }
        }
        return null;
    }

    public void subEdge(int from, int to) throws IllegalArgumentException  {
        // TODO Auto-generated method stub
        // check nodes are valid
        if (!getVertices().contains(from))
            throw new NullPointerException("subEdge: pt1:"+from+"is not in graph");
        if (!getVertices().contains(to))
            throw new NullPointerException("subEdge: pt2:"+to+"is not in graph");

        CapEdge ce = findEdge(from,to);
        if (ce == null) {
            throw new NullPointerException("subedge "+from+" to "+to+" is not part of the graph"); }
        else {
            edges.remove(ce);
        }
    }

    public void subVertex(Integer i)  throws IllegalArgumentException {
        if (!getVertices().contains(i))
            throw new NullPointerException("vertex: "+i+" is not in graph");
       Set<Integer> verts = getNeighbors(i);
       for (Integer j:verts) {
           subEdge(i,j);
       }
       personNodeMap.remove(i);
       vertices.remove(i);
       capGraphMap.remove(i);
    }


    /* (non-Javadoc)
     * @see graph.PathGraph#getEgonet(int)
     */

    @Override
    public Graph getEgonet(int center) {

        CapGraph egoGraph = new CapGraph();
        egoGraph.addVertex(center);

        for(Integer i : getNeighbors(center)) {
            egoGraph.addVertex(i);
            egoGraph.addEdge(center, i);
            egoGraph.addEdge(i, center);
        }

        for(Integer i : getNeighbors(center)) {
            for(Integer j : getNeighbors(i)) {
                if(egoGraph.getVertices().contains(j)) {
                    egoGraph.addEdge(i, j);
                    egoGraph.addEdge(j, i);
                }
            }
        }

        return egoGraph;
    }


    @Override
/*    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        // TODO Auto-generated method stub
        return capGraphMap;
    }*/


    //   @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        for(Integer v : capGraphMap.keySet()) {
            System.out.print(v+": ");
            StringBuffer neighbors = new StringBuffer();
            for(Integer n : getNeighbors(v)) {
                neighbors.append(n+" ");
            }
            System.out.println(neighbors);
        }
        System.out.println("numVertices: "+this.getNumVertices()+"\tnumCapEdges: "+getNumEdges());
        return capGraphMap;
    }

    public void printGraph() {
        HashMap<Integer, HashSet<Integer>> pGraph = exportGraph();
        for (Integer i:pGraph.keySet()) {
            System.out.printf("Vertex "+"%d"+i+" vertexes ");
            for (Integer j:pGraph.get(i)) {
                System.out.printf("%d"+j+" ");
            }
        }
    }


    public Set<CapEdge> getEdges(){
        return edges;
    }

    /**
     * Get the number of vertices (road intersections) in the graph
     * @return The number of vertices in the graph.
     */
    public int getNumVertices()
    {
        return capGraphMap.keySet().size();
    }

    /**
     * Return the intersections, which are the vertices in this graph.
     * @return The vertices in this graph as GeographicPoints
     */
    public Set<Integer> getVertices()
    {
        return capGraphMap.keySet();
    }

    /**
     * Get the number of road segments in the graph
     * @return The number of edges in the graph.
     */
    public int getNumEdges()
    {
        return edges.size();
    }

}

    /*
    1) Create an empty stack ‘S’ and do DFS traversal of a graph. In DFS traversal,
     after calling recursive DFS for adjacent vertices of a vertex, push the vertex to stack.
     In the above graph, if we start DFS from vertex 0, we get vertices in stack as 1, 2, 4, 3, 0.
    2) Reverse directions of all arcs to obtain the transpose graph.
    3) One by one pop a vertex from S while S is not empty. Let the popped vertex be ‘v’.
    Take v as source and do DFS (call DFSUtil(v)). The DFS starting from v prints strongly connected component of v.
    In the above example, we process vertices in order 0, 3, 4, 2, 1 (One by one popped from stack).


    1. Perform a DFS of G and number the vertices in order of completion of the recursive calls.
    2. Construct a new directed graph Gr by reversing the direction of every arc in G.
    3. Perform a DFS on Gr starting the search from the highest numbered vertex according to the numbering assigned at step 1.
       If the DFS does not reach all vertices, start the next DFS from the highest numbered remaining vertex.
    4. Each tree in the resulting spanning forest is a strong component of G.

A. DEPTH FIRST SEARCH of G

    DFS(G, stack VERTICES):
    Initialize set VISITED and stack FINISHED
    While (VERTICES not empty)
        v = VERTICES.pop()
        if (v not in VISITED)
            DFS-VISIT(G, v, VISITED, FINISHED)
    return FINISHED

    DFS-VISIT(G, v, VISITED, FINISHED):
        add v to VISITED
        for (n in getNeighbors(v)):
            if (n not in VISITED):
                DFS-VISIT(G, n, VISITED, FINISHED)
        push v on FINISHED

A. DEPTH FIRST SEARCH of G^t (transpose of G)
    exploring in reverse order of finish time from step 1. Each tree found forms an SCC
    pass in FINISHED stack from step A as the VERTICES_stepB stack (== finished stack from step A)
    each (initial) call to DFS-visit from DFS produces a different strongly connected component graph

    ignore already visited vertices





   */
