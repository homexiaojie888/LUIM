import com.google.common.base.Joiner;

import java.io.*;
import java.util.*;

public class OldLUIM {
    String input;
    String output;
    Set<Integer> allItems=new HashSet<>();
    int max_utility;
    List<Itemset> LUIs=new ArrayList<>();
    List<List<Integer>> NUIs=new ArrayList<>();
    BufferedWriter writer=null;
    long runtime ;

    int patternCount;
    long candidatesCount;
    List<List<Integer>> DB=new ArrayList<>();
    List<Integer> TraIdtoTU=new ArrayList<>();
    List<List<Integer>> DB_utility=new ArrayList<>();
    public void runAlgorithm(String input, String output, int max_utility) throws IOException {
        this.input=input;
        this.output=output;
        this.max_utility=max_utility;

        long startTime=System.currentTimeMillis();

        List<List<CandidateGenerator>> LUG=LUG_Miner();

        MemoryLogger.getInstance().checkMemory();

        writer = new BufferedWriter(new FileWriter(output));

        List<Itemset> LUIs=LUIMA(LUG);

        writer.close();

        runtime=System.currentTimeMillis()-startTime;

        MemoryLogger.getInstance().checkMemory();
    }
    public List<List<CandidateGenerator>> LUG_Miner() throws IOException {

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
//        System.out.println("LUG1:");
//        for (int i = 0; i < LUG1.size(); i++) {
//            CandidateGenerator CG=LUG1.get(i);
//            System.out.print(Joiner.on("").join(CG.getCandidateItemset()));
//            System.out.print(" "+CG.isGenerator());
//            System.out.print(" preTWU:"+CG.getPre_TWU());
//            System.out.println(" TWU:"+CG.getTWU());
//        }
//        System.out.println("HUG1:");
//        for (int i = 0; i < HUG1.size(); i++) {
//            CandidateGenerator CG=HUG1.get(i);
//            System.out.print(Joiner.on("").join(CG.getCandidateItemset()));
//            System.out.print(" "+CG.isGenerator());
//            System.out.print(" preTWU:"+CG.getPre_TWU());
//            System.out.println(" TWU:"+CG.getTWU());
//        }
        LUG.add(LUG1);
        HUG.add(HUG1);
        for (int i = 0; true; i++) {
            //set pre_TWU
            List<CandidateGenerator> CGiadd1=GenerateCands(HUG.get(i));
            MemoryLogger.getInstance().checkMemory();
            List<CandidateGenerator> LUGiadd1=new ArrayList<>();
            List<CandidateGenerator> HUGiadd1=new ArrayList<>();
            if (CGiadd1.isEmpty()){
                break;
            }
            //set TWU
            TWUCount22(CGiadd1);
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
                } else if (cg.isGenerator()&&cg.getTWU()>0&&cg.getTWU()<max_utility) {
                    LUGiadd1.add(cg);
                }
            }
//            System.out.println("LUG"+(i+2)+":");
//            for (int k = 0; k < LUGiadd1.size(); k++) {
//                CandidateGenerator CG=LUGiadd1.get(k);
//                System.out.print(Joiner.on("&").join(CG.getCandidateItemset()));
//                System.out.print(" "+CG.isGenerator());
//                System.out.print(" preTWU:"+CG.getPre_TWU());
//                System.out.println(" TWU:"+CG.getTWU());
//            }
//            System.out.println("HUG"+(i+2)+":");
//            for (int k = 0; k < HUGiadd1.size(); k++) {
//                CandidateGenerator CG=HUGiadd1.get(k);
//                System.out.print(Joiner.on("&").join(CG.getCandidateItemset()));
//                System.out.print(" "+CG.isGenerator());
//                System.out.print(" preTWU:"+CG.getPre_TWU());
//                System.out.println(" TWU:"+CG.getTWU());
//            }
            LUG.add(LUGiadd1);
            HUG.add(HUGiadd1);
            System.out.println("#LUG"+(i+2)+": "+LUGiadd1.size());
            System.out.println("#HUG"+(i+2)+": "+HUGiadd1.size());
        }

        return LUG;
    }
    private void TWUCount22(List<CandidateGenerator> listCG){

        for (int i = 0; i < DB.size(); i++) {
            List<Integer> TranOne=DB.get(i);
            for (int j = 0; j < listCG.size(); j++) {
                CandidateGenerator cg=listCG.get(j);
                if (TranOne.containsAll(cg.getCandidateItemset())){
                    cg.TWU+=TraIdtoTU.get(i);
                }
            }
        }
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
        //int size2=candidateGenerators.get(0).CandidateItemset.size()+1;
        int size=candidateGenerators.isEmpty()?0:candidateGenerators.get(0).CandidateItemset.size()+1;
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
                TraIdtoTU.add(TU);
                List<Integer> transOne=new ArrayList<>();
                List<Integer> transOneUtility=new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    int item=Integer.valueOf(items[i]);
                    int utility=Integer.valueOf(itemsUtilities[i]);
                    transOne.add(item);
                    transOneUtility.add(utility);
                    allItems.add(item);
                    mapItemToTWU.put(item,mapItemToTWU.get(item)==null?TU:mapItemToTWU.get(item)+TU);
                }
                DB.add(transOne);
                DB_utility.add(transOneUtility);
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
    public List<Itemset> LUIMA(List<List<CandidateGenerator>> lug) throws IOException {

        int min_i = 0;
        for (int i = 0; i < lug.size(); i++) {
            if (!lug.get(i).isEmpty()){
                min_i=i;
                break;
            }
        }
        System.out.println("C"+(min_i+1)+":");
        List<CandidateGenerator> candidateGeneratorList=lug.get(min_i);
        for (int i = 0; i < candidateGeneratorList.size(); i++) {
            candidatesCount++;
            List<Integer> itemset= candidateGeneratorList.get(i).getCandidateItemset();
            int utility=calculateU2(itemset);

            //System.out.print(Joiner.on("&").join(itemset)+":");
            //System.out.println(utility);

            if (utility<max_utility&&utility!=0){
                LUIs.add(new Itemset(itemset,utility));
                myprint(itemset,utility);
                patternCount++;
            } else if (utility==0) {
                NUIs.add(itemset);
            }
        }
        List<List<Integer>> C_i=new ArrayList<>();
        for (int i = 0; i < candidateGeneratorList.size(); i++) {
            List<Integer> itemset = candidateGeneratorList.get(i).getCandidateItemset();
            C_i.add(itemset);
        }
        int i=min_i;
        while (!C_i.isEmpty()){
            List<List<Integer>> new_Ci=new ArrayList<>();
            Set<String> setCi=new HashSet<>();
            System.out.println("C"+(i+2)+":");
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
            MemoryLogger.getInstance().checkMemory();
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
                candidatesCount++;
                List<Integer> itemset=new_Ci.get(j);
                int utility=calculateU2(itemset);
                //System.out.print(Joiner.on("&").join(itemset)+":");
                //System.out.println(utility);
                if (utility<max_utility&&utility!=0){
                    LUIs.add(new Itemset(itemset,utility));
                    myprint(itemset,utility);
                    patternCount++;
                } else if (utility==0) {
                    NUIs.add(itemset);
                }
            }
            C_i=new_Ci;
            i++;
        }
        return LUIs;
    }
    private void myprint(List<Integer> itemset, int uOfRoot) throws IOException {
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i < itemset.size(); i++) {
            if (i==itemset.size()-1){
                // System.out.print(itemset[i]+":");
                buffer.append(itemset.get(i)+":");
            }else {
                // System.out.print(itemset[i]+" ");
                buffer.append(itemset.get(i)+" ");
            }
        }
        //System.out.println(uOfRoot);
        buffer.append(uOfRoot);
        writer.write(buffer.toString());
        writer.newLine();
        writer.flush();
    }
    private boolean checkHasMinNUIset(List<Integer> newC) {
        for (int i = 0; i < NUIs.size(); i++) {
            if (newC.contains(NUIs.get(i))){
                return true;
            }
        }
        return false;
    }
    private int calculateU2(List<Integer> itemset) throws IOException {
        int utility = 0;
        for (int i = 0; i < DB.size(); i++) {
            int utilityOfTra=0;
            Map<Integer,Integer> mapItemToU=new HashMap<>();
            List<Integer> TransOne=DB.get(i);
            List<Integer> TransOneUtility=DB_utility.get(i);
            for (int j = 0; j < TransOne.size(); j++) {
                mapItemToU.put(TransOne.get(j),TransOneUtility.get(j));
            }
            if (mapItemToU.keySet().containsAll(itemset)){
                for (int j = 0; j < itemset.size(); j++) {
                    int item=itemset.get(j);
                    utilityOfTra+=mapItemToU.get(item);
                }
            }
            utility+=utilityOfTra;
        }
        return utility;

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
    public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Integer> patternlist) {
        System.out.println("runtime: "+(double)runtime/1000);
        runTimelist.add((double)runtime/1000);
        System.out.println("memory: "+MemoryLogger.getInstance().getMaxMemory());
        memorylist.add(MemoryLogger.getInstance().getMaxMemory());
        System.out.println("candidatesCount: "+candidatesCount);
        candidateslist.add(candidatesCount);
        System.out.println("patternCount: "+patternCount);
        patternlist.add(patternCount);

    }

}
