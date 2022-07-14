package Elements;

import Main.Compiler;

public class LiteralInt implements ScriptElement<LiteralInt> {
    public int number;

    public LiteralInt(String string){
        this.number=Integer.parseInt(string);
    }

    public static LiteralInt stringToType(String string, Compiler compiler){
        return new LiteralInt(string);
    }

    public String typeToString(Compiler compiler){
        return this.number+"";
    }
}