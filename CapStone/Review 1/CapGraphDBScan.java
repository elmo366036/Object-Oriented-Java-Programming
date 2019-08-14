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
public class CapGraphDBScan extends CapGraph {


    HashSet<Integer> Visited = new HashSet<Integer>();
    HashSet<Integer> Unvisited ;
    HashMap<Integer, HashSet<Integer>> Clusters = new HashMap<Integer, HashSet<Integer>>();
    HashMap<Integer, CapNode> PersonMapNode = new HashMap<Integer, CapNode>();
    HashMap<Integer, HashSet<Integer>> NeighborPoints = new HashMap<Integer, HashSet<Integer>>();


    public CapGraphDBScan() {
        super();
        //Unvisited = copyVertexes(vertices);
    }

    public CapGraphDBScan(HashSet<Integer> vertices) {
        super(vertices);
        //Unvisited = copyVertexes(vertices);
    }


    // Function to merge two sets in Java
    public static HashSet<Integer> mergeSets(HashSet<Integer> a, HashSet<Integer> b)       {
        HashSet<Integer> set = new HashSet<>();

        Collections.addAll(set, a.toArray(new Integer[0]));
        Collections.addAll(set, b.toArray(new Integer[0]));

        return set;
    }

    public HashSet<Integer> BFS(Integer P, int numhops) {
        if (numhops < 0) {
            throw new IllegalArgumentException("No negative hop number"); }
        else if (!getVertices().contains(P)) {
            throw new IllegalArgumentException("Point is not in graph"); }

        HashSet<Integer> allPoints = iterBFS(P, numhops, 0, new HashSet<Integer>());
        return allPoints;
    }

    protected HashSet<Integer> iterBFS(Integer P, int numhops, int count, HashSet<Integer> tempSet) {
        HashSet<Integer> tempsetnext;
        if ((int)count >= numhops) {
            return tempSet;
        }
        else {
            tempsetnext = capGraphMap.get(P);
            for (Integer i:tempsetnext) {
                iterBFS(i, numhops, count+1, mergeSets(tempSet, tempsetnext));
            }
        }
        return mergeSets(tempSet, tempsetnext);
    }

    public HashSet<Integer> copyVertexes(Set<Integer> aSet) {
        HashSet<Integer> myCopy = new HashSet<>();
        for (Integer i:aSet) { myCopy.add(i); }
        return myCopy;
    }

    public Boolean inCluster(Integer P) {
        if (Clusters.keySet().contains(P)) return true;
        for (Integer i:Clusters.keySet()) {
            if (Clusters.get(i).contains(P)) return true;
        }
        return false;
    }



    public Map<Integer, HashSet<Integer>> find_clusters(int Numhops, int MinPts, HashMap<Integer, HashSet<Integer>> Persons) {


        //PersonNodeMap = find_distance(Numhops);
        //HashMap<Integer, HashSet<Integer>> NeighborPoints = new HashMap<>();
        //for each unvisited point P in dataset D
        Unvisited = copyVertexes(this.getVertices());

        while (!Unvisited.isEmpty()) {
            //for (Integer P : Unvisited) {
            Integer P = Unvisited.iterator().next();
            Visited.add(P);
            Unvisited.remove(P);

            //HashSet<Integer> MyPts = regionQuery(Numhops, P);
            HashSet<Integer> NeighborPoints = regionQuery(Numhops, P);
            //NeighborPoints.put(P, MyPts);


            if (NeighborPoints.size() < MinPts) {
                //mark P as NOISE
                CapNode cn = new CapNode(P);
                cn.setNoise(true);
                personNodeMap.put(P, cn);
            } else {
                //C = next cluster
                HashSet<Integer> C = new HashSet<Integer>();
                C = expandCluster(P, NeighborPoints, C, Numhops, MinPts);
                Clusters.put(P, C);

//                PersonNodeMap = find_distance(Numhops)
//                //for each unvisited point P in dataset D
//
////                for (Integer P : Unvisited)
////                    visted.add(P);
////                unvisited.pop(P)
//
//                HashSet<Integer> MyPts = regionQuery(P, eps)
//                NeighborPoints.add(p, MyPts);
            }
        }

        return Clusters;

    }



    // expandCluster(P, NeighborPts, C, eps, MinPts)
    public HashSet<Integer> expandCluster(Integer P, HashSet<Integer> NeighborSet, HashSet<Integer> C, Integer NumHops, Integer MinPts)
    {
        // add P to cluster C
        C.add(P);
        // for each point P' in NeighborPts
        for (Integer PP : NeighborSet) {
            // if P' is not visited
            if (!Visited.contains(PP)) {
                //mark P' as visited;
                Visited.add(PP);
                Unvisited.remove(PP);
                // NeighborPts' = regionQuery(P', eps)
                HashSet<Integer> NeighborPtsPrime = regionQuery(PP, NumHops);
                //if sizeof(NeighborPts') >= MinPts
                if (NeighborPtsPrime.size() >= MinPts) {
                    //NeighborPts = NeighborPts joined with NeighborPts'
                    NeighborSet = mergeSets(NeighborPtsPrime, NeighborSet);

                }
            }
            // if P' is not yet member of any cluster
//                if (!C.contains(PP))
//                    add P ' to cluster C
            if (!inCluster(PP)) {
                C.add(PP);
            }
        }
        return C;
    }

        //regionQuery(P, eps)
        //return all points within P's eps-neighborhood (including P)


    HashSet<Integer> regionQuery(int Numhops, Integer P) {
        HashSet<Integer> myRegion = BFS(P, Numhops);
        return myRegion;
    }


}
