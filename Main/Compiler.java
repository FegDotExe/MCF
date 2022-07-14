package Main;

import java.io.*;
import java.util.regex.*;

import Elements.*;

import java.util.Hashtable;

interface StringFunction{
    void run(String input);
}
interface CustomFunction{
    void run(String input, Compiler compiler);
}

public class Compiler{
    public static Compiler compiler; //The compiler which is being used
    private BufferedReader br;
    private int logDepth=0; //Log depth: 0=errors, 1=warnings, 2=info, 3=verbose

    public int lineInt=0; //The current line
    public int format=10; //Format of the datapack
    public String description="Made with MCF!"; //Description of the datapack
    public String name="MCF";
    public String destination="";

    public Context defaultContext;
    public Context currentContext;

    public Hashtable<String, CustomFunction> customFunctions;

    private String literalCharacter="[^\\s\\.\\+\\-\\=<>]";

    private boolean baseCreated=false; //Wether the base of the datapack has been created or not

    public Compiler(String path,int logDepth){
        Compiler.compiler=this;
        this.logDepth=logDepth;
        this.defaultContext=new Context(destination+name,"main", "main");
        currentContext=defaultContext;
        customFunctions=new Hashtable<String,CustomFunction>();

        try{
            br=new BufferedReader(new FileReader(path));
        }catch(FileNotFoundException exc){
            System.out.println("The given file does not exist.");
            System.exit(1);
        }

        importFunctions();

    }

    private void importFunctions(){
        //Internal functions
        customFunctions.put("file", (str, cmp)->{ //Change file of actual context
            Matcher name=Pattern.compile("\"(.*)\"").matcher(str);
            if(name.matches()){
                cmp.addContext();
                currentContext.file=name.group(1);
            }else{
                cmp.throwException("The given filename is not valid (it probably is not between quotes).");
            }
        });
        customFunctions.put("endcontext", (str,cmp)->{ //Switch to previous context, if there was any
            cmp.log("Returning to the previous context.",4);
            cmp.previousContext();
        });
        customFunctions.put("folder", (str, cmp)->{ //Change folder of actual context
            Matcher name=Pattern.compile("\"(.*)\"").matcher(str);
            if(name.matches()){
                cmp.addContext();
                currentContext.file=name.group(1);
            }else{
                cmp.throwException("The given folder is not valid (it probably is not between quotes).");
            }
        });
        customFunctions.put("at", (str, cmp)->{//Execute all the context at a certain entity
            Entity entity=Entity.stringToType(str, cmp);
            if(entity==null){
                cmp.throwException("The given value is not an entity.");
            }

            cmp.addContext();
            cmp.currentContext.linePrefix.Add("at", entity.typeToString(cmp));
        });
        customFunctions.put("as", (str, cmp)->{//Execute all the context at a certain entity
            Entity entity=Entity.stringToType(str, cmp);
            if(entity==null){
                cmp.throwException("The given value is not an entity.");
            }

            cmp.addContext();
            cmp.currentContext.linePrefix.Add("as", entity.typeToString(cmp));
        });
    }

    public void compile(){
        String line;
        try{
            while((line=br.readLine())!=null){
                lineInt++;
                if(!line.equals("")){
                    compileLine(line);
                }
            }
        }catch(IOException exc){
            System.out.println("An IO exception has occoured.");
            System.exit(1);
        }
        File path=new File(destination+name);
        log("Compiled successfully; output directory: \""+path.getAbsolutePath()+"\".",0);
    }

