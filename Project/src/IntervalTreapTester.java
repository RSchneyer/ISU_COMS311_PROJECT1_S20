public class IntervalTreapTester 
{
    public static void main(String [] args)
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

        System.out.printf("\nTreap complete, now inorder printing\n");
        IntervalTreap.inorder(testTreap.getRoot());

    }
}