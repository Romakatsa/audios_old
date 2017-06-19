import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

/**
 * Created by Roma on 05.05.2017.
 */
public class ListParser {

    public static LinkedList<SongPartition> parse(String listString,int capacity) {

        LinkedList<SongPartition> audioList = new LinkedList<>();
        int pos_start = listString.indexOf("[");
        int pos_end = 0;
        int pos_open_quote = pos_start;
        int pos_close_quote = pos_start;
        String name = null;
        while (pos_start != -1) {

           pos_end = findNotInQuoteBracket(listString,pos_start);
           for (int i = 0; i < 5; i++) {
               pos_open_quote = listString.indexOf("\"",pos_open_quote+1);
           }
           pos_close_quote = listString.indexOf("\"",pos_open_quote+1);
           if (pos_close_quote - pos_open_quote <= 20) {
               name =  unescapeHtml4(listString.substring(pos_open_quote+1,pos_close_quote).toLowerCase());
               audioList.add(new SongPartition(name,get_parts(clear(name).split("\\s+"))));
           }
           pos_start = listString.indexOf("[",pos_end);
           pos_open_quote = pos_start;
        }

        return audioList;

    }

    public static int findNotInQuoteBracket(String listString, int start_pos) {

        int close_quote = start_pos;
        int open_quote = start_pos;
        int bracket_pos = start_pos;
        boolean continue_search = true;

        bracket_pos = listString.indexOf("]", start_pos);

        while(continue_search) {

            open_quote = listString.indexOf("\"",close_quote+1);
            close_quote = listString.indexOf("\"",open_quote+1);

            if (open_quote == -1) {
                continue_search = false;
                break;
            }

            if (bracket_pos < close_quote) {
                if (bracket_pos > open_quote) {
                    bracket_pos = listString.indexOf("]", close_quote);
                }
                else {
                   return bracket_pos;
                }
            }
        }

        return bracket_pos;
    }


    public static ArrayList<Pair> stream_parse(String listString,int capacity, String uid) {

        ArrayList<Pair> audioList = new ArrayList<>(capacity);
        int pos_start = listString.indexOf("[");
        int pos_end = 0;
        int pos_open_quote = pos_start;
        int pos_close_quote = pos_start;
        String name = null;
        while (pos_start != -1) {

            pos_end = findNotInQuoteBracket(listString,pos_start);
            for (int i = 0; i < 5; i++) {
                pos_open_quote = listString.indexOf("\"",pos_open_quote+1);
            }
            pos_close_quote = listString.indexOf("\"",pos_open_quote+1);
            if (pos_close_quote - pos_open_quote <= 20) {
                name =  unescapeHtml4(listString.substring(pos_open_quote+1,pos_close_quote));
                Pair newPair = new Pair(uid,name);
                if (newPair.song.parts.size() > 0)
                    audioList.add(newPair);
            }
            pos_start = listString.indexOf("[",pos_end);
            pos_open_quote = pos_start;
        }

        return audioList;

    }





    public static List<String> get_parts(String[] words) {
        ArrayList<String> parts = new ArrayList<>();

        for (String word : words) {
            if (word.length() > 2) {
                for (int k = 0; k < word.length() - 2; k++) {
                    parts.add(word.substring(k, k + 3));
                }
            } else if (word.length() >= 2) {
                parts.add(word);
            }
        }
        return parts;
    }

    public static String clear(String artist) {

        artist.replaceAll("\\[.*?\\]|\\(.*?\\)|\\{.*?\\}"," ");
        artist.replaceAll("[^a-zA-Zа-яА-я0-9їіё]"," ");
        return artist.toLowerCase();

    }

}
