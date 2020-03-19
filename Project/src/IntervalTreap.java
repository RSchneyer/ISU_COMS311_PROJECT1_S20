/**
 * @author Justin Merkel, Reid Schneyer
 */
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
        //Add size from new node
        size++;
        //empty root case
        if(root == null)
        {
            root = z;
            height = 0; //Im pretty sure height starts at 0
            return;
        }
        //First set z.imax to z.high
        z.setiMax(z.getInterv().getHigh());
        //Go down using zlow as key
        Node y = root;
        //Log(n) while
        int nodeHeight = 0;
        while((!y.getLeft().equals(z)) && (!y.getRight().equals(z)))
        {
            //Going down so our height increases (might cause off by one but too lazy to double check)
            nodeHeight++;
            //Update y (parent) imax
            if(y.getIMax() < z.getIMax())
            {
                y.setiMax(z.getIMax());
            }
            //try left
            if(y.getInterv().getLow() > z.getInterv().getLow())
            {
                //Found location
                if(y.getLeft() == null)
                {
                    y.setLeft(z);
                }
                else
                {
                    //keep looking
                    y = y.getLeft();
                }
            }
            else
            {
                if(y.getRight() == null)
                {
                    y.setRight(z);
                }
                else
                {
                    y = y.getRight();
                }
            }
        }
        if(nodeHeight > height)
        {
            height = nodeHeight;
        }
        //Now that we found our insertion point, second phase begins
        //Log(n) while
        while(z.getPriority() <= y.getPriority())
        {
            //Determine which way to rotate
            if(y.getLeft().equals(z))
            {
                //right rotation
                //Swap right subtrees
                y.setLeft(z.getRight());
                z.setRight(y);
                //swap parents
                z.setParent(y.getParent());
                y.setParent(z);
                //need to fix imaxes only changes y and z
                fixMax(y);
                fixMax(z);
            }
            else
            {
                //left rotation
                //swap left subtrees
                y.setRight(z.getLeft());
                z.setLeft(y);
                //swap parents
                z.setParent(y.getParent());
                y.setParent(z);

                fixMax(y);
                fixMax(z);
            }
        }
    }

    /**
     * removes node z from the interval treap. This operation
     * must maintain the required interval treap properties. The expected running time of this
     * method should be O(log n) on an n-node interval treap.
     */
    void intervalDelete(Node z)
    {
        size--;
        //if z is a leaf
        if(z.getRight() == null && z.getLeft() == null)
        {
            //Fix iMaxUp
            recFixMax(z.getParent());
            if(z.getParent().getLeft().equals(z))
            {
                z.getParent().setLeft(null);
            }
            else
            {
                z.getParent().setRight(null);
            }
            return;
        }
        //Find the replacement node (could be null)
        Node replacement = null;
        if(z.getLeft() == null)
        {
            replacement = z.getRight();
        }
        else if(z.getRight() == null)
        {
            replacement = z.getLeft();
        }
        else
        {
            replacement = min(z.getRight());
            // This case is different since minheap can be violated
            // TODO code here
        }
        //Remove the replacement from its location and recursively fix the max
        if(replacement.getParent().getRight().equals(replacement))
        {
            replacement.getParent().setRight(null);
            recFixMax(replacement.getParent());
        }
        else
        {
            replacement.getParent().setLeft(null);
            recFixMax(replacement.getParent());
        }
        //Now place it in and recursively fix imax
        if(z.getParent().getLeft().equals(z))
        {
            z.getParent().setLeft(replacement);
            recFixMax(z.getParent());
        }
        else
        {
            z.getParent().setRight(replacement);
            recFixMax(z.getParent());
        }

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
        Node x = root;
        while(x != null && !x.getInterv().overlap(i))
        {
            //i is to the left
            if(x.getLeft() != null && x.getLeft().getIMax() >= i.getLow())
            {
                x = x.getLeft();
            }
            else
            {
                x = x.getRight();
            }
        }
        return x;
    }

    /**
     * Fixes the max of the node
     */
    private void fixMax(Node x)
    {
        //case 1 x is a leaf
        if(x.getRight() == null && x.getLeft() == null)
        {
            x.setiMax(x.getInterv().getHigh());
        }
        //case 2 right == null
        else if(x.getRight() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getLeft().getIMax()));
        }
        //case 3 left == null
        else if(x.getLeft() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getRight().getIMax()));
        }
        else
        {
            int childMax = Math.max(x.getLeft().getIMax(), x.getRight().getIMax());
            x.setiMax(Math.max(childMax, x.getInterv().getHigh()));
        }
    }

    /**
     * Replaces with children nodes.
     */
    private void replace(Node newChild, Node oldChild, Node parent)
    {
        if(parent.getRight().equals(oldChild))
        {
            parent.setRight(newChild);
            newChild.setParent(parent);
        }
        else
        {
            parent.setLeft(newChild);
            newChild.setParent(parent);
        }
    }

    /**
     * Get the min of a subtree
     */
    private Node min(Node x)
    {
        while(x.getLeft() != null)
        {
            x = x.getLeft();
        }
        return x;
    }

    /**
     * Recursive call to fix imax up
     */
    private void recFixMax(Node n)
    {
        if(n.equals(root))
        {
            return;
        }
        fixMax(n);
        recFixMax(n.getParent());
    }
}
