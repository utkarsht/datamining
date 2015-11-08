package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Freq_set 
{
	HashMap<HashSet<String>, Integer> Mcur;
	int min_sup;

	File f;
	FileReader fr;
	BufferedReader br;
	
	Freq_set() throws Exception
	{
		f = new File("res/data.txt");
		/*
			the formate of file is - 
			trans1 I1 I2 I5		//	1 space between items and transactions
			trans2 I4 I3
			....
		*/
		fr = new FileReader(f);
		br = new BufferedReader(fr);
	}
	
	public static void main(String[] args) throws Exception 
	{
		new Freq_set().solve();
	}
	
	void printMap(HashMap<HashSet<String>, Integer> M)
	{
		Set<Entry<HashSet<String>, Integer>> st = M.entrySet();
		for(Entry<HashSet<String>, Integer> e : st)
		{
			System.out.println(e.getKey() + "   " + e.getValue());
		}
		System.out.println();
	}
	
	void get_first_set(BufferedReader br) throws Exception
	{
		String tmp;
		while(true)
		{
			tmp = br.readLine();
			if(tmp == null)		break;
			for(String part : tmp.split(" "))
			{
				if(part.charAt(0) == 'I')
				{
					HashSet<String> hs = new HashSet<String>();
					hs.add(part);
					if(Mcur.containsKey(hs))
					{
						int freq = Mcur.get(hs);
						Mcur.remove(hs);
						Mcur.put(hs, freq + 1);
					}
					else
						Mcur.put(hs, 1);
				}
			}
		}
		printMap(Mcur);
        
		System.out.println("After removing element set having less support\n");
		Set<Entry<HashSet<String>, Integer>> st = Mcur.entrySet();
		ArrayList<HashSet<String>> ar = new ArrayList<HashSet<String>>();
		
		for(Entry<HashSet<String>, Integer> e : st)
		{
			if(e.getValue() < min_sup)									//	Concurrent Modification
				ar.add(e.getKey());
		}
		
		for(HashSet<String> e : ar)
			Mcur.remove(e);          
	}
	
	int get_freq(HashSet<String> hs, BufferedReader br) throws Exception 
	{
		String tmp = "";
		fr = new FileReader(f);
		br = new BufferedReader(fr);
		
		int freq = 0;
		while(true)
		{
			tmp = br.readLine();
			int cnt = 0;
			if(tmp == null)		break;
			
            HashSet<String> onetrans = new HashSet<String>();
			for(String part : tmp.split(" "))
			{
				if(part.charAt(0) == 'I')
				{
                    onetrans.add(part);
				}
			}
            for(String s : hs)
            {
                    if(onetrans.contains(s))
                        cnt++; 
            }
            if(cnt >= hs.size())
                freq++;
		}            
		return freq;
	}
	
	HashMap<HashSet<String>, Integer> joinAndPrune(HashMap<HashSet<String>, Integer> Mp, BufferedReader br) throws Exception
	{
		HashMap<HashSet<String>, Integer> Mc = new HashMap<HashSet<String>, Integer>();
		HashMap<HashSet<String>, Integer> Mstore = new HashMap<HashSet<String>, Integer>();
		Set<HashSet<String>> it1 = Mp.keySet();
		Set<HashSet<String>> it2 = it1;
		
		for(HashSet<String> p1 : it1)
		{
			for(HashSet<String> p2 : it2)
			{
				HashSet<String> p3 = new HashSet<String>();
				for(String s1 : p1)
				{
					for(String s2 : p2)
					{
						p3.add(s1);
						p3.add(s2);
					}
				}
				
				if(p3.size() == p1.size() + 1)
				{
					//	check for in_frequent_subset
					
					int i, j;
					boolean no = true;
					for(i = 0; i < p3.size(); i++)
					{
						HashSet<String> hs = new HashSet<String>();
						j = 0;
						
						for(String s3 : p3)
						{
							if(i != j)
								hs.add(s3);
							j++;
						}
						if(!Mp.containsKey(hs))
						{
							no = false;
							break;
						}
					}
					
					if(no)
					{
						int freq = get_freq(p3, br);
                        Mstore.put(p3, freq);
						if(freq >= min_sup)
							Mc.put(p3, freq);
					}
				}
			}
		}
        if(Mc.size() != 0)
            printMap(Mstore);
		return Mc;
	}
	
    void getconfidence(HashMap<HashSet<String>, Integer> M, BufferedReader br) throws Exception
    {
        Set<HashSet<String>> freq = M.keySet();
        for(HashSet<String> e : freq)
        {
            ArrayList<String> copy = new ArrayList<String>(e);    
            System.out.println("Confidence for " + copy);
            for(int i = 1; i < Math.pow(2, copy.size()) - 1; i++)
            {
                int k = i;
                HashSet<String> lf, rt;
	            lf = new HashSet<String>();
	            rt = new HashSet<String>();
                for(int j = 0; j < copy.size(); j++)
                {
                   if(k % 2 == 1)
                       lf.add(copy.get(j)); 
                   else
                       rt.add(copy.get(j));
                   k /= 2;
                }
                
                int l = get_freq(e, br);
                int r = get_freq(lf, br);
                System.out.println(lf + " -> " + rt + "  " + l + "/" + r + " = " + (double)l/r);
            }
            System.out.println("");
        }
    }
        
	void solve() throws Exception
	{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Min Support\n");
        min_sup = in.nextInt();
        Mcur = new HashMap<HashSet<String>, Integer>();
        HashMap<HashSet<String>, Integer> save;
                
		get_first_set(br);
		printMap(Mcur);
		int phase = 1;
                
		while(true)
		{
            System.out.println("Phase " + phase++);
            save = Mcur;
			Mcur = joinAndPrune(Mcur, br);
			
            if(Mcur.size() == 0)
				break;
            System.out.println("After removing element set having less support\n");
			printMap(Mcur);
		}
                
        System.out.println("Frequent item set are :");
        printMap(save);
        getconfidence(save, br);
	}
}