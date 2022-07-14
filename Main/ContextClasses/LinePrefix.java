package Main.ContextClasses;

import java.util.LinkedHashMap;
import java.util.Set;

public class LinePrefix {
    public LinkedHashMap<String,String> hashtable;
    private LinePrefix previousLinePrefix;
    private LinePrefix nextLinePrefix;

    public LinePrefix(){
        hashtable=new LinkedHashMap<String,String>();
    }
    public LinePrefix(LinePrefix previousLinePrefix){
        this.previousLinePrefix=previousLinePrefix;
        previousLinePrefix.nextLinePrefix=this;

        hashtable=new LinkedHashMap<>();

        // Enumeration<String> en=previousLinePrefix.hashtable.keys();
        // while(en.hasMoreElements()){
        //     String key=en.nextElement();

        //     hashtable.put(key, previousLinePrefix.hashtable.get(key));
        // }
    }

    public void Add(String key, String value){
        hashtable.put(key, value);
    }

    /**
     * Unlinks this prefix from the other ones so that it can be removed.
     */
    public void Unlink(){
        this.previousLinePrefix.nextLinePrefix=null;
    }

    public String toString(){
        LinePrefix selectedPrefix=this;
        while(selectedPrefix.previousLinePrefix!=null){
            selectedPrefix=selectedPrefix.previousLinePrefix; //Selects the first line prefix
        }

        LinkedHashMap<String,String> completeHashtable=new LinkedHashMap<>(); //Compiles a complete hashtable, with all the prefixes in the order they were declared in
        while(selectedPrefix!=null){
            Set<String> keys=selectedPrefix.hashtable.keySet();
            // System.out.println(keys);
            for(String key: keys){
                if(completeHashtable.get(key)!=null){
                    completeHashtable.remove(key);
                }

                completeHashtable.put(key, selectedPrefix.hashtable.get(key));
            }

            selectedPrefix=selectedPrefix.nextLinePrefix;
        }

        String output="";
        String old="";
        boolean execute=false;

        Set<String> keys2 = completeHashtable.keySet();
        // System.out.println(keys2);
        for(String key: keys2){
            if(key.equals("old")){
                old=completeHashtable.get("old");
            }
            if(key.equals("as")){
                output=output+" as "+completeHashtable.get("as");
                execute=true;
            }
            if(key.equals("at")){
                output=output+" at "+completeHashtable.get("at");
                execute=true;
            }
        }

        if(execute){
            output=old+"execute"+output+" run ";
        }else{
            output=old+output;
        }

        return output;
    }

    /**
     * Tests if the prefix has a certain key
     * @param key The key to search for.
     * @return True if the prefix has the given key; false otherwise.
     */
    public boolean HasKey(String key){
        return hashtable.get(key)!=null;
    }
}
