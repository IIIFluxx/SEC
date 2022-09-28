package texteditor;
import java.io.*;
import java.nio.charset.Charset;
/**
 * Classname: FileIO
 * Function: Provide all methods for File IO purposes - loading/saving files.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class FileIO
{

    public void save(File f, String text, String encoding) throws IOException {

        OutputStreamWriter osr = new OutputStreamWriter(
                new FileOutputStream(f.getAbsolutePath()),Charset.forName(encoding));

        try(BufferedWriter bw = new BufferedWriter(osr))
        {
            String line;
            bw.write(text);
        }
    }

    public String getFileContents(File file, String encoding) throws IOException {
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream(file.getPath()),encoding);

        String contents = "";
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try(BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null)
            {
                //System.out.println(line);
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            if(stringBuilder.length() > 1)
            {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }

        //return contents;
        return stringBuilder.toString();
    }
}
