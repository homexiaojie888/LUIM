import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Generator {
    int itemsCount;
    int tidCount;
    boolean[][] bitOfItem;
    int[][] uOfItem;
    Map<Integer,Integer> mapItemtoIndex=new HashMap<>();
    //items 为按照字典序或其他顺序排列的item总数，tidcount为事务总数
    public Generator(Set<Integer> items, int tidCount) {
        itemsCount = items.size();
        this.tidCount = tidCount;
        bitOfItem=new boolean[itemsCount][tidCount];
        uOfItem=new int[itemsCount][tidCount];
        int index=0;
        for (Integer item:items) {
            mapItemtoIndex.put(item,index++);
        }
    }
    //items 的顺序要按照字典序或其他固定顺序
    public void setGeneratorOne(int tid,String[] items, String[] utils){
        for (int k = 0; k < items.length; k++) {
            int index=mapItemtoIndex.get(Integer.valueOf(items[k]));
            bitOfItem[index][tid-1]=true;
            uOfItem[index][tid-1]=Integer.valueOf(utils[k]);
        }
    }
    public ULB getUBofItemset(int[] itemset){

        boolean[] bitTidsOfItemset=new boolean[tidCount];
        for (int i = 0; i < bitTidsOfItemset.length; i++) {
            bitTidsOfItemset[i]=true;
        }
        //每个 item
        for (int i = 0; i < itemset.length; i++) {
            //获取item的索引
            int index=mapItemtoIndex.get(itemset[i]);
            //每个tid
            for (int j = 0; j < bitTidsOfItemset.length; j++) {
                bitTidsOfItemset[j]=bitTidsOfItemset[j]&bitOfItem[index][j];
            }
        }
        ULB res=new ULB();
        res.setItemset(itemset);
        Map<Integer,Integer> mapItemToUB=new LinkedHashMap<>();
        res.setMapItemToLB(mapItemToUB);
        for (int i = 0; i < bitTidsOfItemset.length; i++) {
            if (bitTidsOfItemset[i]==true){
                for (int j = 0; j < itemset.length; j++) {
                    int index=mapItemtoIndex.get(itemset[j]);
                    Integer old= mapItemToUB.get(itemset[j]);
                    if (old!=null){
                        mapItemToUB.put(itemset[j],old+uOfItem[index][i]);
                    }else {
                        mapItemToUB.put(itemset[j],uOfItem[index][i]);
                    }

                }

            }

        }
        return res;
    }

    public void setGenerator(Map<Integer, String[]> mapTidToItems, Map<Integer, String[]> mapTidToUtilities) {
        for (Integer tid: mapTidToItems.keySet()) {
            setGeneratorOne(tid,mapTidToItems.get(tid),mapTidToUtilities.get(tid));
        }
    }

}
