package Elements;

import Main.Compiler;

public class LiteralString implements ScriptElement<LiteralString> {
    public String string;

    public LiteralString(String string){
        this.string=string;
    }

    public static LiteralString stringToType(String string, Compiler compiler){
        return new LiteralString(string);
    }

    public String typeToString(Compiler compiler){
        return this.string;
    }
}
