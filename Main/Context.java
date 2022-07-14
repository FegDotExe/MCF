package Main;

import java.util.Hashtable;

import Main.ContextClasses.LinePrefix;

public class Context {
    public Context previousContext;
    public Compiler compiler;

    public String dpPath;
    public String folder;
    public String subfolder="functions/";
    public String file;
    public String extension=".mcfunction";
    public Hashtable<String,Object> literals;
    public LinePrefix linePrefix;

    public Context(String dpPath, String folder, String file){
        this(dpPath,folder,"functions/",file,".mcfunction");
    }
    public Context(String dpPath, String folder, String subfolder, String file, String extension){
        if(dpPath.length()!=0 && (dpPath.charAt(dpPath.length()-1)!='/' && dpPath.charAt(dpPath.length()-1)!='\\')){
            dpPath=dpPath+"/";
        }
        this.dpPath=dpPath;

        if(folder.charAt(folder.length()-1)!='/' && folder.charAt(folder.length()-1)!='\\'){
            folder=folder+"/";
        }
        this.folder=folder;

        if(subfolder.charAt(subfolder.length()-1)!='/' && subfolder.charAt(subfolder.length()-1)!='\\'){
            subfolder=subfolder+"/";
        }
        this.subfolder=subfolder;

        if(extension.charAt(0)!='.'){
            extension="."+extension;
        }

        this.file=file;

        literals=new Hashtable<>();

        this.linePrefix=new LinePrefix();
    }

    /**
     * Create a new Context based on the given one. All the default values are left as null; an empty literals Hashtable is created; a linePrefix derived from previousContext.linePrefix is created.
     * @param previousContext The context this new context will be based on.
     * @param compiler The compiler this context will use.
     */
    public Context(Context previousContext, Compiler compiler){
        this.previousContext=previousContext;
        this.compiler=compiler;
        this.literals=new Hashtable<>();
        this.linePrefix=new LinePrefix(previousContext.linePrefix);
    }

    public String getFilePath(){
        return this.getDpPath()+"data/"+this.getFolder()+getSubfolder()+this.getFile()+getExtension();
    }
    public String getPath(){
        return getDpPath()+"data/"+this.getFolder()+getSubfolder();
    }

    /**
     * Get the datapack path of this context.
     * @return Returns the datapack path of the current context
     */
    public String getDpPath(){
        if(this.dpPath==null){
            if(this.previousContext==null){
                this.compiler.throwException("The given datapack path is not valid.");
            }
            return this.previousContext.getDpPath();
        }else{
            return this.dpPath;
        }
    }
    /**
     * Get the folder of this context.
     * @return Returns the folder of the current context
     */
    public String getFolder(){
        if(this.folder==null){
            if(this.previousContext==null){
                this.compiler.throwException("The given folder is not valid.");
            }
            return this.previousContext.getFolder();
        }else{
            return this.folder;
        }
    }
    /**
     * Get the subfolder of this context.
     * @return Returns the subfolder of the current context
     */
    public String getSubfolder(){
        if(this.subfolder==null){
            if(this.previousContext==null){
                this.compiler.throwException("The given subfolder is not valid.");
            }
            return this.previousContext.getSubfolder();
        }else{
            return this.subfolder;
        }
    }
    /**
     * Get the file of this context.
     * @return Returns the file of the current context
     */
    public String getFile(){
        if(this.file==null){
            if(this.previousContext==null){
                this.compiler.throwException("The given file is not valid.");
            }
            return this.previousContext.getFile();
        }else{
            return this.file;
        }
    }
    /**
     * Get the extension of this context.
     * @return Returns the extension of the current context
     */
    public String getExtension(){
        if(this.extension==null){
            if(this.previousContext==null){
                this.compiler.throwException("The given extension is not valid.");
            }
            return this.previousContext.getExtension();
        }else{
            return this.extension;
        }
    }
    

    public String toString(){
        return previousContext+"->{"+"dpPath:"+dpPath+",folder:"+folder+",subfolder:"+subfolder+",file:"+file+",extension:"+extension+",literals:"+literals+",linePrefix:"+linePrefix+"}";
    }
}
