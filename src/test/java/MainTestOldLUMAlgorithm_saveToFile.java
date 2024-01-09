import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainTestOldLUMAlgorithm_saveToFile {
	static List<Double> runTime=new ArrayList<>();
	static List<Double> memory=new ArrayList<>();
	static List<Long> candidates=new ArrayList<>();
	static List<Integer> pattern=new ArrayList<>();

	public static void main(String [] arg) throws IOException{

//		String input1 ="BMSPHM_1W.txt";
//		String input2 ="ChessPHM.txt";
//		String input3 = "FoodmartPHM.txt";
//		String input4 = "MushroomPHM.txt";
//		String input5 = "RetailPHM.txt";
//		String input6 = "BMSPHM.txt";
		String input7 = "example.txt";
		String input8 = "DB_Utility2.txt";

		String finalInput=input7;
		String input = fileToPath(finalInput);

		String output = ".//outputOld.txt";

		int[] max_utility_bms = new int[]{10,15,20,25,30,35};
		int[] max_utility_chess = new int[]{100,200,300,400,500,600};
		int[] max_utility_foodmart = new int[]{10,20,30,40,50,60};
		int[] max_utility_mushroom = new int[]{100,200,300,400,500,600};
		int[] max_utility_retail = new int[]{10,20,30,40,50,60};
		int[] max_utility = new int[]{10};
		for (int i = 0; i < max_utility.length; i++) {
			MemoryLogger.getInstance().reset();
			MemoryLogger.getInstance().checkMemory();
			OldLUIM oldLUIM=new OldLUIM();
			oldLUIM.runAlgorithm(input,output,max_utility[i]);

			MemoryLogger.getInstance().checkMemory();
			oldLUIM.printStats(runTime,memory,candidates,pattern);
		}
		OutputExp(max_utility,finalInput);

	}
	private static void OutputExp(int[] max_utility, String input) throws IOException {
		String experimentFile = ".//oldexp"+input;
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(experimentFile));
		bufferedWriter.write("maxUtil:");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(max_utility[i]);
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
		URL url = MainTestOldLUMAlgorithm_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
