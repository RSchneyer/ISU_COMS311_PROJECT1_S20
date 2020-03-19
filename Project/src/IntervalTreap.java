public class IntervalTreap
{
    private Node root;
    private int size;
    private int height;
    /**
     * Constructor with no parameters
     */
    public IntervalTreap()
    {
        height = 0;
        size = 0;
        root = null;
    }

    /**
     * Returns a reference to the root node
     */
    Node getRoot()
    {
        return root;
    }

    /**
     * Returns the number of nodes in the treap
     */
    int getSize()
    {
        return size;
    }

    /**
     * Returns the height of the treap
     */
    int getHeight()
    {
        return height;
    }

    /**
     * adds node z, whose interv attribute references an
     * Interval object, to the interval treap. This operation must maintain the required interval
     * treap properties. The expected running time of this method should be O(log n) on an
     * n-node interval treap.
     */
    void intervalInsert(Node z)
    {
        if(root == null)
        {
            root = z;
            height = 0; //Im pretty sure height starts at 0
        }
        
    }

    /**
     * removes node z from the interval treap. This operation
     * must maintain the required interval treap properties. The expected running time of this
     * method should be O(log n) on an n-node interval treap.
     */
    void intervalDelete(Node z)
    {

    }

    /**
     *returns a reference to a node x in the interval
     * treap such that x.interv overlaps interval i, or null if no such element is in the treap.
     * This method must not modify the interval treap. The expected running time of this method
     * should be O(log n) on an n-node interval treap.
     */
    Node intervalSearch(Interval i)
    {
        if(root == null)
        {
            return null;
        }
        //TODO
        return null;
    }
}
