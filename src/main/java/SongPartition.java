import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roma on 06.05.2017.
 */
public class SongPartition implements Serializable {

    public SongPartition(String name, List<String> parts) {
        this.name = name;
        this.parts = new ArrayList<>(parts);
    }
    String name;
    ArrayList<String> parts;

    public SongPartition(String name) {

        this.name = name;
        this.parts = (ArrayList<String>)get_parts(clear(name).toLowerCase().split("\\s+"));

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

        artist = artist.replaceAll("\\[.*?\\]|\\(.*?\\)|\\{.*?\\}"," ");
        artist = artist.replaceAll("[^a-zA-Zа-яА-я0-9їіё]"," ");
        if (artist.length() > 2) {
            artist = artist.replaceAll("([0-9]+)"," $1");
        }
        return artist.toLowerCase();

    }

}
