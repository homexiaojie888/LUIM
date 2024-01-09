public class MaptoRank {
    public static int mapToRank1(int base,int TwuOrU){

        double rank=Math.round((double)TwuOrU/base);
        return (int)rank;

    }
}
