package apriori;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Create_data 
{
	public static void main(String[] args) throws IOException 
	{
		File f = new File("res/data.txt");
		FileWriter fr = new FileWriter(f);
		BufferedWriter br = new BufferedWriter(fr);
		
		Random rand = new Random();
		Scanner in = new Scanner(System.in);
		int trans = in.nextInt();
		
		for(int i = 0; i < trans; i++)
		{
			String tmp = "Trans" + i;
			int item = rand.nextInt(trans / 2) + 1;
			for(int j = 0; j < item; j++)
			{
				tmp += " I" + rand.nextInt(2*trans);
			}
			System.out.println(tmp);
			br.write(tmp);
			br.newLine();
		}
		
		br.close();
	}
}
