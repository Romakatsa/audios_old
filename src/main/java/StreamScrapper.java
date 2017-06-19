import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Roma on 09.05.2017.
 */
public class StreamScrapper {

    public static final String URL = "https://vk.com/al_audio.php";
    public static final String file_in = "src/main/java/users.txt";
    public static final String file_out = "src/main/java/artists.txt";
    static String format = "access_hash=&act=load_section&al=1&claim=0&offset=1&owner_id=%s&playlist_id=-1&type=playlist";

    public static int stream_scrap() throws IOException {

        int total = 0;
        long timeStart = System.currentTimeMillis();

        List<String> uids = new ArrayList<String>();
        List<String> used_uids = null;
        BufferedReader br = new BufferedReader(new FileReader(new File(file_in).getAbsoluteFile()));



        try (LineNumberReader lnr = new LineNumberReader(br)) {
            for (String line; (line = lnr.readLine()) != null; ) {



                //if (lnr.getLineNumber() <= 50000) {
                //    continue;
                //}


                

                if (lnr.getLineNumber() > 50000) {
                    break;
                }
                uids.add(line);
            }
        }

        URL obj = new URL(URL);

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
        List<Pair> pairs = IntStream.range(0,uids.size()).boxed().flatMap(i ->
            getUserList(uids.get(i),obj).stream()).collect(Collectors.toList());

        FileOutputStream pairs_out = new FileOutputStream("00000_50000_v2.ser");
        ObjectOutputStream pairs_oos = new ObjectOutputStream(pairs_out);
        pairs_oos.writeObject(pairs);
        pairs_oos.close();



        /*
        List<Pair> pairs = new ArrayList<>();
        try
        {
            FileInputStream inputFileStream = new FileInputStream("0000_10000.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
            pairs  = (List<Pair>)objectInputStream.readObject();
            objectInputStream.close();
            inputFileStream.close();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException i)
        {
            i.printStackTrace();
        }

        */
        System.out.println(pairs.size());



        ArrayList<SongListeners> songListeners = Filter.group_stream(pairs);

        FileOutputStream listeners_out = new FileOutputStream("00000_50000_grouped_v2.ser");
        ObjectOutputStream listeners_oos = new ObjectOutputStream(listeners_out);
        listeners_oos.writeObject(songListeners);
        listeners_oos.close();

        /*

        ArrayList<SongListeners> songListeners = null;
        try
        {
            FileInputStream inputFileStream = new FileInputStream("00000_50000_grouped_v2.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
            songListeners  = (ArrayList<SongListeners>)objectInputStream.readObject();
            objectInputStream.close();
            inputFileStream.close();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException i)
        {
            i.printStackTrace();
        }
        */


        //popularity("ДДТ",songListeners);
        //popularity("U2",songListeners);
        //popularity("rag n bone man",songListeners);
        //popularity("Машина времени",songListeners);


        byte[][] choises = MatrixCreator.getChoisesMatrix(songListeners);
        long timeEnd = System.currentTimeMillis();

        FileOutputStream choises_out = new FileOutputStream("choises00000_50000_v2.ser");
        ObjectOutputStream choises_oos = new ObjectOutputStream(choises_out);
        choises_oos.writeObject(choises);
        choises_oos.close();


        PrintWriter f = null;
            try {
                f = new PrintWriter(new FileWriter("artists00000_50000_v2.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(SongListeners song : songListeners) {
                f.println(song.song);
            }


            f.close();


        //System.out.println("after grouping = "+ songListeners.size());
        System.out.println("time = "+ (timeEnd-timeStart));

        return pairs.size();
        //return 1;
    }

    public static int popularity(String song_name, List<SongListeners> songListeners) {

        SongPartition search = new SongPartition(song_name,Pair.get_parts(Pair.clear(song_name).toLowerCase().split("\\s+")));
        IntStream.range(0, songListeners.size()).parallel().mapToObj(i-> songListeners.get(i))
                .filter(i-> Filter.isSimilar(search,new SongPartition(i.song))).forEach(i-> System.out.println("song: " + i.song + " popular: "+ i.uids.size()));

        return 1;
    }


    public static ArrayList<Pair> getUserList(String uid, URL obj) {
        ArrayList<Pair> pairs = new ArrayList<>();
        HttpURLConnection con = null;
        StringBuffer response = new StringBuffer();
        try {
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            con.setRequestProperty("Cookie", " remixsid=ff4fc4b70f254e056aae0b69ce95d96ac377a84e67ebf16f9ae46");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Content-Length", "98");
            con.setRequestProperty("Host", "vk.com");

            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(String.format(format, uid));

            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"windows-1251" ));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        }
        catch (IOException e) {
            //return pairs;
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }


        int start = response.indexOf("[[");
        int end = response.lastIndexOf("]]");
        if (start < 0 || end < 0)
            return pairs;

        String audio_list = response.substring(start + 1, end+1);
        //System.out.println(audio_list);
        int totalCountIndex = response.indexOf("totalCount");
        int totalCount = Integer.parseInt(response.substring(totalCountIndex + 12, response.indexOf(",", totalCountIndex + 12)));

        pairs = ListParser.stream_parse(audio_list, totalCount, uid);

        return pairs;

    }




}
