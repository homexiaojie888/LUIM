import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainTestLUMAlgorithm_saveToFile {

	static List<Double> runTime=new ArrayList<>();
	static List<Double> memory=new ArrayList<>();
	static List<Long> candidates=new ArrayList<>();
	static List<Integer> pattern=new ArrayList<>();

	public static void main(String [] arg) throws IOException{
//		String input1 ="BMSPHM.txt";
//		String input2 ="ChessPHM.txt";
//		String input3 = "FoodmartPHM.txt";
//		String input4 = "MushroomPHM.txt";
//		String input5 = "RetailPHM.txt";
//		String input6 = "accidentsPHM.txt";
		String input7 = "example.txt";
		String input8 = "DB_Utility2.txt";
		String finalInput=input8;

		String input = fileToPath(finalInput);
		String output = ".//outputNew.txt";
		int[] max_utility = new int[]{10};


		for (int i = 0; i < max_utility.length; i++) {
			MemoryLogger.getInstance().reset();
			LowUtilityMing lowUtilityMing = new LowUtilityMing();
			MemoryLogger.getInstance().checkMemory();
			lowUtilityMing.runAlgorithm(input, max_utility[i], output);
			MemoryLogger.getInstance().checkMemory();
			lowUtilityMing.printStats(runTime,memory,candidates,pattern);
		}
		OutputExp(max_utility,finalInput);

	}
	private static void OutputExp(int[] max_utility, String input) throws IOException {
		String experimentFile = ".//newexp"+input;
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(experimentFile));
		bufferedWriter.write("maxUtil: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(max_utility[i]+"");
			}else {
				bufferedWriter.write(max_utility[i]+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("runTime: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(runTime.get(i)+"");
			}else {
				bufferedWriter.write(runTime.get(i)+",");
			}
		}
		bufferedWriter.newLine();
		bufferedWriter.write("memory: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(memory.get(i)+"");
			}else {
				bufferedWriter.write(memory.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("candidates: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(candidates.get(i)+"");
			}else {
				bufferedWriter.write(candidates.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("patterns: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(pattern.get(i)+"");
			}else {
				bufferedWriter.write(pattern.get(i)+",");
			}

		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestLUMAlgorithm_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
