package Main.ContextClasses;

import Elements.*;
import Main.Compiler;

public class LinePrefix {
    public static Compiler compiler; //The compiler used to correclty interpret line prefixes

    public String name;
    public ScriptElement<?> value;
    public LinePrefix previousLinePrefix;
    public LinePrefix nextLinePrefix;

    public LinePrefix(){
        this.name="";
        this.value=null;
    }

    public LinePrefix(String name, ScriptElement<?> value){ //Only used to create the prefix of the defaultContext
        this.name=name;
        this.value=value;
    }

    public LinePrefix(LinePrefix previous){
        this.previousLinePrefix=previous;
        this.previousLinePrefix.nextLinePrefix=this;
    }

    /**
     * Unlinks this prefix from the other ones so that it can be removed.
     */
    public void Unlink(){
        this.previousLinePrefix.nextLinePrefix=null;
    }

    public void Set(String name, ScriptElement<?> value){
        this.name=name;
        this.value=value;
    }

    public String toString(){
        if(this.name==null){
            return "";
        }

        String output="";
        LinePrefix selectedPrefix=this;
        boolean execute=false;
        while(selectedPrefix.previousLinePrefix!=null){
            if(selectedPrefix.name.equals("as")){
                output=output+" as "+selectedPrefix.value.typeToString(LinePrefix.compiler);
                execute=true;
            }else if(selectedPrefix.name.equals("at")){
                output=output+" at "+selectedPrefix.value.typeToString(LinePrefix.compiler);
                execute=true;
            }else if(selectedPrefix.name.equals("old")){
                return selectedPrefix.value.typeToString(LinePrefix.compiler)+finalString(output, execute);
            }

            selectedPrefix=selectedPrefix.previousLinePrefix; //Selects the first line prefix
        }
        return finalString(output, execute);
    }

    private String finalString(String baseString, boolean execute){
        if(execute){
            return "execute"+baseString+" run ";
        }else{
            return baseString;
        }
    }
}
