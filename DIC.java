package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

//  For reference - http://www2.cs.uregina.ca/~dbd/cs831/notes/itemsets/DIC.html

class Pair
{
    int freq;
    int trans;
    Pair(int freq, int trans)
    {
        this.freq = freq;
        this.trans = trans;
    }
}

public class DIC 
{
    int min_sup = 2;
    int curTrans;
    
    File f;
    FileReader fr;
    BufferedReader br;
    
    ArrayList<Entry<HashSet<String>, Pair>> temp;
    Set<Entry<HashSet<String>, Pair>> st;
    HashMap<HashSet<String>, Pair> dashCircle, dashSquare, solidCircle, solidSquare;

    public static void main(String[] args)  throws Exception
    {
        new DIC().solve();
    }
    
    void process(HashSet<String> hs, HashMap<HashSet<String>, Pair> ds)
    {          
        st = ds.entrySet();
        temp = new ArrayList<Entry<HashSet<String>, Pair>>();

        for(Entry<HashSet<String>, Pair> e : st)
        {       
            HashSet<String> eK = e.getKey();
            if(hs.containsAll(eK))
            {
                temp.add(e);
            }
        }
        
        for(Entry<HashSet<String>, Pair> e : temp)
        {
            Pair eP = e.getValue();
            Pair t = new Pair(eP.freq + 1, eP.trans);
            ds.put(e.getKey(), t);
        }            
    }

    boolean noWhere(HashSet<String> e)
    {
    	if(!dashCircle.containsKey(e) && !dashSquare.containsKey(e) && !solidSquare.containsKey(e) && !solidCircle.containsKey(e))	
    		return true;
    	return false;
    }
    
    HashSet<String> val(String tmp)
    {
        HashSet<String> hs = new HashSet<String>();
        for(String part : tmp.split(" "))
        {
            if(part.charAt(0) == 'I')
               hs.add(part);
        }
        return hs;
    }

    void printDs(HashMap<HashSet<String>, Pair> ds)
    {
        st = ds.entrySet();
        System.out.println(">>");
        for(Entry<HashSet<String>, Pair> e : st)
        {
            System.out.println(e.getKey() + " : " + e.getValue().freq + " " + e.getValue().trans); 
        }
        System.out.println();
    }

    void reopen()
    {
    	f = new File("res/data.txt");
		try {   fr = new FileReader(f);   }       catch (Exception e) {	}
        br = new BufferedReader(fr);
    }
    
    void solve() throws Exception
    {
        int M = 3;
        boolean no = false;
        int totalTransaction = 9;
        reopen();
                
        dashCircle = new HashMap<HashSet<String>, Pair>();
        solidCircle = new HashMap<HashSet<String>, Pair>();
        dashSquare = new HashMap<HashSet<String>, Pair>();
        solidSquare = new HashMap<HashSet<String>, Pair>();
        
        String tmp;
        ArrayList<String> items = new ArrayList<String>();
        for(int i = 1; i <= 5; i++)
        {
            tmp = "I" + i;
            HashSet<String> hs = new HashSet<String>();
            hs.add(tmp);
            dashCircle.put(hs, new Pair(0, 0));
            items.add(tmp);
        }
        
        curTrans = 0;
        while(dashCircle.size() != 0 || dashSquare.size() != 0)
        {
        //    for (int i = 0; i < M; i++) 
            {
                curTrans++;
                tmp = br.readLine();    
                if(tmp == null)
                {
                	reopen(); 
                	tmp = br.readLine();
                }

                HashSet<String> itemSet = val(tmp);    
                process(itemSet, dashCircle);
                process(itemSet, dashSquare);

                st = dashCircle.entrySet();
                temp = new ArrayList<Entry<HashSet<String>, Pair>>();

                for(Entry<HashSet<String>, Pair> e : st)
                {       
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();

                    if(eP.freq >= min_sup)                                    //      Concurrent
                    {
                        temp.add(e);
                    }
                }
                for(Entry<HashSet<String>, Pair> e : temp)
                {
                    dashCircle.remove(e.getKey());
                    dashSquare.put(e.getKey(), e.getValue());
                }
                    
                st = dashSquare.entrySet();
                ArrayList<HashSet<String>> tempH = new ArrayList<HashSet<String>>();

                for(Entry<HashSet<String>, Pair> e : st)
                {
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();
                    for(String item : items)
                    {   
                        if(eK.contains(item))
                            continue;
                        
                        HashSet<String> tmpitem = new HashSet<String>();
                        tmpitem.addAll(eK);
                        tmpitem.add(item);
                        
                        no = true;
                        Iterator it = tmpitem.iterator();
                        while(it.hasNext())
                        {
                            String str = (String)it.next();
                            HashSet<String> withoutstr = new HashSet<String>();
                            for(String s : tmpitem)
                            {
                                if(!s.equals(str))
                                    withoutstr.add(s);
                            }
                            if(!solidSquare.containsKey(withoutstr) && !dashSquare.containsKey(withoutstr))
                            {
                                no = false;
                                break;
                            }   
                        }
                        
                        if(no)
                        {
                            tempH.add(tmpitem);
                            // dashCircle.put(tmpitem, new Pair(0, curTrans));
                        }
                    }
                }
                for(HashSet<String> e : tempH)
                {
                	if(noWhere(e))
                		dashCircle.put(e, new Pair(0, curTrans));
                }

                st = dashCircle.entrySet();
                temp = new ArrayList<Entry<HashSet<String>, Pair>>();
                
                for(Entry<HashSet<String>, Pair> e : st)
                {
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();

                    int transactionSeen = eP.trans;
                    if(curTrans - transactionSeen >= totalTransaction)
                    {
                        temp.add(e);
                    }
                }
                for(Entry<HashSet<String>, Pair> e : temp)
                {
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();
                    dashCircle.remove(eK);
                    solidCircle.put(eK, eP);
                }
                
                st = dashSquare.entrySet();
                temp = new ArrayList<Entry<HashSet<String>, Pair>>();

                for(Entry<HashSet<String>, Pair> e : st)
                {
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();

                    int transactionSeen = eP.trans;
                    if(curTrans - transactionSeen >= totalTransaction)
                    {
                        temp.add(e);
                    }
                }
                for(Entry<HashSet<String>, Pair> e : temp)
                {
                    Pair eP = e.getValue();
                    HashSet<String> eK = e.getKey();
                    dashSquare.remove(eK);
                    solidSquare.put(eK, eP);
                }

//                printDs(dashCircle);
//                printDs(dashSquare);
//                printDs(solidCircle);
//                printDs(solidSquare);    
            }
        }

        st = solidSquare.entrySet();
        for(Entry<HashSet<String>, Pair> e : st) 
        {
            System.out.println(e.getKey() + " : " + e.getValue().freq);
        }
        br.close();
    }
}