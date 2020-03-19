public class Interval {
    private int low;
    private int high;

    public Interval(int min, int max){
        low = min;
        high = max;
    }

    public int getLow(){
        return low;
    }
    public int getHigh(){
        return high;
    }
    public boolean overlap(Interval i){
        int iLow = i.getLow();
        int iHigh = i.getHigh();
        //TODO: Simplify the following logic
        //TODO: Not sure if the following logic handles when the highs and/or lows are the same
        return  ((iLow <=  high) && (low <= iHigh));
    }
}
