import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.*;

public class IntervalTreapTester
{
    static int bigNumber = 10000;
    private Random rand = new Random();
    private IntervalTreap treap;
    private List<Interval> intervalList = new ArrayList<Interval>();
    private List<Node> nodeList = new ArrayList<Node>();
    @Before
    public void setup()
    {
        treap = new IntervalTreap();
        rand = new Random();
        for(int i =0; i < bigNumber; i++)
        {
            int numBig = rand.nextInt();
            int numSmall = rand.nextInt();
            if(numSmall > numBig)
            {
                int temp = numSmall;
                numSmall = numBig;
                numBig = temp;
            }
            intervalList.add(new Interval(numSmall, numBig));
            nodeList.add(new Node(intervalList.get(i)));
            treap.intervalInsert(nodeList.get(i));
        }

    }
    @Test
    public void checkInsert()
    {
        assertEquals(bigNumber, treap.getSize());
        assertTrue(Math.log(bigNumber) <= treap.getHeight());// should always be greater than or equal to
       // assertTrue(treap.getHeight() < (Math.log(bigNumber) * Math.log(bigNumber))); //Give a bit of wiggle room
    }
    @Test
    public void smallTests()
    {
        int [][] testIntervalVals = {{0,3},{5,8},{6,10},{8,9},{15,23},{16,21},{17,19},{19,20},{25,30},{26,26}};
        int [] testPriorities = {21,17,20,12,16,8,13,17,10,11};

        IntervalTreap testTreap = new IntervalTreap();
        Node testNode;
        Interval testInterval;

        for(int x=0; x<testIntervalVals.length; x++)
        {
            testInterval = new Interval(testIntervalVals[x][0], testIntervalVals[x][1]);
            testNode = new Node(testInterval);
            testNode.setPriority(testPriorities[x]);

            testTreap.intervalInsert(testNode);
        }

    //    System.out.printf("\nTreap of size " + testTreap.getSize() +" complete, now inorder printing\n");
    //    IntervalTreap.inorder(testTreap.getRoot());
        //Might make more sense to make inorder also keep track of height and size to double check
        for(int i =0; i < testIntervalVals.length; i++) // Fails on i = 2
        {
            Node del = testTreap.intervalSearchExactly(new Interval(testIntervalVals[9 - i][0], testIntervalVals[9-i][1]));
            testTreap.intervalDelete(del);
            //testTreap.intervalDelete(IntervalTreap.min(testTreap.getRoot()));
        }
//        assertEquals(0, testTreap.getSize());
//        assertNull(testTreap.getRoot());

      //  System.out.println(testTreap.getSize());
    }

    @Test
    public void DeleteRoot()
    {
        int [][] testIntervalVals = {{0,3},{5,8},{6,10},{8,9},{15,23},{16,21},{17,19},{19,20},{25,30},{26,26}};
        int [] testPriorities = {21,17,20,12,16,8,13,17,10,11};

        IntervalTreap testTreap = new IntervalTreap();
        Node testNode;
        Interval testInterval;

        for(int x=0; x<testIntervalVals.length; x++)
        {
            testInterval = new Interval(testIntervalVals[x][0], testIntervalVals[x][1]);
            testNode = new Node(testInterval);
            testNode.setPriority(testPriorities[x]);

            testTreap.intervalInsert(testNode);
        }
        testTreap.intervalDelete(testTreap.getRoot().getLeft());
        //  System.out.println(testTreap.getSize());
    }


    @Test
    public void basicSearch()
    {
        for(int x =0; x <treap.getSize(); x++)
        {
                assertNotNull(treap.intervalSearch(intervalList.get(x)));
        }
    }

    @Test
    public void InorderKey()
    {
        Node min = IntervalTreap.min(treap.getRoot());
        for(int i = 0; i < treap.getSize() - 1; i++)
        {
            Node next = IntervalTreap.successor(min);
                assertTrue(min.getInterv().getLow() <= next.getInterv().getLow());

            min = next;
        }
    }

    @Test
    public void ExactKeyMatch()
    {
        for(int i =0; i < bigNumber; i++)
        {
            treap.intervalSearchExactly(intervalList.get(i));
        }
    }

    @Test
    public void InsertMaintainsHeight()
    {
        int expectedHeight = treap.getHeight();
        int actual = GetHeight(treap.getRoot(),-1);
        //System.out.println("expected " + expectedHeight + " actual" + actual);
        assertTrue(GetHeight(treap.getRoot(), -1) == treap.getHeight());

    }

   @Test
    public void InsertMaintainsImax()
    {
        assertTrue(MaxRec(treap.getRoot()));
    }

    @Test
    public void InsertMaintainsPriority()
    {
        assertTrue(recCheckPrio(treap.getRoot()));
    }

