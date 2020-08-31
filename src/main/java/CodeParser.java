import com.alibaba.fastjson.JSON;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class CodeParser {
    public static String getComment(IASTFunctionDefinition function,List<IASTComment> comments){
        String result = "";
        int fstart = function.getFileLocation().getStartingLineNumber();
        for(IASTComment comment:comments){
            int cend = comment.getFileLocation().getEndingLineNumber();
            if(fstart == cend+1){
                return comment.getRawSignature();
            }
        }
        return result;
    }

    public static HashMap<String, HashMap<String,String>> getCodeParsingResult(MyVisitor visitor){
        HashMap<String, HashMap<String,String>> result = new HashMap<>();
        List<IASTFunctionDefinition> functions = visitor.getFunctions();
        List<IASTComment> comments = visitor.getComments();

        for(IASTFunctionDefinition function:functions){
            String name = function.getDeclarator().getName().toString();
            String body = function.getRawSignature();
            String comment = getComment(function,comments);
            String fileName = function.getFileLocation().getFileName();
            HashMap<String,String>info = new HashMap<>();
            info.put("name",name);
            info.put("body",body);
            info.put("comment",comment);
            info.put("filename",fileName);
            result.put(name,info);
        }
        return result;
    }

    public static void ParseFile(String sourceFile){
        ASTTranslationUnitCore astTranslationUnitCore = new ASTTranslationUnitCore();
        IASTTranslationUnit astTranslationUnit = astTranslationUnitCore.parse(sourceFile, ParserLanguage.CPP, false, false);
        System.out.println(astTranslationUnit.getFilePath());
        MyVisitor visitor = new MyVisitor();
        astTranslationUnit.accept(visitor);
        HashMap<String, HashMap<String,String>> result = getCodeParsingResult(visitor);
        String mapString = JSON.toJSONString(result, true);
        String filename = sourceFile.substring(sourceFile.lastIndexOf("\\")+1);
        ReadWriteUtil.writeFile(filename+".json",mapString);
    }

    public static void Parse(File f){
        if(f.isDirectory()){
            for(File file:f.listFiles()){
                Parse(file);
            }
        }else if(f.getName().endsWith("cpp")){
            //System.out.println(f.getAbsolutePath());
            ParseFile(f.getAbsolutePath());
        }
    }
    public static void main(String args[]) throws Exception{
        String sourceDir = "D:\\标注竞赛\\openGauss-server\\src\\gausskernel\\storage\\replication";
        Parse(new File(sourceDir));
    }
}
