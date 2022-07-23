package Main;
import java.io.Console;

public class MCF {
    public static void main(String[] args) {
        Console console=System.console();

        if(console!=null){
            String path=null;
            if(args.length>0){
                path=args[0];
            }else{
                path="script.mcf";
            }

            boolean logPrefix=false;
            boolean newDirPrompt=true;
            int logDepth=2;

            for(int i=1; i<args.length;i++){
                String argument=args[i];
                if(argument.equals("-logPrefix")){
                    logPrefix=true;
                }else if(argument.equals("-noNewDirPrompt")){
                    newDirPrompt=false;
                }else if(argument.equals("-logDepth")){
                    if((i+1)<args.length){
                        try{
                            int depth=Integer.parseInt(args[i+1]);
                            logDepth=depth;
                        }catch(NumberFormatException exc){
                            System.out.println("The given depth value (\""+args[i+1]+"\") is not valid.");
                            System.exit(1);
                        }
                    }
                }
            }

            Compiler compiler=new Compiler(path,logDepth,logPrefix,newDirPrompt);
            compiler.compile();
        }else{
            Compiler compiler=new Compiler("script.mcf",2,false,false);
            compiler.compile();
        }
    }
}
