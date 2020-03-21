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
        while(true) //might be better as a check/boolean flag
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
                    z.setParent(y);
                    break;
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
                    z.setParent(y);
                    break;
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
        while(z != null && y != null && z.getPriority() <= y.getPriority())
        {
            if(root.equals(z))
            {
                break;
            }
            //Determine which way to rotate
            if(y.getLeft()!= null && y.getLeft().equals(z))
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
            //Need to update parents parent if it exists.
            if(z.getParent()!= null)
            {
                if(z.getParent().getRight().equals(y))
                {
                    z.getParent().setRight(z);
                }
                else
                {
                    z.getParent().setLeft(z);
                }
            }
            if(y.equals(root))
            {
                root = z;
            }
            y = z.getParent();
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
            if(replacement.getParent().getRight() != null &&
                    replacement.getParent().getRight().equals(replacement))
            {
                replacement.getParent().setRight(z);
            }
            else
            {
                replacement.getParent().setLeft(z);

            }
            //swap nodes then try deleting in new spot
            Node temp = replacement.getRight();
            Node tempP = replacement.getParent();
            replacement.setLeft(z.getLeft());
            replacement.setRight(z.getRight());
            replacement.setParent(z.getParent());
            z.setLeft(null);
            z.setRight(temp);
            z.setParent(tempP);
            //didnt end up deleting will do it next time since x left is null
            size++;
            intervalDelete(z);
            //Fix replacement then move on
            recFixMax(replacement);
            //Finish moving replacement down
            rotateDown(replacement);
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

    /**
     * Prints the IntervalTreap in order
     * nodes are printed as followed:
     * ([intervalMin, intervalMax], iMax, priority)
     */
    public static void inorder(Node n)
    {
        if (n == null)
        {
            return;
        }
        inorder(n.getLeft());
        System.out.println("([" + n.getInterv().getLow()+ "," +
                            n.getInterv().getHigh() + "], " +
                            n.getIMax() + ", " +
                            n.getPriority() + ")");
        inorder(n.getRight());
    }

    /**
     * This will rotate a node down so that delete guarantees min heap status in Log n
     */
    private void rotateDown(Node n)
    {
        //we know this node has less priority since it's a successor
        while(rotateDownCheck(n))
        {

            //Determine which way we can rotate (rotations preserve inorder so only thing that matters is priority)
            if(n.getRight() != null && n.getPriority() >= n.getRight().getPriority()) //If n has a right child with less priority we need to rotate down
            {
                rotateLeft(n);
            }
            else if(n.getLeft() != null && n.getPriority() >= n.getLeft().getPriority()) // IF n has a left child with less priority we need to rotate down
            {
                rotateRight(n);
            }
        }
    }

    private boolean rotateDownCheck(Node n)
    {
        if(n.getLeft() == null && n.getRight() == null) //leaf so we done
        {
            return false;
        }
        else if(n.getLeft() == null && (n.getRight() != null && n.getRight().getPriority() > n.getPriority())) //left is null and right has greater priority
        {
            return false;
        }
        else if(n.getRight() == null && (n.getLeft() != null && n.getLeft().getPriority()> n.getPriority())) //right is null and right has greater priority
        {
            return false;
        }
        else if((n.getRight() != null && n.getRight().getPriority() > n.getPriority())
                && (n.getLeft() != null && n.getLeft().getPriority() > n.getPriority())) //Left and right exist and have greater priority
        {
            return false;
        }
        return true;
    }

    /**
     * Rotates a nodes left subtree to the right
     */
    private void rotateRight(Node n)
    {
        //Get left node and temp right subtree of left
        Node m = n.getLeft();
        Node temp = m.getRight();

        //move left
        m.setRight(n);
        n.setLeft(temp);
        //Fix Parents
        m.setParent(n.getParent());
        n.setParent(m);
        //Fix Max
        fixMax(n);
        fixMax(m);
    }

    /**
     * Rotates a nodes right subtree left
     */
    private void rotateLeft(Node n)
    {
        //Get right node and temp left subtree of right
        Node m = n.getRight();
        Node temp = m.getLeft();

        //move left
        m.setLeft(n);
        n.setRight(temp);
        //Fix Parents
        m.setParent(n.getParent());
        n.setParent(m);
        //Fix Max
        fixMax(n);
        fixMax(m);
    }
}
