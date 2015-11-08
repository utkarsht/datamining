package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

class Node
{
    String item;
    int freq;
    Node parent;
    Node link;
    ArrayList<Node> child;
    
    Node()
    {
        freq = 0;
        item = "";
        child = new ArrayList<Node>();
        parent = null;
    }

    Node(String item)
    {
        child = new ArrayList<Node>();
        freq = 1;
        this.item = item;
    }
}

public class FPTree 
{
    HashMap<String, Integer> Mcur;
    HashMap<String, Node> prev;
    prefix_table[] ptable;
    int total_items;
    int min_sup;

    File f;
    FileReader fr;
    BufferedReader br;
    
    class prefix_table
    {
        String item;
        int sup_count;
        Node link;

        prefix_table()
        {
            link = null;
        }

        prefix_table(String item, int sup_count)
        {
            this.item = item;
            this.sup_count = sup_count;
            link = null;
        }
    }

    public static void main(String[] args) throws Exception
    {   
        new FPTree().build();
    }

    void get_first_set(BufferedReader br) throws Exception
    {
        String tmp;
        while(true)
        {
            tmp = br.readLine();
            if(tmp == null)     break;
            for(String part : tmp.split(" "))
            {
                if(part.charAt(0) == 'I')
                {
                    if(Mcur.containsKey(part))
                        Mcur.put(part, Mcur.get(part) + 1);
                    else
                        Mcur.put(part, 1);
                }
            }
        }
        
        Set<Entry<String, Integer>> st = Mcur.entrySet();
        ArrayList<String> ar = new ArrayList<String>();
        
        for(Entry<String, Integer> e : st)
        {
            if(e.getValue() < min_sup)                                  //  Concurrent Modification
                ar.add(e.getKey());
        }
        
        for(String e : ar)
            Mcur.remove(e);          
    }

    void print_tree(Node t)
    {
        System.out.println(t.item + " >> " + t.freq);
        for (int i = 0; i < t.child.size(); i++) 
        {
            print_tree(t.child.get(i));    
        }
    }

    void insert_in_tree(Node t, ArrayList<String> lst, int idx)
    {
        if(idx < lst.size())
        {
            String item = lst.get(idx);
            for (int i = 0; i < t.child.size(); i++) 
            {
                if(t.child.get(i).item.equals(item))
                {
                    t.child.get(i).freq++;
                    insert_in_tree(t.child.get(i), lst, idx + 1);
                    return;
                }    
            }

            Node tmp = new Node(item);
            tmp.parent = t;
            
            if(!prev.containsKey(item))
            {
                for(int i = 0; i < total_items; i++)
                {
                    if(ptable[i].item.equals(item))
                    {
                        ptable[i].link = tmp;
                        break;
                    }
                }
            }
            else
            {
            	Node pre = prev.get(item);
            	pre.link = tmp;
            }

            prev.put(item, tmp);
            t.child.add(tmp);
            insert_in_tree(t.child.get(t.child.size() - 1), lst, idx + 1);      //  or send tmp
        }
    }

    Node build_tree(BufferedReader br) throws Exception
    {
        String tmp = "";
        fr = new FileReader(f);
        br = new BufferedReader(fr);

        int freq = 0;
        Node root = new Node();

        while(true)
        {
            tmp = br.readLine();
            if(tmp == null)     break;

            ArrayList<String> lst = new ArrayList<String>();

            for(String part : tmp.split(" "))
            {
                if(part.charAt(0) == 'I')
                {
                    lst.add(part);
                }
            }

            Collections.sort(lst, new Comparator<String>()
            {
                public int compare(String I1, String I2) 
                {
                    if(Mcur.get(I1) > Mcur.get(I2))
                        return -1;
                    else
                        return 1;
                }
                
            });
            insert_in_tree(root, lst, 0);
        }
        return root;
    }

    void processback(Node tmp)
    {
        while(tmp.parent != null)
        {
            System.out.println(tmp.item + " ");
            tmp = tmp.parent;
        }
        System.out.print("   ");
    }

    void build() throws Exception
    {
        f = new File("res/data.txt");
        fr = new FileReader(f);
        br = new BufferedReader(fr);

        Mcur = new HashMap<String, Integer>();
        prev = new HashMap<String, Node>();

        get_first_set(br);
        ptable = new prefix_table[Mcur.size()];

        Set<Entry<String, Integer>> st = Mcur.entrySet();
        total_items = 0;
        for (Entry<String, Integer> e : st) 
        {
            prefix_table pt = new prefix_table(e.getKey(), e.getValue());
            ptable[total_items++]  = pt;
        }

        Node root = build_tree(br);
//        print_tree(root);

        for (int i = total_items - 1; i >= 0; i--)
        {
        	System.out.println(ptable[i].item + "  " + ptable[i].sup_count + "   ");
        	
            Node tmp = ptable[i].link;
            while(tmp != null)			//	Each time we'll go for       same item but different paths
            {								
                processback(tmp);		//	For example for I3 loop will go 3 times  and path will back track upward   so process there only
                tmp = tmp.link;
            }
            System.out.println("");
        }
    }
}