    public void compileLine(String line){
        log("Compiling line "+lineInt+": \""+line+"\"",4);
        
        line=substituteLiterals(line);

        log("Decoded line "+lineInt+" to: \""+line+"\"",4);

        Matcher meta=Pattern.compile("\\s*#").matcher(line);
        if(meta.find()){
            boolean successful=false;
            successful=metaCompiler("\\s*#\\s*name\\s*=\\s*\"(.*)\"", (input)->{
                log("Pack name set to "+input+".",3);
                this.name=input;
            }, line);
            if(successful){
                return;
            }
            successful=metaCompiler("\\s*#\\s*format\\s*=\\s*([^\\s]*)", (input)->{
                try{
                    this.format=Integer.parseInt(input);
                    log("Pack format set to "+input+".",3);
                }catch(NumberFormatException exc){
                    throwException("The given value cannot be converted to an integer");
                }
            }, line);
            if(successful){
                return;
            }
            successful=metaCompiler("\\s*#\\s*destination\\s*=\\s*\"(.*)\"", (input)->{
                if(!(input.charAt(input.length()-1)=='/') || !(input.charAt(input.length()-1)=='\\')){
                    input=input+"/";
                }
                mkdir(input);
                log("Changing destination path to \""+input+"\".",3);
                destination=input;
            },line);
            if(successful){
                return;
            }
            successful=metaCompiler("\\s*#\\s*description\\s*=\\s*\"(.*)\"", (input)->{
                log("Changing description to \""+input+"\".",3);
                description=input;
            },line);
            if(successful){
                return;
            }

            throwException("Unrecognized meta statement.");;
        }else if(!baseCreated){
            createBase(); //This function shall not return, as it corresponds to the first non-meta instruction met
        }
        Matcher endContext=Pattern.compile("\\s*\\}\\s*\\z").matcher(line);
        if(endContext.find()){
            customFunctions.get("endcontext").run("", this);
            return;
        }

        Matcher customFunction=Pattern.compile("\\s*@([^\\s]*)\\((.*)\\)").matcher(line); //@<function>(<arguments>)
        if(customFunction.find()){
            if(customFunctions.get(customFunction.group(1))==null){
                throwException("The function \""+customFunction.group(1)+"\" does not exist.");
            }
            customFunctions.get(customFunction.group(1)).run(customFunction.group(2),this);
            return;
        }

        Matcher scoreDeclaration=Pattern.compile("\\s*([^\\s]*)\\s*=\\s*score\\s*([^\\s]*)").matcher(line); //<name>=score <scoreType>
        if(scoreDeclaration.find()){
            contextAppend("scoreboard objectives add "+scoreDeclaration.group(1)+" "+scoreDeclaration.group(2)+"\n");
            setBaseLiteral(scoreDeclaration.group(1), (ScriptElement<?>)new Score(scoreDeclaration.group(1)));
            return;
        }

        Matcher entityDeclaration=Pattern.compile("\\s*([^\\s]*)\\s*=\\s*entity\\s*(\"?.*\"?)\\z").matcher(line); //<name>=entity "<entityString>"
        if(entityDeclaration.find()){
            log("Declared new entity: "+entityDeclaration.group(2),4);
            Entity thisEntity=Entity.stringToType(entityDeclaration.group(2), compiler);
            getContext().literals.put(entityDeclaration.group(1),thisEntity);
            return;
        }

        Matcher literalDeclaration=Pattern.compile("\\s*([^\\s]*)\\s*=\\s*literal\\s*\"(.*)\"\\z").matcher(line); //<name>=literal "<literalString>"
        if(literalDeclaration.find()){
            getContext().literals.put(literalDeclaration.group(1),LiteralString.stringToType(literalDeclaration.group(2), this));
            return;
        }
        
        Matcher intDeclaration=Pattern.compile("\\s*([^\\s]*)\\s*=\\s*int\\s*(\\d*)\\z").matcher(line); //<name>=int <value>
        if(intDeclaration.find()){
            getContext().literals.put(intDeclaration.group(1), LiteralInt.stringToType(intDeclaration.group(2),this));
            return;
        }

        Matcher assignation=Pattern.compile("\\s*("+literalCharacter+"*)\\.(["+literalCharacter+"]*)\\s*=\\s*(.*)\\z").matcher(line); //<selector>.<value>=<selector>.<value>
        if(assignation.find()){
            //Entity and score
            Entity literal1=Entity.stringToType(assignation.group(1),this);
            Object literal2=getLiteral(assignation.group(2));

            if(literal1!=null && literal2 instanceof Score){
                assignEntityScore(literal1,(Score)literal2,assignation.group(3));
            }else{
                throwException("The value assignation is not valid.");
            }

            return;
        }

        Matcher sum=Pattern.compile("\\s*([^\\s\\.\\+\\-]*)\\.([^\\s\\.\\+\\-]*)\\s*\\+=\\s*(.*)\\z").matcher(line); //<selector>.<value>+=<selector>.<value>
        if(sum.find()){
            Object literal1=getLiteral(sum.group(1));
            Object literal2=getLiteral(sum.group(2));

            if(literal1 instanceof Entity && literal2 instanceof Score){
                sumEntityScore((Entity)literal1, (Score)literal2, sum.group(3));
            }else{
                throwException("The value assignation is not valid.");
            }

            return;
        }

        Matcher lineMatcher=Pattern.compile("\\s*(.*)").matcher(line);
        if(lineMatcher.matches()){
            contextAppend(lineMatcher.group(1)+"\n"); //Append line if it has not been interpreted in other ways
        }
    }

    private boolean metaCompiler(String pattern, StringFunction stringFunction, String line){ //Returns true if successful, false otherwise
        Matcher matcher=Pattern.compile(pattern).matcher(line);
        if(matcher.matches()){
            String match=matcher.group(1);
            stringFunction.run(match);
            return true;
        }
        return false;
    }

