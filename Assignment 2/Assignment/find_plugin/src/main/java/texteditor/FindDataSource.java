package texteditor;
import java.text.Normalizer;
import java.util.Locale;
/**
 * Classname: FindDataSource
 * Function: Provide all methods implemented by
 *          DataCollector interface specifically for FindPlugin
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class FindDataSource implements APIClasses.DataCollector {
    private Locale locale;
    public FindDataSource(Locale inLoc) {
        this.locale = inLoc;
    }

    @Override
    public DataIntermed collect(DataIntermed dm)
    {
        if(dm.getSearchTerm() != null)
        {
            //System.out.println("Caret pos in Find collect(): " + dm.getCaretPos());
            String normText1 = Normalizer.normalize(dm.getText(), Normalizer.Form.NFKC);
            String normText2 = Normalizer.normalize(dm.getSearchTerm(), Normalizer.Form.NFKC);

            if(normText1.indexOf(normText2, dm.getCaretPos()) !=  -1)
            {
                int startIndex = normText1.indexOf(normText2, dm.getCaretPos());
                int endIndex = startIndex + normText2.length();
                dm.setStartIdx(startIndex);
                dm.setEndIdx(endIndex);
            }
        }
        return dm;
    }
}
