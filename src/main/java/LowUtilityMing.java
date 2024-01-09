import com.google.common.base.Joiner;

import java.io.*;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.store;

public class LowUtilityMing {
    Set<int[]> preSet=new HashSet<>();
    Generator3 generator;
    int max_utility;
    BufferedWriter writer=null;
    //String exteFile="src/main/resources/extension.txt";
    long runtime ;

    int patternCount;
    long candidatesCount;


    public void runAlgorithm(String input, int max_utility,String output) throws IOException {
        this.max_utility=max_utility;

        long startTime=System.currentTimeMillis();

        loadFile(input);
        //sortTrans(input);
        String delPrex=null;
        List<int[]> res=genContain(input);

        writer = new BufferedWriter(new FileWriter(output));


        for (int i = 0; i < res.size(); i++) {
           LUM(delPrex, res.get(i));
           preSet.add(res.get(i));
        }
        runtime=System.currentTimeMillis()-startTime;

        writer.close();
    }

    public void LUM(String delPre,int[] itemset) throws IOException {

        for (int[] maxItemset:preSet) {
            List<Integer> l1 = Arrays.stream(maxItemset).boxed().collect(Collectors.toList());
            List<Integer> l2 = Arrays.stream(itemset).boxed().collect(Collectors.toList());
            if ( l1.containsAll(l2)){
                return;
            }
        }

        candidatesCount++;
        ULB ulb=generator.getUBofItemset(itemset);
        //获取当前项集的utility
        int uOfRoot=ulb.getUofItemset();
        if (uOfRoot<=max_utility){
            myprint(itemset,uOfRoot);
            patternCount++;
        }
        //不需要考虑一项集的扩展
        if (itemset.length==1){
            return;
        }

        int lbOfExD;
        if (delPre!=null){
            String[] delPreArr2=delPre.split("&");
            int lastItem=Integer.valueOf(delPreArr2[delPreArr2.length-1]);
            //获取深度剪枝的下届，若>maxutil,则不考虑扩展
            lbOfExD=ulb.getLBofExD(lastItem);
        }else {
            //获取深度剪枝的下届，若>maxutil,则不考虑扩展
            lbOfExD=ulb.getMinUOfItem();
        }
        MemoryLogger.getInstance().checkMemory();
        //要不要放到上方的里面(不放到里面因为如果本身不满足条件的话，扩展有可能满足条件，放到上面会忽略扩展)
        //考虑其扩展

        //深度剪枝
        if (lbOfExD<=max_utility){
            if (delPre!=null){
                //只能删除大于lastitem的元素
                String[] delPreArr=delPre.split("&");
                int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
                for (int i = 0; i < itemset.length; i++) {
                    if (itemset[i]<lastItem){
                        continue;
                    }
                    //获取宽度剪枝下届，若下届>maxutil,本身及扩展都剪去
                    int lbOfExB=ulb.getLBofExW(itemset[i]);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                           // mycheck(delPre+"&"+itemset[i],extendsion,exteFile);
                            LUM(delPre+"&"+itemset[i],extendsion);
                        }

                    }//else{
                        //myprintExtension(extendsion,lbOfExB,false);
                    //}
                }

            }else //delpre==null
            {
                for (int i = 0; i < itemset.length; i++) {
                    //获取宽度剪枝下届，若下届>maxutil,本身及扩展都剪去
                    int lbOfExB=ulb.getLBofExW(itemset[i]);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                          //  mycheck(String.valueOf(itemset[i]),extendsion,exteFile);
                            LUM(String.valueOf(itemset[i]),extendsion);
                        }

                    }
//                    else {
//                        myprintExtension(extendsion,lbOfExB,false);
//                    }
                }
            }


        }
