package graph;

import util.GraphLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CapGraphAnalytics extends CapGraph {
    //protected CapGraph cGA = new CapGraph();
    //protected HashMap<Integer, CapNode> personNodeMap = cGA.getPersonNodeMap();
    //protected HashMap<Integer, HashSet<Integer>> cGA.capGraphMap;
    protected Set<Integer> verts;
    //protected HashMap<Integer, CapNode> pNodeMap = super.getPersonNodeMap();
    protected Double ratio, nbweight, ecweight;


    public CapGraphAnalytics(Boolean loadData){
        super();
        ratio = 0.5d;
        if (loadData)
        {GraphLoader.loadGraph(this,"C:\\Users\\sneddors\\projects\\SocialNetworks\\data\\facebook_1000.txt");}  //loadRoadMap("data/testdata/simpletest.map", theMap);
        //HashMap<Integer, CapNode> personNodeMap = cGA.getPersonNodeMap();
    }

    public CapGraphAnalytics(String path, Double myratio) {
        super();
        this.ratio = myratio;
        GraphLoader.loadGraph(this, path);
        //HashMap<Integer, CapNode> personNodeMap = cGA.getPersonNodeMap();
    }

    public CapGraphAnalytics(Double myRatio) {
        super();
        ratio = myRatio;
        //GraphLoader.loadGraph(cGA, path);
        //HashMap<Integer, CapNode> personNodeMap = cGA.getPersonNodeMap();
    }

    public CapGraphAnalytics(){
        super();
        ratio = 1.0d;
        //HashMap<Integer, CapNode> personNodeMap = cGA.getPersonNodeMap();
    }

    public Double getRatio() {return ratio;}


    public void ranIDE(Double myratio) {
        //personNodeMap = cGA.getPersonNodeMap();
        Random rand = new Random();
        for (Integer i:personNodeMap.keySet()) {
            Float r = rand.nextFloat();
            CapNode cn = personNodeMap.getOrDefault(i, new CapNode(i));
            if (r <= myratio) cn.setIDE("eclipse");
            else cn.setIDE("netbeans");
            personNodeMap.put(i, cn);
        }
    }

    public int getNumNetbeans(){
        int count = 0;
        for (Integer i:verts) {
            if (personNodeMap.get(i).getIDE() == "netbeans") {
                count++;
            }
        }
        return count;
    }

    public Integer countEclipse() {
        Integer count = 0;
        for (Integer i:personNodeMap.keySet()) {
            if (personNodeMap.get(i).getIDE() == "eclipse") {
                count++;
            }
        }
        return count;
    }

    public Integer countNetbeans() {
        Integer count = 0;
        for (Integer i:personNodeMap.keySet()) {
            if (personNodeMap.get(i).getIDE() == "netbeans") {
                count++;
            }
        }
        return count;
    }

    public void Cascade(int iterations) {
        Boolean outcome = false;
        Integer i;
        Integer tooMany = 1000;

        if (iterations > 0) {
            for (i = 0; i < iterations; i++) {
                outcome = updateGraph();
                if (outcome) break;
            }
            if (outcome) {System.out.println("Graph fully updated in "+i+" iterations"); }
            else {
                System.out.println("Graph NOT fully updated in "+i+" iterations");
            }
        }
        else {
            i = 0;
            while (!outcome) {
                outcome = updateGraph();
                i++;
                if (i >= tooMany) {
                    System.out.println("Graph does not converge after 1000 iterations");
                    break;}
            }
        }
    }

    public Boolean updateGraph() {
        HashMap<Integer, HashSet<Integer>> capGraphMap = this.getGraph();
        HashSet<Integer> flipToNetBeans = new HashSet<>();
        HashSet<Integer> flipToEclipse = new HashSet<>();
        Integer ecweight, nbweight;
        Integer total = 0;


        for (Integer i:capGraphMap.keySet()) {
            verts = capGraphMap.keySet();
            personNodeMap = this.getPersonNodeMap();
            total = 0;
            nbweight = 0;
            ecweight = 0;
            Double myRatio = 0d;
            Set<Integer> neighbors = this.getNeighbors(i);

            for (Integer j:neighbors) {
                total = neighbors.size();
                CapNode nd = personNodeMap.get(j);
                if (nd.getIDE() == "eclipse") {
                    ecweight++;
                }
            }

            nbweight = total - ecweight;
            if (total > 0)
            { myRatio = nbweight.doubleValue()/total.doubleValue();}


            if (myRatio >= ratio && personNodeMap.get(i).getIDE() != "netbeans" && total > 0) { //&& myRatio * nbweight >= (1-myRatio)* ecweight) {
                flipToNetBeans.add(i);
            }
            else if (myRatio < ratio && personNodeMap.get(i).getIDE() != "eclipse"  && total > 0) { // && myRatio * nbweight < (1-myRatio)* ecweight) {
                flipToEclipse.add(i);
            }
        }

        HashMap<Integer, CapNode> personNodeMap = this.getPersonNodeMap();
        for (Integer i:flipToNetBeans) {
            CapNode cn = personNodeMap.get(i);
            cn.setIDE("netbeans");
            personNodeMap.put(i, cn);
        }
        for (Integer i:flipToEclipse) {
            CapNode cn = personNodeMap.get(i);
            cn.setIDE("eclipse");
            personNodeMap.put(i, cn);
        }
        if (flipToNetBeans.isEmpty() && flipToEclipse.isEmpty()) { return true;}
        return false;
    }



// C:\Users\sneddors\projects\SocialNetworks\src\Week3SampleWriteupPrompt1.pdf

}
