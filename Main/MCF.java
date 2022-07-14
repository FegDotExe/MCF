package Main;

public class MCF {
    public static void main(String[] args) {
        String path=null;
        if(args.length>0){
            path=args[0];
        }else{
            path="script.mcf";
        }

        Compiler compiler=new Compiler(path,3);
        compiler.compile();
    }
}
