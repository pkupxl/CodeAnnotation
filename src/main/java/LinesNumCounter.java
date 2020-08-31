import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LinesNumCounter {
    public static void Count(File f ){
        if(f.isDirectory()){
            File[] fs = f.listFiles();
            for(int i=0;i<fs.length;++i){
                Count(fs[i]);
            }
            return;
        }

        int line = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                line++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(f.getName());
        System.out.println(line);
    }
    public static void main(String args[]){
        Count(new File("D:\\标注竞赛\\openGauss-server\\src\\gausskernel\\storage\\replication"));
    }
}
