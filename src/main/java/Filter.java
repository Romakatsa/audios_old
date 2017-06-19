import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map.*;

/**
 * Created by Roma on 03.05.2017.
 */
public class Filter {


    public static LinkedList<Choise> group(int totalUsers,ArrayList<LinkedList<SongPartition>> lists, int totalSongs) {

        LinkedList<Choise> choises = new LinkedList<>();
        ArrayList<SongPartition> similar = new ArrayList<>();
        int[] listeners = new int[totalUsers];

        SongPartition cur_song = null;
        SongPartition song_j = null;
        int match = 0;
        //List<String> parts_i = new ArrayList<>(15);
        //List<String> parts_j = new ArrayList<>(15);

        ArrayList<ListIterator<SongPartition>> iterators = new ArrayList<ListIterator<SongPartition>>(lists.size());
        for (List list: lists) {
            iterators.add(list.listIterator());
        }

        for (int i = 0; i < lists.size(); i++) {
            System.out.println("list_no " + i);
            while (!lists.get(i).isEmpty()) {

                cur_song = lists.get(i).get(0);
                similar.add(cur_song);
                listeners[i]++;
                lists.get(i).remove(0);
                for (int j = i; j < lists.size(); j++) {
                    for (ListIterator<SongPartition> it = lists.get(j).listIterator(); it.hasNext(); ) {
                        song_j = it.next();
                        match = 0;
                        for (String part : cur_song.parts) {
                            if (song_j.parts.contains(part)) {
                                match++;
                            }
                        }
                        if (match / Math.max(Math.max(cur_song.parts.size(), song_j.parts.size()),1) >= 0.6) {
                            listeners[j]++;
                            similar.add(song_j);
                            it.remove();
                        }
                    }
                }

                choises.add(new Choise(similar, listeners));
                similar = new ArrayList<>();
                listeners = new int[totalUsers];
            }
        }

        return choises;
    }


    public static ArrayList<SongListeners> group_stream(List<Pair> pairs) {

        System.out.println("0"+ new String(new char[47]).replace("\0", "-") + "100%");
        int size = pairs.size();
        int division = (int)(size/50);
        int nextdiv = size - division;

        ArrayList<SongListeners> listenersList = new ArrayList<>();
        //System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
        while(!pairs.isEmpty()) {

            Pair first = pairs.get(0);
            Map<Boolean, List<Pair>> similar = pairs.stream().parallel().collect(Collectors.partitioningBy(pair -> isSimilar(first.song, pair.song)));
            Set<String> uids = similar.get(true).stream().map(i -> i.uid).collect(Collectors.toSet());
            if (uids.size() < 15) {
                pairs = similar.get(false);
                //pairs = new ArrayList<Pair>(similar.get(false));
                continue;
            }
            if(!valid(first.song.name)) {
                pairs = similar.get(false);
                continue;
            }

            Optional<String> frequent = similar.get(true).stream().parallel().map(i -> i.song.name).collect(Collectors.groupingBy(s -> s, Collectors.counting())).entrySet().stream().max(Comparator.comparing(Entry::getValue)).map(i -> i.getKey());
            String song = frequent.orElse(new String(""));
            SongListeners listeners = new SongListeners(song, uids);
            listenersList.add(listeners);
            //pairs = new ArrayList<Pair>(similar.get(false));
            pairs = similar.get(false);
            //System.out.println(pairs.size());
            if (pairs.size() < nextdiv) {
                System.out.print("-");
                nextdiv = nextdiv - division;
            }
        }

        return listenersList;

    }


    public static boolean valid(String songName) {


        if (songName.toLowerCase().contains("неизв")) {
            return false;
        }
        if (songName.toLowerCase().contains("www")) {
            return false;
        }
        if (songName.toLowerCase().contains(".ru")) {
            return false;
        }
        if (songName.toLowerCase().contains(".ua")) {
            return false;
        }
        if (songName.toLowerCase().contains("муз")) {
            return false;
        }
        if (songName.toLowerCase().contains("песни")) {
            return false;
        }
        if (songName.toLowerCase().contains("гимн")) {
            return false;
        }
        if (songName.toLowerCase().contains("гімн")) {
            return false;
        }
        if (songName.toLowerCase().contains("танец")) {
            return false;
        }
        if (songName.toLowerCase().contains("онлайн")) {
            return false;
        }
        if (songName.toLowerCase().contains("июль")) {
            return false;
        }
        if (songName.toLowerCase().contains("август")) {
            return false;
        }
        if (songName.toLowerCase().contains("апрель")) {
            return false;
        }
        if (songName.toLowerCase().contains("мурашки")) {
            return false;
        }


        return true;
    }

    public static boolean isSimilar(SongPartition song1, SongPartition song2) {

        int match = 0;
        for (String part : song1.parts) {
            if (song2.parts.contains(part)) {
                match++;
            }
        }
        float ratio = (float)match / Math.max(Math.max(song1.parts.size(), song2.parts.size()),1);
        if ( ratio >= 0.5f) {
            return true;
        }
        else {
            return false;
        }


    }

}
