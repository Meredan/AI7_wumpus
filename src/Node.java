import java.util.ArrayList;

public class Node {
    int id;
    int X;
    int Y;
    int wumpusNum;
    int pitNum;
    boolean visited = false;
    boolean hasGold = false;
    boolean hasWumpus = false;
    boolean hasPit = false;
    boolean smell = false;
    boolean breeze = false;
    boolean safe = false;
    boolean start = false;
    ArrayList<Node> tail = new ArrayList<>();

    ArrayList<Node> friends = new ArrayList<>();


    

    public Node(int nodeId, int xSpot, int ySpot){
    	id = nodeId;
    	X = xSpot;
    	Y = ySpot;
    }



    
    public String toString() { return "<" + id + ">"; }
}
