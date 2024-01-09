import com.google.common.base.Joiner;

import java.io.*;
import java.util.*;

public class LUG_Miner {

    Set<Integer> allItems=new TreeSet<>();
    public List<List<CandidateGenerator>> runAlgorithm(String input, int max_utility) throws IOException {

        List<List<CandidateGenerator>> LUG=new ArrayList<>();
        List<List<CandidateGenerator>> HUG=new ArrayList<>();
        Map<Integer, Integer> mapCG1=new TreeMap<>();
        int Null_TWU=TWUCount(input,mapCG1);

        List<CandidateGenerator> listCG1=new ArrayList<>();
        for (Integer item:mapCG1.keySet()) {
            CandidateGenerator cg=new CandidateGenerator();
            cg.getCandidateItemset().add(item);
            cg.setTWU(mapCG1.get(item));
            cg.setPre_TWU(Null_TWU);
            if (cg.TWU==cg.pre_TWU){
                cg.setIsGenerator(false);
            }else {
                cg.setIsGenerator(true);
            }
            listCG1.add(cg);
        }

        List<CandidateGenerator> LUG1=new ArrayList<>();
        List<CandidateGenerator> HUG1=new ArrayList<>();
        for (int i = 0; i < listCG1.size(); i++) {
            CandidateGenerator cg=listCG1.get(i);
            if (cg.isGenerator()&&cg.getTWU()>=max_utility){
                HUG1.add(cg);
            } else if (cg.isGenerator()&&cg.getTWU()<max_utility) {
                LUG1.add(cg);
            }
        }
        LUG.add(LUG1);
        HUG.add(HUG1);
        for (int i = 0; true; i++) {
            //set pre_TWU
            List<CandidateGenerator> CGiadd1=GenerateCands(HUG.get(i));
            List<CandidateGenerator> LUGiadd1=new ArrayList<>();
            List<CandidateGenerator> HUGiadd1=new ArrayList<>();
            if (CGiadd1.isEmpty()){
                break;
            }
            //set TWU
            TWUCount2(input,CGiadd1);
            //set isgenertor
            for (int j = 0; j < CGiadd1.size(); j++) {
                CandidateGenerator cg=CGiadd1.get(j);
                if (cg.getTWU()==cg.getPre_TWU()){
                    cg.setIsGenerator(false);
                }else {
                    cg.setIsGenerator(true);
                }
            }
            for (int j = 0; j < CGiadd1.size(); j++) {
                CandidateGenerator cg=CGiadd1.get(j);
                if (cg.isGenerator()&&cg.getTWU()>=max_utility){
                    HUGiadd1.add(cg);
                } else if (cg.isGenerator()&&cg.getTWU()<max_utility) {
                    LUGiadd1.add(cg);
                }
            }
            LUG.add(LUGiadd1);
            HUG.add(HUGiadd1);
        }

        return LUG;
    }

    private void TWUCount2(String input, List<CandidateGenerator> listCG) throws IOException {
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                Set<Integer> itemSet=new HashSet<>();
                for (int i = 0; i < items.length; i++) {
                    itemSet.add(Integer.valueOf(items[i]));
                }
                for (int i = 0; i < listCG.size(); i++) {
                    CandidateGenerator cg=listCG.get(i);
                    if (itemSet.containsAll(cg.getCandidateItemset())){
                        cg.TWU+=TU;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
    }

    private List<CandidateGenerator> GenerateCands(List<CandidateGenerator> candidateGenerators) {
        List<CandidateGenerator> res=new ArrayList<>();
        Map<String,CandidateGenerator> mapRes=new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] str1=o1.split("&");
                String[] str2=o2.split("&");
                for (int i = 0; i < str1.length; i++) {
                    if (Integer.valueOf(str1[i])>Integer.valueOf(str2[i])){
                        return 1;
                    } else if (Integer.valueOf(str1[i])<Integer.valueOf(str2[i])) {
                        return -1;
                    }
                }
                return 0;
            }
        });
        int size=candidateGenerators.get(0).CandidateItemset.size()+1;
        for (int i = 0; i < candidateGenerators.size()-1; i++) {
            for (int j = i+1; j < candidateGenerators.size(); j++) {
                Set<Integer> itemset=new TreeSet<>();
                CandidateGenerator x=candidateGenerators.get(i);
                CandidateGenerator y=candidateGenerators.get(j);
                itemset.addAll(x.getCandidateItemset());
                itemset.addAll(y.getCandidateItemset());
                if (itemset.size()==size){
                    String str= Joiner.on("&").join(itemset);
                    if (mapRes.keySet().contains(str)){
                        CandidateGenerator newCG=mapRes.get(str);
                        newCG.setPre_TWU(Math.min(newCG.getPre_TWU(),Math.min(x.getTWU(),y.getTWU())));
                    }else{
                        CandidateGenerator newCG=new CandidateGenerator();
                        newCG.setCandidateItemset(new ArrayList<>(itemset));
                        newCG.setPre_TWU(Math.min(x.getTWU(),y.getTWU()));
                        mapRes.put(str,newCG);
                    }

                }
            }
        }
        res.addAll(mapRes.values());
        return res;
    }

    public int TWUCount(String input, Map<Integer, Integer> mapItemToTWU) throws IOException {
        String thisLine;
        BufferedReader myInput = null;
        int totalTU=0;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                String[] partions=thisLine.split(":");
                String[] items = partions[0].split(" ");
                int TU=Integer.valueOf(partions[1]);
                String[] itemsUtilities = partions[2].split(" ");
                totalTU+=TU;
                for (int i = 0; i < items.length; i++) {
                    int item=Integer.valueOf(items[i]);
                    allItems.add(item);
                    mapItemToTWU.put(item,mapItemToTWU.get(item)==null?TU:mapItemToTWU.get(item)+TU);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        return totalTU;
    }
}

