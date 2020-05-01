/**
 * @author Justin Merkel, Reid Schneyer
 */
public class Interval
{
    private int low;
    private int high;

    public Interval(int low, int high)
    {
        this.low = low;
        this.high = high;
    }

    public int getLow()
    {
        return low;
    }

    public int getHigh()
    {
        return high;
    }

    public boolean overlap(Interval i)
    {
        int iLow = i.getLow();
        int iHigh = i.getHigh();
        //Use the check the assignment gives to determine overlap
        return ((iLow <= high) && (low <= iHigh));
    }
}