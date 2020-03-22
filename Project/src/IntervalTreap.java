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
        if(root == null)
        {
            return 0;
        }
        return root.getHeight();
    }

    /**
     * adds node z, whose interv attribute references an
     * Interval object, to the interval treap. This operation must maintain the required interval
     * treap properties. The expected running time of this method should be O(log n) on an
     * n-node interval treap.
     */
    public void intervalInsert(Node z)
    {
        //Add size from new node
        size++;
        z.setHeight(0);
        //empty root case
        if(root == null)
        {
            root = z;
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
        recFixHeight(y);
        //Now that we found our insertion point, second phase begins
        //Log(n) while since rotations and fixHeight are O(1)
        while(y!= null && z.getPriority() <= y.getPriority())
        {
            y = z.getParent();
            if(y.equals(root))
            {
                root = z;
            }
            //Determine which way to rotate
            if(y.getLeft() != null && y.getLeft().equals(z)) //z is left child of parent
            {
                //right rotation on parent
                rotateRight(y);
            }
            else if(y.getRight() != null && y.getRight().equals(z))
            {
                //left rotation on parent
                rotateLeft(y);
            }
            y = z.getParent();
            fixHeight(y);
        }
        //Need to fix all the way up again Still logn
        recFixHeight(y);
    }
    /**
     * removes node z from the interval treap. This operation
     * must maintain the required interval treap properties. The expected running time of this
     * method should be O(log n) on an n-node interval treap.
     */
    void intervalDelete(Node z)
    {
        if(z == null)
        {
            return;
        }
        size--;
        //if z is a leaf
        if(z.getRight() == null && z.getLeft() == null)
        {
            if(z.equals(root))
            {
                root = null;
                return;
            }

            if(z.getParent().getLeft().equals(z))
            {
                z.getParent().setLeft(null);
            }
            else
            {
                z.getParent().setRight(null);
            }
            //Fix iMaxUp
            recFixMax(z.getParent());
            //Fix
            recFixHeight(z.getParent());
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
        ////////////////////////////StartWeird Case/////////////////////////////////
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
        ///////////////////////End Weird Case ////////////////////////////////////

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
        if(z.getParent() != null && z.getParent().getLeft() != null && //Gotta love null checks
                z.getParent().getLeft().equals(z))
        {
            z.getParent().setLeft(replacement);
            replacement.setParent(z.getParent());
            recFixMax(z.getParent());
        }
        else if (z.getParent() != null)
        {
            z.getParent().setRight(replacement);
            replacement.setParent(z.getParent());
            recFixMax(z.getParent());
        }
        else
        {
            //Think this means z is root?
            if(z.equals(root))
            {
                root = replacement;
                root.setParent(null);
            }
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
            //Currently the low is higher than our low so move left
            if(x.getInterv().getLow() > i.getLow())
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
        if (x.getRight() == null && x.getLeft() == null)
        {
            x.setiMax(x.getInterv().getHigh());
        }
        //case 2 right == null
        else if (x.getRight() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getLeft().getIMax()));
        }
        //case 3 left == null
        else if (x.getLeft() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getRight().getIMax()));
        } else
        {
            int childMax = Math.max(x.getLeft().getIMax(), x.getRight().getIMax());
            x.setiMax(Math.max(childMax, x.getInterv().getHigh()));
        }
    }

    /**
     * Get the min of a subtree
     */
    public static Node min(Node x)
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
        if(n == null || n.equals(root))
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
     * Gets the successor
     */
    public static Node successor(Node n)
    {
        if(n.getRight() != null)
            return min(n.getRight());
        Node parent = n.getParent();
        while(parent != null && n.equals(parent.getRight()))
        {
            n = parent;
            parent = parent.getParent();
        }
        return parent;
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
     *            n                     m
     *         m    3         ->      1   n
     *       1   2                       2  3
     */
    private void rotateRight(Node n)
    {
        //If n's parent exists
        if(n.getParent() != null)
        {
            //If the right child isnt null and is n
            if(n.getParent().getRight() != null && n.getParent().getRight().equals(n))
            {
                n.getParent().setRight(n.getLeft());
            }
            else
            {
                n.getParent().setLeft(n.getLeft());
            }
        }

        //Get left node and temp right subtree of left
        Node m = n.getLeft();
        Node two = m.getRight();
        if(two!= null){two.setParent(n); } //THIS LINE IS PAIN
        //move left
        m.setRight(n);
        n.setLeft(two);
        //Fix Parents
        m.setParent(n.getParent());
        n.setParent(m);
        //Fix Max
        fixMax(n);
        fixMax(m);
        //now heights change
        fixHeight(n);
        fixHeight(m);
        if(n.equals(root))
        {
            root = m;
        }
    }

    /**
     * Rotates a nodes right subtree left
     *      *            m                     n
     *      *         n    3         <-      1   m
     *      *       1   2                       2  3
     */
    private void rotateLeft(Node n)
    {
        if(n.getParent() != null)
        {
            if (n.getParent().getRight() != null && n.getParent().getRight().equals(n))
            {
                n.getParent().setRight(n.getRight());
            } else
            {
                n.getParent().setLeft(n.getRight());
            }
        }
        //Get right node and temp left subtree of right
        Node m = n.getRight();
        Node temp = m.getLeft();
        if(temp!= null){temp.setParent(n);} //THIS LINE IS PAIN
        //move left
        m.setLeft(n);
        n.setRight(temp);
        //Fix Parents
        m.setParent(n.getParent());
        n.setParent(m);
        //Fix maxes
        fixMax(n);
        fixMax(m);
        //fix height
        fixHeight(n);
        fixHeight(m);
        if(n.equals(root))
        {
            root = m;
        }
    }

    private void fixHeight(Node n)
    {
        if(n == null)
        {
            return;
        }
        int right = 0;
        int left =0;
        if(n.getLeft() == null && n.getRight() == null)
        {
            n.setHeight(0);
            return;
        }
        if(n.getLeft() != null)
        {
            left = n.getLeft().getHeight();
        }
        if(n.getRight()!= null)
        {
            right = n.getRight().getHeight();
        }
        n.setHeight(Math.max(left,right) + 1);
    }
    /**
     * Returns a reference to a node in the treap such that low = low and high = high
     */
    public Node intervalSearchExactly(Interval i)
    {
        if(root == null)
        {
            return null;
        }
        Node x = root;
        while(x != null && !(x.getInterv().getLow() == i.getLow() && x.getInterv().getHigh() == i.getHigh()))
        {
            //Currently the low is higher than our low so move left
            if(x.getInterv().getLow() > i.getLow())
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

    private void recFixHeight(Node n)
    {
        if(n == null)
        {
            return;
        }
        fixHeight(n);
        recFixHeight(n.getParent());
    }
}
