import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Roma on 09.05.2017.
 */
public class SongListeners  implements Serializable{

    String song;
    HashSet<String> uids;

    public SongListeners(String song, Set<String> uids) {
        this.song = new String(song);
        this.uids = new HashSet<String>(uids);
    }

}