//        else{
//            myprintExtension(itemset,lbOfExD,true);
//        }

    }

    private void myprint(int[] itemset, int uOfRoot) throws IOException {
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i < itemset.length; i++) {
            if (i==itemset.length-1){
               // System.out.print(itemset[i]+":");
                buffer.append(itemset[i]+":");
            }else {
               // System.out.print(itemset[i]+" ");
                buffer.append(itemset[i]+" ");
            }
        }
        //System.out.println(uOfRoot);
        buffer.append(uOfRoot);
        writer.write(buffer.toString());
        writer.newLine();
        writer.flush();
    }


    private void myprintExtension(int[] itemset, int Lowbound,boolean isDepthPrune) {
        for (int i = 0; i < itemset.length; i++) {
            if (i==itemset.length-1){
                System.out.print(itemset[i]+": lower bound = ");
            }else {
                System.out.print(itemset[i]+" ");
            }
        }
        if (isDepthPrune){
            System.out.println(Lowbound+" 深度剪枝，扩展被过滤");
        }else {
            System.out.println(Lowbound+" 宽度剪枝，扩展被过滤");
        }

    }
    private int[] delOneOfItemset(int[] itemset,int index, int item) {
//        if (itemset.length==1){
//            return null;
//        }
        int[] res=new int[itemset.length-1];
        if (itemset[index]==item){
        //int i = 0, j = 0;
        for (int i = 0, j = 0; i < itemset.length&&j < res.length; i++) {
            if (i==index){
                continue;
            }else {
                res[j++]=itemset[i];
            }
        }}else {
            System.out.println("del item error");
        }
        return res;
    }

    public void loadFile(String path) throws IOException {
        Set<Integer> setOfItems=new TreeSet<>();
        Map<Integer,String[]> mapTidToItems = new LinkedHashMap<>();
        Map<Integer,String[]> mapTidToUtilities=new LinkedHashMap<>();
        int tidCount=0;
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                tidCount++;
                String[] partions=thisLine.split(":");
               // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                int[] itemsInt=new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    itemsInt[i]=Integer.valueOf(items[i]);
                    setOfItems.add(itemsInt[i]);
                }
                mapTidToItems.put(tidCount,items);
                String[] utilities = partions[2].split(" ");
                mapTidToUtilities.put(tidCount,utilities);
            }
        } catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        }finally {
            if(myInput != null){
                // close the file
                myInput.close();
            }
        }
        generator=new Generator3(setOfItems,tidCount);
        generator.setGenerator(mapTidToItems,mapTidToUtilities);
    }

    public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Integer> patternlist) {

        runTimelist.add((double)runtime/1000);
        memorylist.add(MemoryLogger.getInstance().getMaxMemory());
        candidateslist.add(candidatesCount);
        patternlist.add(patternCount);

    }

    private void sortTrans(String input) throws IOException {

        Set<String> trans=new HashSet<>();
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                String[] partions = thisLine.split(":");
                // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                String itemsStr= Joiner.on("&").join(items);
                trans.add(itemsStr);
            }
        } catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                // close the file
                myInput.close();
            }
        }

    }

    List<int[]> genContain(String input) throws IOException {
        List<List<Integer>> trans=new LinkedList<>();
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                String[] partions = thisLine.split(":");
                // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                List<Integer> tranOne=new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    tranOne.add(Integer.valueOf(items[i]));
                }
//                int[] itemsInt=new int[items.length];
//                for (int i = 0; i < items.length; i++) {
//                    itemsInt[i]=Integer.valueOf(items[i]);
//                }
                trans.add(tranOne);
            }
        } catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                // close the file
                myInput.close();
            }
        }
        Collections.sort(trans, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o1.size()-o2.size();
            }
        });
        //去重
        Set<String> stringSet=new LinkedHashSet<>();
        for (int i = 0; i < trans.size(); i++) {
            List<Integer> temp=trans.get(i);
            stringSet.add(Joiner.on("&").join(temp));
        }
        trans.clear();

        List<int[]> res=new ArrayList<>();
        List<String> stringList=new ArrayList<>(stringSet);

        while (!stringList.isEmpty()){
            //lastSize=trans.size();
            for (int i = stringList.size()-2; i >= 0 ; i--) {

                String[] items=stringList.get(stringList.size()-1).split("&");
                List<Integer> itemsList1=Arrays.stream(Arrays.stream(items).mapToInt(Integer::parseInt).toArray()).boxed().collect(Collectors.toList());

                String[] items2=stringList.get(i).split("&");
                List<Integer> itemsList2=Arrays.stream(Arrays.stream(items2).mapToInt(Integer::parseInt).toArray()).boxed().collect(Collectors.toList());

                if (itemsList1.containsAll(itemsList2)){
                    stringList.remove(i);
                    //i++;
                }
            }

            res.add(Arrays.stream(stringList.get(stringList.size()-1).split("&")).mapToInt(Integer::parseInt).toArray());
            stringList.remove(stringList.size()-1);
        }
        return  res;
    }

}