    /**
     * Basic delete technically has a chance to fail if two intervals are exactly the same but not likely
     */
    @Test
    public void DeleteTest()
    {
            Node toDel = treap.intervalSearchExactly(intervalList.get(0));
            treap.intervalDelete(toDel);
            assertTrue(treap.getSize() == bigNumber - 1);
            assertNull(treap.intervalSearchExactly(intervalList.get(0)));
    }

    @Test
    public void DeleteAll()
    {
        for(int i =0; i < bigNumber; i++)
        {
            Node toDel = treap.intervalSearchExactly(intervalList.get(i));
            assertNotNull(toDel);
            treap.intervalDelete(toDel);
        }
        assertEquals(0, treap.getSize());
        Node root = treap.getRoot();
        assertNull(treap.getRoot());
    }

    @Test
    public void DeleteMaintainsPriority()
    {
        for(int i =0; i < bigNumber; i++)
        {
            Node toDel = treap.intervalSearchExactly(intervalList.get(i));
            assertNotNull(toDel);
            treap.intervalDelete(toDel);
            //assertTrue(MaxRec(treap.getRoot()));
            assertTrue(recCheckPrio(treap.getRoot()));
        }
    }

    @Test
    public void DeleteMaintainsIMax()
    {
        for(int i =0; i < bigNumber; i++)
        {
            Node toDel = treap.intervalSearchExactly(intervalList.get(i));
            assertNotNull(toDel);
            treap.intervalDelete(toDel);
            assertTrue(MaxRec(treap.getRoot()));
        }
    }

    @Test
    public void DeleteMaintainsHeight()
    {
        for(int i =0; i < bigNumber - 1; i++)
        {
            Node toDel = treap.intervalSearchExactly(intervalList.get(i));
            assertNotNull(toDel);
            treap.intervalDelete(toDel);
            assertEquals(treap.getRoot().getHeight(), GetHeight(treap.getRoot(), -1));
        }
    }

    @Test
    public void overlappingIntervals()
    {
        for(int i = 0; i < 15; i++)
        {
            List<Interval> realList = getRealList(treap, intervalList.get(i));
            List<Interval> outputList = treap.overlappingIntervals(intervalList.get(i));
            //Sanity check
            assertTrue(outputList != null);
            assertTrue(realList != null);
            assertEquals(realList.size(), outputList.size());
            //Every Interval that exists in the real list exists in the output
            for(int j = 0; j < realList.size(); j++)
            {
                assertTrue(outputList.contains(realList.get(j)));
            }
        }
    }





    //---------------------Helpers-------------------------------------
    public static List<Interval> getRealList(IntervalTreap t, Interval i)
    {
        List<Interval> list = new ArrayList<Interval>();
        Node n = IntervalTreap.min(t.getRoot());
        while(n != null)
        {
            if(n.getInterv().overlap(i))
            {
                list.add(n.getInterv());
            }
            n = IntervalTreap.successor(n);
        }
        return list;
    }
   public static boolean recCheckPrio(Node n)
    {
        if(n == null || n.getParent() == null)
        {
            return true;
        }
        if(n.getPriority() > n.getParent().getPriority())
        {
            boolean right = recCheckPrio(n.getRight());
            boolean left = recCheckPrio(n.getLeft());
            if(left && right)
            {
                return true;
            }
        }
        return false;
    }

    public static int GetHeight(Node n, int prev)
    {
        if(n == null)
        {
            return prev;
        }
        int right = GetHeight(n.getRight(), prev +1);
        int left = GetHeight(n.getLeft(), prev +1);
        if(right > left)
        {
            return right;
        }
        else
        {
            return left;
        }
    }

    public static boolean MaxRec(Node n)
    {
        if(n == null)
        {
            return true;
        }
        if(checkMax(n))
        {
            boolean right = MaxRec(n.getRight());
            boolean left = MaxRec(n.getLeft());
            if(right && left)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean checkMax(Node n)
    {
        if(n.getIMax() == getActualMax(n))
        {
            return true;
        }
        return false;
    }

    public static int getActualMax(Node x)
    {
        //case 1 x is a leaf
        if(x.getRight() == null && x.getLeft() == null)
        {
            return x.getInterv().getHigh();
        }
        //case 2 right == null
        else if(x.getRight() == null)
        {
            return Math.max(x.getInterv().getHigh(), x.getLeft().getIMax());
        }
        //case 3 left == null
        else if(x.getLeft() == null)
        {
            return Math.max(x.getInterv().getHigh(), x.getRight().getIMax());
        }
        else
        {
            int childMax = Math.max(x.getLeft().getIMax(), x.getRight().getIMax());
            return Math.max(childMax, x.getInterv().getHigh());
        }
    }
}