package Elements;
import Main.Compiler;

public class Score implements ScriptElement<Score> {
    public String name;

    public Score(String name){
        this.name=name;
    }

    public String toString(){
        return name;
    }

    public static Score stringToType(String string, Compiler compiler){
        return new Score(string);
    }
    public String typeToString(Compiler compiler){
        return this.name;
    }
}
