import java.util.Random;

public class Node
{
    private Node parent;
    private Node left;
    private Node right;
    private Interval nodeInterval;
    private int iMax;
    private int priority;

    public Node(Interval i)
    {
        //Constructor
        nodeInterval = i;
        //Generate random priority max bounds to guarantee logn height
        Random rand = new Random();
        priority = rand.nextInt();
    }

    public Node getParent()
    {
        return parent;
    }

    public Node getLeft()
    {
        return left;
    }

    public Node getRight()
    {
        return right;
    }

    public Interval getInterv()
    {
        return nodeInterval;
    }

    public int getIMax()
    {
        return iMax;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public void setLeft(Node left)
    {
        this.left = left;
    }

    public void setRight(Node right)
    {
        this.right = right;
    }


    public void setiMax(int iMax)
    {
        this.iMax = iMax;
    }

}
