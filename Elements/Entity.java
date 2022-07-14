package Elements;

import java.util.Hashtable;
import java.util.regex.*;

import Main.Compiler;

import java.util.Enumeration;

@SuppressWarnings("Cast")
public class Entity implements ScriptElement<Entity> {
    public char target;
    public Hashtable<String,Object> tags;

    public Entity(String arguments, Main.Compiler cmp){
        Matcher base=Pattern.compile("@(.)\\[(.*)\\]").matcher(arguments);
        if(base.matches()){
            this.target=base.group(1).charAt(0);
            this.tags=cmp.listToHash(base.group(2));
            return;
        }
        base=Pattern.compile("@(.)").matcher(arguments);
        if(base.matches()){
            this.target=base.group(1).charAt(0);
            return;
        }
        
        cmp.throwException("The entity is declared uncorrectly.");
    }
    private Entity(){}

    public String toString() throws RuntimeException{
        throw new RuntimeException();

        // return typeToString(this, null);
    }

    /**
     * Convert a string to an entity.
     * @param entityString The string to be converted.
     * @param compiler The compiler used to convert the string.
     * @return The Entity derived from the given string or null if the string cannot be converted to an entity.
     */
    public static Entity stringToType(String entityString, Compiler compiler){
        Matcher base=Pattern.compile("\"@(.)\\[(.*)\\]\"").matcher(entityString);
        if(base.matches()){
            Entity entity=new Entity();

            entity.target=base.group(1).charAt(0);
            entity.tags=compiler.listToHash(base.group(2));
            return entity;
        }
        base=Pattern.compile("\"@(.)\"").matcher(entityString);
        if(base.matches()){
            Entity entity=new Entity();

            entity.target=base.group(1).charAt(0);
            return entity;
        }
        Object literal=compiler.getLiteral(entityString,false);
        if(literal!=null && literal instanceof Entity){
            return (Entity)literal;
        }
        
        return null;
    }

    public String typeToString(Compiler compiler){
        String output="";
        
        if(tags!=null){
            output="@"+target+"[";
        
            Enumeration<String> enumeration=tags.keys();
            while(enumeration.hasMoreElements()){
                String key=enumeration.nextElement();
                Object object=tags.get(key);

                output=output+key+"="+object;
                if(enumeration.hasMoreElements()){
                    output=output+",";
                }
            }
            output=output+"]";
        }else{
            output="@"+target;
        }

        return output;
    }
}
