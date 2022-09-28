package texteditor;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
/**
 * Classname: DateDataSource
 * Function: Provide all methods implemented by
 *          DataCollector interface specifically for DatePlugin
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class DateDataSource implements APIClasses.DataCollector
{
    private Locale locale;
    public DateDataSource(Locale inLoc)
    {
        this.locale = inLoc;
    }

    @Override
    public DataIntermed collect(DataIntermed dm)
    {
        ZonedDateTime time = ZonedDateTime.now(); // Time we want to format.
        DateTimeFormatter dtf = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(locale);

        String export = dtf.format(time);
        String newTextareaContents = new StringBuilder(dm.getText()).insert(dm.getCaretPos(), export).toString();
        dm.setText(newTextareaContents);
        return dm;
    }
}


