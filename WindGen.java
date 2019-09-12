import java.util.*;
import java.util.*;
import java.io.*;

public class WindGen {
	public static void main(String[] args) {
      Scanner s = new Scanner(System.in);
		int numberOfTimeSteps = Integer.parseInt(s.next());
		int width = Integer.parseInt(s.next());
		int height = Integer.parseInt(s.next());
      
      try{
         FileWriter fileWriter = new FileWriter("Test.txt");
   		PrintWriter printWriter = new PrintWriter(fileWriter);
         
         //System.out.println(String.format("%d %d %d", numberOfTimeSteps, width, height));
         printWriter.printf("%d %d %d%n", numberOfTimeSteps, width, height);
   		for (int time = 0; time < numberOfTimeSteps; time++) {
   			StringBuilder row = new StringBuilder();
   			for (int x = 0; x < width; x++) {
   				for (int y = 0; y < height; y++) {
   					for (int i = 0; i < 3; i++)
   						row.append(String.format("%.6f ", Math.random()));
   				}
   			}
   			//System.out.println(row.toString());
            printWriter.println(row.toString());
   		}
         
         printWriter.close();
      
      }catch (IOException e){
			 System.out.println("Unable to open output file ");
				e.printStackTrace();
		 }

		
	}
}
