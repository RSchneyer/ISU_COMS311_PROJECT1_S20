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
        assertTrue(treap.getRoot().getPriority() < treap.getRoot().getRight().getPriority());
        assertEquals(bigNumber, treap.getSize());
        assertTrue(Math.log(bigNumber) <= treap.getHeight());// should always be greater than or equal to
        assertTrue(treap.getHeight() < (Math.log(bigNumber) * Math.log(bigNumber))); //Give a bit of wiggle room
    }
    @Test
    public void basicInsert()
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

        System.out.printf("\nTreap of size " + testTreap.getSize() +" complete, now inorder printing\n");
        IntervalTreap.inorder(testTreap.getRoot());
        //Might make more sense to make inorder also keep track of height and size to double check
    }

    @Test
    public void basicSearch()
    {
        for(int x =0; x <bigNumber; x++)
        {
                assertNotNull(treap.intervalSearch(intervalList.get(x)));
        }
    }

    @Test
    public void InorderKey()
    {

    }
}