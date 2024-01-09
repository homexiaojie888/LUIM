import com.google.common.base.Joiner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LUIMA {
    String input;
    Set<Integer> allItems;
    int max_utility;
    List<Itemset> LUIs=new ArrayList<>();
    List<List<Integer>> NUIs=new ArrayList<>();
    public List<Itemset> runAlgorithm(String input, List<List<CandidateGenerator>> lug, Set<Integer> allItems, int max_utility) throws IOException {
        this.input=input;
        this.allItems=allItems;
        this.max_utility=max_utility;

        int min_i = 0;
        for (int i = 0; i < lug.size(); i++) {
            if (!lug.get(i).isEmpty()){
                min_i=i;
                break;
            }
        }
        List<CandidateGenerator> candidateGeneratorList=lug.get(min_i);
        for (int i = 0; i < candidateGeneratorList.size(); i++) {
            List<Integer> itemset= candidateGeneratorList.get(i).getCandidateItemset();
            int utility=calculateU(input,itemset);
            if (utility<max_utility&&utility!=0){
                LUIs.add(new Itemset(itemset,utility));
            } else if (utility==0) {
                NUIs.add(itemset);
            }
        }
        List<List<Integer>> C_i=new ArrayList<>();
        for (int i = 0; i < candidateGeneratorList.size(); i++) {
            List<Integer> itemset= candidateGeneratorList.get(i).getCandidateItemset();
            C_i.add(itemset);
        }
        int i=min_i;
        while (!C_i.isEmpty()){
            List<List<Integer>> new_Ci=new ArrayList<>();
            Set<String> setCi=new HashSet<>();

            List<CandidateGenerator> CGiand1 = i+1<lug.size()?lug.get(i+1):new ArrayList<>();
            for (int k = 0; k < CGiand1.size(); k++) {
                List<Integer> CGitemset=CGiand1.get(k).getCandidateItemset();
                if (!setCi.contains(Joiner.on("&").join(CGitemset))) {
                    setCi.add(Joiner.on("&").join(CGitemset));
                    new_Ci.add(CGitemset);
                }
            }

            for (int j = 0; j < C_i.size(); j++) {
                for (Integer item:allItems) {
                    if (!C_i.get(j).contains(item)){
                        List<Integer> newC=new ArrayList<>();
                        newC.addAll(C_i.get(j));
                        newC.add(item);
                        Collections.sort(newC);
                        if (!setCi.contains(Joiner.on("&").join(newC))&&!checkHasMinNUIset(newC)){
                            new_Ci.add(newC);
                            setCi.add(Joiner.on("&").join(newC));
                        }

                    }
                }
            }
            Collections.sort(new_Ci, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> o1, List<Integer> o2) {
                    for (int j = 0; j < o1.size(); j++) {
                        if (o1.get(j)>o2.get(j)){
                            return 1;
                        }else if (o1.get(j)<o2.get(j)){
                            return -1;
                        }
                    }
                    return 0;
                }
            });
            for (int j = 0; j < new_Ci.size(); j++) {
                List<Integer> itemset=new_Ci.get(j);
                int utility=calculateU(input,itemset);
                if (utility<max_utility&&utility!=0){
                    LUIs.add(new Itemset(itemset,utility));
                } else if (utility==0) {
                    NUIs.add(itemset);
                }
            }
            C_i=new_Ci;
            i++;
    }
    return LUIs;
 }


    private boolean checkHasMinNUIset(List<Integer> newC) {
        for (int i = 0; i < NUIs.size(); i++) {
            if (newC.contains(NUIs.get(i))){
                return true;
            }
        }
        return false;
    }

    private int calculateU(String input, List<Integer> itemset) throws IOException {
        int utility = 0;
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                int utilityOfTra=0;
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                String[] itemsUtility = partions[2].split(" ");
                Map<Integer,Integer> mapItemToU=new HashMap<>();
                for (int i = 0; i < items.length; i++) {
                    mapItemToU.put(Integer.valueOf(items[i]),Integer.valueOf(itemsUtility[i]));
                }
                if (mapItemToU.keySet().containsAll(itemset)){
                    for (int i = 0; i < itemset.size(); i++) {
                        int item=itemset.get(i);
                        utilityOfTra+=mapItemToU.get(item);
                    }
                    utility+=utilityOfTra;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        return utility;

    }

}
