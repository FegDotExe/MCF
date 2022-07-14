package Elements;

import Main.Compiler;

public interface ScriptElement<T> {
    public static <T> T stringToType(String string, Compiler compiler){
        return null;
    }
    public String typeToString(Compiler compiler);
}