    private String substituteLiterals(String line){
        while(true){
            Matcher literal=Pattern.compile("<("+literalCharacter+"*?)>").matcher(line);
            if(literal.find()){
                //Another substitution
                int i=0;
                while(true){
                    log("Replacing literal "+literal.group(1),4);
                    Context context=getContext(i);
                    if(context==null){
                        throwException("The literal \""+literal.group(1)+"\" has not been declared.");
                    }
                    ScriptElement<?> value=context.literals.get(literal.group(1));
                    if(value!=null){
                        line=line.replaceAll("(<"+literal.group(1)+">)", value.typeToString(this));
                        log("Replaced literal "+literal.group(1)+" with "+value.typeToString(this),4);
                        break;
                    }else{
                        i++;
                    }
                }
            }else{
                break;
            }
        }

        return line;
    }

    private void createBase(){
        log("Creating base directory.",2);

        deldir(destination+name);

        mkdir(destination+name+"/data/main/functions/");

        this.defaultContext=new Context(destination+name, "main", defaultContext.file);
        this.currentContext=this.defaultContext;

        try{
            BufferedWriter output=new BufferedWriter(new FileWriter(destination+name+"/pack.mcmeta"));
            output.write("{\"pack\":{\"pack_format\":"+format+",\"description\":\""+description+"\"}}");
            output.close();
        }catch(IOException exc){
            throwException("An IO exception has occoured.");
        }

        baseCreated=true;
    }

    //Context
    public Context getContext(){ //Get the most recent context
        return getContext(0);
    }
    public Context getContext(int index){ //Get a context of choice. Context indexes go from most recent (0) to least recent.
        Context output=currentContext;

        // log(currentContext.toString(),0);

        for(int i=0; i<index;i++){
            if(output.previousContext==null){ //FIXME: this should return null when context is too big.
                return output;
            }
            output=output.previousContext;
        }

        return output;
    }

    /**
     * Add a new context which is the exact copy of the actual one. The context is also automatically selected as the current one.
     */
    public void addContext(){
        currentContext=new Context(getContext(), this);
    }
    /**
     * Set the current context to the previous one.
     */
    public void previousContext(){
        if(currentContext.previousContext==null){
            return;
        }
        this.currentContext.linePrefix.Unlink();
        this.currentContext=currentContext.previousContext;
    }

    /**
     * Get the object corresponding to the given name
     * @param name The name of the literal
     * @return The literal object
     */
    public ScriptElement<?> getLiteral(String name){
        return getLiteral(name,true);
    }
    /**
     * Get the object corresponding to the given name
     * @param name The name of the literal
     * @param exception Wether to throw an exception or not if the literal is not found; if set to false and the literal is not found, null is returned.
     * @return The literal object
     */
    public ScriptElement<?> getLiteral(String name,boolean exception){
        int i=0;
        while(true){
            Context context=getContext(i);
            ScriptElement<?> value=context.literals.get(name);

            if(value!=null){
                return value;
            }else if(context==defaultContext && value==null){
                if(exception){
                    throwException("The literal \""+name+"\" has not been declared.");
                }else{
                    return null;
                }
            }else{
                i++;
            }
        }
    }
    public void setLiteral(String name, ScriptElement<?> value){
        getContext().literals.put(name, value);
    }
    public void setBaseLiteral(String name,ScriptElement<?> value){
        defaultContext.literals.put(name,value);
    }

    //Logging
    public void throwException(String line){
        log("Compiler error at line "+lineInt+": "+line,0);
        System.exit(1);
    }

    public void log(String message, int depth){
        if(depth<=this.logDepth){
            String prefix="";
            switch(depth){
                case 0:
                    prefix="EXC: ";
                    break;
                case 1:
                    prefix="WRN: ";
                    break;
                case 2:
                    prefix="INF: ";
                    break;
                case 3:
                    prefix="VRB: ";
                    break;
                case 4:
                    prefix="XVB: ";
                    break;
            }
            System.out.println(prefix+message);
        }
    }

    //IO
    public void mkdir(String path){
        File file=new File(path);
        if(!file.isDirectory()){
            log("Creating directory \""+path+"\".",3);
            file.mkdirs();
        }
    }

    public static void deldir(String path){
        File index=new File(path);
        String[] entries=index.list();

        if(entries!=null){
            for(String s:entries){
                File file=new File(index.getPath()+"/"+s);
                if(!file.isDirectory()){
                    file.delete();
                }else{
                    deldir(index.getPath()+"/"+s);
                }
            }
        }

        index.delete();
    }

