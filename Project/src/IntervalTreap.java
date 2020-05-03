import java.util.ArrayList;
import java.util.List;

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
        z.setiMin(z.getInterv().getLow());
        //Go down using zlow as key
        Node y = root;
        //Log(n) while
        int nodeHeight = 0;
        while(true) //might be better as a check/boolean flag
        {
            //Going down so our height increases
            nodeHeight++;
            //Update y (parent) imax
            if(y.getIMax() < z.getIMax())
            {
                y.setiMax(z.getIMax());
            }
            if(y.getiMin() > z.getiMin())
            {
                y.setiMin(z.getiMin());
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
//        if(nodeHeight > height)
//        {
//            height = nodeHeight;
//        }
        //Another log n call
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

            if(z.getParent().getLeft() != null && z.getParent().getLeft().equals(z))
            {
                z.getParent().setLeft(null);
            }
            else
            {
                z.getParent().setRight(null);
            }
            //log n + log n
            //Fix iMaxUp
            recFixMax(z.getParent());
            //Fix
            recFixHeight(z.getParent());
            return;

        }
        Node replacement = null;
        if(z.getLeft() == null)
        {
            if(z.getParent() != null && z.getParent().getRight() != null && z.getParent().getRight().equals(z))
            {
                z.getParent().setRight(z.getRight());
            }
            else if(z.getParent() != null)
            {
                z.getParent().setLeft(z.getRight());
            }
            else
            {
                root = z.getRight();
            }
            z.getRight().setParent(z.getParent());
            //log n + log n
            recFixMax(z.getRight());
            recFixHeight(z.getRight());

        }
        else if(z.getRight() == null)
        {
            if(z.getParent() != null && z.getParent().getRight() != null && z.getParent().getRight().equals(z))
            {
                z.getParent().setRight(z.getLeft());
            }
            else if(z.getParent() != null)
            {
                z.getParent().setLeft(z.getLeft());
            }
            else
            {
                root = z.getLeft();
            }
            z.getLeft().setParent(z.getParent());
            // logn + logn
            recFixMax(z.getLeft());
            recFixHeight(z.getLeft());
        }
        ////////////////////////////StartWeird Case/////////////////////////////////
        else
        {
            /**
             * Note while this does call itself again, it is still log n since we know the next time we call the method
             * it will take O(logn) time since left will be null meaning logn + logn
             */
            replacement = min(z.getRight());
            // This case is different since minheap can be violated
            //NOTE TRANSPLANT IS LOG N and fixes imax and height
            transplant(z, replacement);
            //swap nodes then try deleting in new spot
            size++;
            // + logn
            intervalDelete(z);
            //Finish moving replacement down
            // + log n
            rotateDown(replacement);
           // return;
        }
        ///////////////////////End Weird Case ////////////////////////////////////
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
     * DO NOT TOUCH IT WORKS NOW; SEE TRANSPLANT2 FOR WHAT FAILURE LOOKS LIKE (Log N time)
     */
    private void transplant(Node n, Node z)
    {
        //Log n since it has the recursive fix calls.
        //Grab nodes
        Node nParent = n.getParent();
        Node nLeft = n.getLeft();
        Node nRight = n.getRight();
        Node zParent = z.getParent();
        //  zleft is always null
        Node zRight = z.getRight();
        //case 1 z is right child of n
        if(n.getRight().equals(z))
        {
            z.setParent(nParent);
            z.setRight(n);
            z.setLeft(nLeft);

            n.setLeft(null);
            n.setRight(zRight);
            n.setParent(z);
            if(nParent != null && nParent.getRight() != null && nParent.getRight().equals(n))
            {
                nParent.setRight(z);
            }
            else if(nParent != null)
            {
                nParent.setLeft(z);
            }
            else
            {
                root = z;
            }
            //Shouldn't have to check n's new parent since its z
            if(nLeft != null)
            {
                nLeft.setParent(z);
            }
            if(zRight != null)
            {
                zRight.setParent(n);
            }
        }
        else
        {
            z.setParent(nParent);
            z.setLeft(nLeft);
            z.setRight(nRight);

            n.setLeft(null);
            n.setRight(zRight);
            n.setParent(zParent);

            if(nParent != null && nParent.getRight() != null && nParent.getRight().equals(n))
            {
                nParent.setRight(z);
            }
            else if (nParent != null)
            {
                nParent.setLeft(z);
            }
            else
            {
                root = z;
            }
            if(nLeft != null)
            {
                nLeft.setParent(z);
            }
            if(nRight != null)
            {
                nRight.setParent(z);
            }
            if(zRight != null)
            {
                zRight.setParent(n);
            }
            zParent.setLeft(n);
        }
        recFixHeight(n);
        recFixMax(n);
        recFixHeight(z);
        recFixMax(z);
    }
    private void transplant2(Node n, Node z)
    {
        //Grab nodes
        Node nParent = n.getParent();
        Node nLeft = n.getLeft();
        Node nRight = n.getRight();
        Node zParent = z.getParent();
        Node zLeft = z.getLeft();
        Node zRight = z.getRight();
        //set references for n
        if(zParent.equals(n))
        {
            n.setParent(z);
        }
        else
        {
            n.setParent(zParent);
        }
        n.setLeft(zLeft);
        n.setRight(zRight);
        //set references for z
        z.setParent(nParent);
        if(nRight.equals(z))
        {
            z.setRight(n);
        }
        else
        {
            z.setRight(nRight);
        }
        z.setLeft(nLeft);

        if(nRight != null && nRight != z)
        {
            nRight.setParent(z);
        }
        if(nLeft != null && nLeft != z)
        {
            nLeft.setParent(z);
        }
        if(zRight != null && zRight != n)
        {
            zRight.setParent(n);
        }
        if(zLeft != null && zLeft != n)
        {
            zLeft.setParent(n);
        }
        if(nParent != null && nParent.getRight() != null && nParent.getRight().equals(n))
        {
            nParent.setRight(z);
        }
        else if (nParent != null && nParent != z)
        {
            nParent.setLeft(z);
        }
        // Then do the z parent
        if(zParent != null && zParent.getRight() != null && zParent.getRight().equals(z))
        {
            zParent.setRight(n);
        }
        else if (nParent != null && zParent != n)
        {
            zParent.setLeft(n);
        }
    }
    /**
     * Fixes the max of the node (also the min for the extra credit)
     */
    private void fixMax(Node x)
    {
        //case 1 x is a leaf
        if (x.getRight() == null && x.getLeft() == null)
        {
            x.setiMax(x.getInterv().getHigh());
            x.setiMin(x.getInterv().getLow());
        }
        //case 2 right == null
        else if (x.getRight() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getLeft().getIMax()));
            x.setiMin(Math.min(x.getInterv().getLow(), x.getLeft().getiMin()));
        }
        //case 3 left == null
        else if (x.getLeft() == null)
        {
            x.setiMax(Math.max(x.getInterv().getHigh(), x.getRight().getIMax()));
            x.setiMin(Math.min(x.getInterv().getLow(), x.getRight().getiMin()));
        } else
        {
            int childMax = Math.max(x.getLeft().getIMax(), x.getRight().getIMax());
            int childMin = Math.min(x.getLeft().getiMin(), x.getRight().getiMin());
            x.setiMin(Math.min(childMin, x.getInterv().getLow()));
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
     * Recursive call to fix imax up (and min for EC)
     */
    private void recFixMax(Node n)
    {
        if(n == null)
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
            if(n.getRight() != null && n.getLeft() != null) //This is the change for delete to maintain priority I forgot that it's possible one of the subtrees breaks priority when you use both
            {
                if(n.getRight().getPriority() > n.getLeft().getPriority())
                {
                    rotateRight(n);
                }
                else
                {
                    rotateLeft(n);
                }
            }
            else if(n.getRight() != null && n.getPriority() >= n.getRight().getPriority()) //If n has a right child with less priority we need to rotate down
            {
                rotateLeft(n);
            }
            else if(n.getLeft() != null && n.getPriority() >= n.getLeft().getPriority()) // IF n has a left child with less priority we need to rotate down
            {
                rotateRight(n);
            }
            else
            {
                System.out.println("WTFFFF");
            }
        }
        //Logn to fix max and height after log n while
        recFixMax(n);
        recFixHeight(n);
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
        else if(n.getRight() == null && (n.getLeft() != null && n.getLeft().getPriority()> n.getPriority())) //right is null and left has greater priority
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
     * Extra credit 1
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
            if(x.getLeft() != null && x.getInterv().getLow() > i.getLow())
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
     * Extra credit 2
     */
    public List<Interval> overlappingIntervals(Interval i)
    {
        if(root == null)
        {
            return null;
        }
        //start working on the problem recursively
        List<Interval> list = new ArrayList<Interval>();
        recOverlap(i, root, list);
        return list;
    }

    private void recOverlap(Interval i, Node n, List<Interval> list)
    {
        //If the node has a left child
        if(n.getLeft() != null)
        {
           //Now check if it's possible for a node below to have stored the interval
            Interval max = new Interval(n.getLeft().getiMin(), n.getLeft().getIMax());
          //If it's possible for the interval to overlap on the child check
           if(max.overlap(i))
           {
               recOverlap(i, n.getLeft(), list);
           }
        }
        //Check if this node belongs in the list
        if(n.getInterv().overlap(i))
        {
            list.add(n.getInterv());
        }
        //if the node has a right child that overlaps
        if(n.getRight() != null)
        {
            Interval max = new Interval(n.getRight().getiMin(), n.getRight().getIMax());
            //If it's possible for the interval to overlap on the child check
            if(max.overlap(i))
            {
                recOverlap(i, n.getRight(), list);
            }
        }
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
