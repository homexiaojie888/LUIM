/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
 *
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 *
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUIMiner {

    public long startTimestamp = 0;

    public long endTimestamp = 0;
    public int huiCount =0;

    public long candadateCount=0;
    //Map<Integer, Integer> mapItemToTWU;
    Map<Integer, UtilityList> mapItemToUtilityList = new HashMap<>();
    BufferedWriter writer = null;
    private int joinCount;

    /** buffer for storing the current itemset that is mined when performing mining
     * the idea is to always reuse the same buffer to reduce memory usage. */


    class Pair{
        int item = 0;
        int utility = 0;
    }

    public void HUIMiner() {
    }

    /**
     * Run the algorithm
     * @param input the input file path
     * @param output the output file path
     * @param minUtility the minimum utility threshold
     * @throws IOException exception if error while writing the file
     */
    public void runAlgorithm(String input, String output, int minUtility) throws IOException {
        MemoryLogger.getInstance().reset();

        startTimestamp = System.currentTimeMillis();

        writer = new BufferedWriter(new FileWriter(output));

        List<UtilityList> listOfUtilityLists = new ArrayList<>();

        BufferedReader myInput = null;
        String thisLine;
        try {
            myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                String split[] = thisLine.split(":");
                String items[] = split[0].split(" ");
                //int transactionUtility = Integer.parseInt(split[1]);
                for(int i=0; i <items.length; i++){
                    Integer item = Integer.parseInt(items[i]);
                    if (!mapItemToUtilityList.keySet().contains(item)){
                        UtilityList uList = new UtilityList();
                        uList.getItemSet().add(item);
                        mapItemToUtilityList.put(item, uList);
                        listOfUtilityLists.add(uList);
                    }
                }
            }
        } catch (Exception e) {
            // catches exception if error while reading the input file
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }

        Collections.sort(listOfUtilityLists, new Comparator<UtilityList>(){
            public int compare(UtilityList o1, UtilityList o2) {
                return compareItems(o1.getItemSet().get(0), o2.getItemSet().get(0));
            }
        } );

        // SECOND DATABASE PASS TO CONSTRUCT THE UTILITY LISTS
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
            int tid =0;
            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                String split[] = thisLine.split(":");
                String items[] = split[0].split(" ");
                String utilityValues[] = split[2].split(" ");
                int remainingUtility =0;
                List<Pair> revisedTransaction = new ArrayList<Pair>();
                for(int i=0; i <items.length; i++){
                    Pair pair = new Pair();
                    pair.item = Integer.parseInt(items[i]);
                    pair.utility = Integer.parseInt(utilityValues[i]);
                    revisedTransaction.add(pair);
                    remainingUtility += pair.utility;

                }
                Collections.sort(revisedTransaction, new Comparator<Pair>(){
                    public int compare(Pair o1, Pair o2) {
                        return compareItems(o1.item, o2.item);
                    }});
                for(Pair pair : revisedTransaction){
                    remainingUtility = remainingUtility - pair.utility;
                    UtilityList utilityListOfItem = mapItemToUtilityList.get(pair.item);
                    Element element = new Element(tid, pair.utility, remainingUtility);
                    utilityListOfItem.addElement(element);
                }
                tid++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        MemoryLogger.getInstance().checkMemory();
        huiMiner(null, listOfUtilityLists, minUtility);
        MemoryLogger.getInstance().checkMemory();
        writer.close();
        endTimestamp = System.currentTimeMillis();
    }

    private int compareItems(int item1, int item2) {
        return item1-item2;
    }

    /**
     * This is the recursive method to find all high utility itemsets. It writes
     * the itemsets to the output file.
     * @param pUL This is the Utility List of the prefix. Initially, it is empty.
     * @param ULs The utility lists corresponding to each extension of the prefix.
     * @param minUtility The minUtility threshold.
     * @throws IOException
     */
    private void huiMiner(UtilityList pUL, List<UtilityList> ULs, int minUtility)
            throws IOException {

        // For each extension X of prefix P
        for(int i=0; i< ULs.size(); i++){
            candadateCount++;

            UtilityList X = ULs.get(i);
            System.out.println("verify current node:"+X.getItemSet());
            if(X.sumIutil > 0 && X.sumIutil <= minUtility){
                writeOut(X.getItemSet(), X.sumIutil);
            }
            List<UtilityList> exULs = new ArrayList<>();
            for(int j=i+1; j < ULs.size(); j++){
                UtilityList Y = ULs.get(j);
                UtilityList Pxy=construct(pUL, X, Y);
                joinCount++;
                if (!Pxy.elements.isEmpty()){
                    exULs.add(Pxy);
                }
            }
            if(X.sumIutil + X.sumRutil >= minUtility){
                System.out.println("verify extensions of current node:"+X.getItemSet());
                huiMiner(X, exULs, minUtility);
            }else{
                System.out.println("from current node, all extensions are LUIM: "+X.getItemSet());
                exploreAll(X, exULs, minUtility);
            }
        }
    }

    private void exploreAll(UtilityList pUL, List<UtilityList> ULs, int minUtility)
            throws IOException {

        // For each extension X of prefix P
        for(int i=0; i< ULs.size(); i++){
            candadateCount++;
            UtilityList X = ULs.get(i);
            System.out.println("LUIM: "+ X.getItemSet());
            writeOut(X.getItemSet(), X.sumIutil);

            List<UtilityList> exULs = new ArrayList<>();
            for(int j=i+1; j < ULs.size(); j++){
                UtilityList Y = ULs.get(j);
                UtilityList Pxy=construct(pUL, X, Y);
                joinCount++;
                if (!Pxy.elements.isEmpty()){
                    exULs.add(Pxy);
                }
            }
            if(!exULs.isEmpty()){
                exploreAll(X, exULs, minUtility);
            }

        }
    }
    private UtilityList construct(UtilityList P, UtilityList px, UtilityList py) {
        UtilityList pxyUL = new UtilityList();

        List<Integer> PxyItemset=pxyUL.getItemSet();
        PxyItemset.addAll(px.getItemSet());
        PxyItemset.add(py.getItemSet().get(py.getItemSet().size()-1));

        List<Element> elementListPx=px.elements;
        List<Element> elementListPy=py.elements;

        for (int i = 0,j = 0; i < elementListPx.size()&&j < elementListPy.size(); ) {
            Element ex=elementListPx.get(i);
            Element ey=elementListPy.get(j);
            if (ex.tid==ey.tid){
                if(P == null){
                    Element newElement = new Element(ex.tid,ex.iutils + ey.iutils, ey.rutils);
                    pxyUL.addElement(newElement);

                }else{
                    Element e = findElementWithTID(P, ex.tid);
                    if(e != null){
                        Element newElement = new Element(ex.tid,ex.iutils + ey.iutils - e.iutils, ey.rutils);
                        pxyUL.addElement(newElement);
                    }else{
                        Element newElement = new Element(ex.tid,ex.iutils + ey.iutils, ey.rutils);
                        pxyUL.addElement(newElement);
                    }
                }
                i++;j++;
            }else if (ex.tid>ey.tid){
                j++;
            }else if (ex.tid<ey.tid){
                i++;
            }

        }
        return pxyUL;
    }

    /**
     * Do a binary search to find the element with a given tid in a utility list
     * @param ulist the utility list
     * @param tid  the tid
     * @return  the element or null if none has the tid.
     */
    private Element findElementWithTID(UtilityList ulist, int tid){
        List<Element> list = ulist.elements;

        // perform a binary search to check if  the subset appears in  level k-1.
        int first = 0;
        int last = list.size() - 1;

        // the binary search
        while( first <= last )
        {
            int middle = ( first + last ) >>> 1; // divide by 2

            if(list.get(middle).tid < tid){
                first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
            }
            else if(list.get(middle).tid > tid){
                last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
            }
            else{
                return list.get(middle);
            }
        }
        return null;
    }

    /**
     * Method to write a high utility itemset to the output file.
     * @param utility the utility of the prefix concatenated with the item
     */
    private void writeOut(List<Integer> itemset, long utility) throws IOException {
        huiCount++;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < itemset.size(); i++) {
            buffer.append(itemset.get(i));
            if (i!=itemset.size()-1){
                buffer.append(' ');
            }
        }
        // append the utility value
        buffer.append(":");
        buffer.append(utility);
        // write to file
        writer.write(buffer.toString());
        writer.newLine();
    }

    public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Integer> patternlist) {

        runTimelist.add((double)(endTimestamp - startTimestamp)/1000);
        memorylist.add(MemoryLogger.getInstance().getMaxMemory());
        candidateslist.add(candadateCount);
        patternlist.add(huiCount);

    }
}