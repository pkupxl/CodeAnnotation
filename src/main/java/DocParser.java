import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.HashMap;

public class DocParser {

    public static String getElementName(String line){
        String s= line.split("<")[0];
        String t = s.replace("#"," ").trim();
        t = t.replace("\\","");
        return t;
    }


    public static HashMap<String,String> ParseFile(File file){
        HashMap<String,String> info = new HashMap<>();

        String s =ReadWriteUtil.readFile(file.getAbsolutePath());
        String lines[] = s.split("\n");
        String title = getElementName(lines[0]);
        StringBuilder titleDescription = new StringBuilder("");
        int index = 1;
        while(index<lines.length && !lines[index].startsWith("#")){
            titleDescription.append(lines[index]);
            titleDescription.append("\n");
            index++;
        }
        info.put("title",title);
        info.put("titleDescription",titleDescription.toString());
        //System.out.println("title:"+title);
        //System.out.println("titleDescription"+titleDescription.toString());

        for(int i=index;i<lines.length;++i){
            String subtitle = getElementName(lines[i]);
            StringBuilder subtitleDescription = new StringBuilder("");
            int k = i+1;
            while(k<lines.length && !lines[k].startsWith("#")){
                subtitleDescription.append(lines[k]);
                subtitleDescription.append("\n");
                k++;
            }
            info.put(subtitle,subtitleDescription.toString());
            //System.out.println("------------------------------------");
            //System.out.println(subtitle);
            //System.out.println(subtitleDescription.toString());
            //System.out.println("------------------------------------");
            i = k-1;
        }
        return info;
    }

    public static void main(String args[]) throws  Exception{
        String path = "D:\\标注竞赛\\docs\\content\\zh\\docs\\Developerguide";
        File f = new File(path);
        File[]fs = f.listFiles();
        HashMap<String, HashMap<String,String>> result = new HashMap<>() ;
        for(File file: fs){
            if(file.isDirectory())continue;
            System.out.println(file.getAbsolutePath());
            HashMap<String,String> info = ParseFile(file);
            result.put(file.getName(),info);
        }
        String mapString = JSON.toJSONString(result, true);
        ReadWriteUtil.writeFile("Developerguide.json",mapString);
    }
}
