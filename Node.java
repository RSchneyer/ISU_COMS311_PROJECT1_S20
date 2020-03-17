public class Node {
    private Node parent;
    private Node left;
    private Node right;
    private Interval nodeInterval;
    private int iMax;
    private int priority;

    public Node(){
        //Constructor
    }

    public Node getParent(){
        return parent;
    }
    public Node getLeft(){
        return left;
    }
    public Node getRight(){
        return right;
    }

    public Interval getInterv(){
        return nodeInterval;
    }

    public int getIMax(){
        return iMax;
    }
    public int getPriority(){
        return priority;
    }

    
}