    public void contextAppend(String string){
        try{
            Context context=getContext();
            mkdir(context.getPath());
            BufferedWriter output=new BufferedWriter(new FileWriter(context.getFilePath(),true));



            output.write(context.linePrefix+string);
            output.close();
        }catch(IOException exc){
            throwException("An IO exception has occoured.");
        }
    }

    //Useful
    public Hashtable<String,Object> listToHash(String string){ //Returns an hashtable from an entity list, formatted like this: [tag="value",otherTag=otherValue]
        String tag="";
        String value="";

        Hashtable<String,Object> output=new Hashtable<>();

        boolean valueSelected=false; //False if tag is being selected; true if it's value that's being selected
        int parenthesys=0; //The amount of parenthesys the text is currently in between

        for(int i=0; i<string.length();i++){
            char character=string.charAt(i);
            if(character=='{'){
                parenthesys++;
                if(!valueSelected){
                    tag=tag+character;
                }else{
                    value=value+character;
                }
            }else if(character=='}'){
                parenthesys--;
                if(!valueSelected){
                    tag=tag+character;
                }else{
                    value=value+character;
                }
            }else if(parenthesys==0 && character=='='){
                valueSelected=true;
            }else if((valueSelected||(!valueSelected && character!=' ')) && (parenthesys!=0 || (character!='=' && character!=','))){
                if(!valueSelected){
                    tag=tag+character;
                }else{
                    value=value+character;
                }
            }
            if(parenthesys==0 && character==','){
                valueSelected=false;

                log("Adding \""+tag+"="+value+"\" to a new HashTable.",4);

                output.put(tag, value);
                tag="";
                value="";
            }else if(parenthesys==0 && i==string.length()-1){
                log("Adding \""+tag+"="+value+"\" to a new HashTable.",4);

                output.put(tag, value);
            }
        }

        return output;
    }

    //Assignation
    /**
     * Assign a value to an entity's score.
     * @param entity The entity whose score is to be set.
     * @param score The score to be set.
     * @param value The value which will be set.
     */
    private void assignEntityScore(Entity entity, Score score, String value){
        Matcher digits=Pattern.compile("(^\\d\\d*)").matcher(value);
        if(digits.find()){
            contextAppend("scoreboard players set "+entity.typeToString(this)+" "+score.typeToString(this)+" "+value+"\n");
            return;
        }

        Matcher entityScore=Pattern.compile("([^\\s\\.\\+\\-]*)\\.([^\\s\\.\\+\\-]*)").matcher(value);
        if(entityScore.find()){
            Entity literal1=Entity.stringToType(entityScore.group(1),this);
            ScriptElement<?> literal2=getLiteral(entityScore.group(2));

            if(literal1!=null && literal2 instanceof Score){
                scoreboardOperation(entity.typeToString(this), score.typeToString(this), "=", literal1.typeToString(this), literal2.typeToString(this));
                
                return;
            }
        }

        throwException("The given value could not be assigned to the given literal.");
    }
    /**
     * Compiles a sum of scores
     * @param entity The entity whose score will be summed
     * @param score The score which will be summed
     * @param value A string of the value to be summed
     */
    private void sumEntityScore(Entity entity, Score score, String value){
        Matcher digits=Pattern.compile("(^\\d\\d*)").matcher(value);
        if(digits.find()){
            contextAppend("scoreboard players add "+entity.typeToString(this)+" "+score.typeToString(this)+" "+value+"\n");
            return;
        }

        Matcher entityScore=Pattern.compile("([^\\s\\.\\+\\-]*)\\.([^\\s\\.\\+\\-]*)").matcher(value);
        if(entityScore.find()){
            ScriptElement<?> literal1=getLiteral(entityScore.group(1));
            ScriptElement<?> literal2=getLiteral(entityScore.group(2));

            if(literal1 instanceof Entity && literal2 instanceof Score){
                scoreboardOperation(entity.typeToString(this), score.typeToString(this), "+=", literal1.typeToString(this), literal2.typeToString(this));
                return;
            }
        }

        throwException("The given value could not be assigned to the given literal.");
    }

    /**
     * Append a scoreboard operation to the context
     * @param selector1 the first entity selector
     * @param score1 the score of the first entity
     * @param operation the operation in a string format (such as "=")
     * @param selector2 the second entity selector
     * @param score2 the score of the second entity
     */
    private void scoreboardOperation(String selector1, String score1, String operation, String selector2, String score2){
        contextAppend("scoreboard players operation "+selector1+" "+score1+" "+operation+" "+selector2+" "+score2+"\n");
    }
}