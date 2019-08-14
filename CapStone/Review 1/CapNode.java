/**
 * A class to represent a node in the map
 */
package graph;

//import geography.GeographicPoint;
import graph.CapEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.round;

/**
 * @author UCSD MOOC development team
 *
 * Class representing a vertex (or node) in our CapGraph
 *
 */
class CapNode
{
    /** The list of edges out of this node */
    private HashSet<CapEdge> edges;

    /** the latitude and longitude of this node */
/*    private GeographicPoint location;
    private double g, h, f;
    private double lat, lon;*/
    private Integer person;
    private String name, IDE, prevIDE;
    //private Integer dist;
    private HashMap<Integer, Integer> distMap = new HashMap<>();
    private Boolean Noise;

    public CapNode(int num)
    {
        this.person = num;
        this.name = "UNKNOWN";
        this.IDE = rndIDE();
        this.prevIDE = this.IDE;
        distMap.put(num,  0);
        this.Noise = false;
        //this.dist = 0;
    }

    public CapNode(int num, Integer myDist)
    {
        this.person = num;
        this.name = "UNKNOWN";
        this.IDE = rndIDE();
        this.prevIDE = this.IDE;
        distMap.put(num,  myDist);
        this.Noise = false;
        //this.dist = myDist;
    }

    public CapNode(int num, String myName)
    {
        this.person = num;
        this.name = myName;
        this.IDE = "eclipse";
        this.prevIDE = this.IDE;
        distMap.put(num,  0);
        this.Noise = false;
    }

    public CapNode(int num, String myName, String myIDE)
    {
        this.person = num;
        this.name = myName;
        this.IDE = myIDE;
        this.prevIDE = this.IDE;
        distMap.put(num,  0);
        this.Noise = false;
    }


    public CapNode(int num, String myName, String myIDE, Integer myDist)
    {
        this.person = num;
        this.name = myName;
        this.IDE = myIDE;
        this.prevIDE = this.IDE;
        distMap.put(num,  myDist);
        this.Noise = false;
    }

    public String rndIDE() {
        Random rn = new Random();
        int i = rn.nextInt(1);
        if (i == 0) {return "eclipse";}
        return "netbeans";
    }

    public void evenIDE() {
        if (round(person/2) == (person/2) ) {
            IDE = "eclipse";
        }
        else {
            IDE = "netbeans";
        }
    }




    public String getIDE() {
        return this.IDE;
    }


    public void setIDE(String newIDE) {
        if (newIDE.toLowerCase() == "netbeans") {this.IDE = "netbeans";}
        else {this.IDE = "eclipse";}
    }

    public void setPrevIDE(String newIDE) {
        if (newIDE.toLowerCase() == "netbeans") {this.prevIDE = "netbeans";}
        else {this.prevIDE = "eclipse";}
    }

    public void updateIDE() {
        if (this.IDE != this.prevIDE) {
            this.IDE = this.prevIDE;
        }
    }

    public String getName() {return this.name;}

    public int getPerson() {return this.person;}

    public Integer getDist(int i) {return distMap.get(i);}

    public Boolean getNoise() {return Noise;}

    public void setDist(int aVert, Integer myDist) {
        distMap.put(aVert, myDist);
    }

    public void setNoise(Boolean noise) {this.Noise = noise;}



}