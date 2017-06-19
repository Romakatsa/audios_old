import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roma on 06.05.2017.
 */
public class Choise {

    public ArrayList<SongPartition> songs = null;
    public int[] counts = null;

    public Choise(List<SongPartition> songs, int[] counts) {
        this.songs = new ArrayList<>(songs);
        this.counts = counts;
    }

}
