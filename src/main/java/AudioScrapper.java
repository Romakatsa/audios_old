import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import org.springframework.web.util.HtmlUtils;

/**
 * Created by Roma on 03.05.2017.
 */
public class AudioScrapper {

    public static final String URL = "https://vk.com/al_audio.php";
    public static final String file_in = "src/users.txt";
    public static final String file_out = "src/artists.txt";

    public static int scrap() throws IOException {

        int total = 0;
        long timeStart = System.currentTimeMillis();

        List<String> uids = new ArrayList<String>();
        List<String> used_uids = null;
        BufferedReader br = new BufferedReader(new FileReader(new File(file_in).getAbsoluteFile()));

        try(LineNumberReader lnr = new LineNumberReader(br)) {
            for (String line; (line = lnr.readLine()) != null; ) {

                if (lnr.getLineNumber() <= 0) {
                    continue;
                }

                if (lnr.getLineNumber() > 300) {
                    break;
                }
                uids.add(line);
            }
        }



        used_uids = new ArrayList<>(uids.size());
        ArrayList<LinkedList<SongPartition>> lists = new ArrayList<LinkedList<SongPartition>>(uids.size());


        URL obj = new URL(URL);
        HttpURLConnection con = null;


        String format = "access_hash=&act=load_section&al=1&claim=0&offset=1&owner_id=%s&playlist_id=-1&type=playlist";

        StringBuffer response = new StringBuffer();

        //FileOutputStream fos = new FileOutputStream("0_3000.ser");

        for (String uid: uids) {
            response.setLength(0);
            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                con.setRequestProperty("Cookie", "remixsid=eeae07c31408f331273d08b0a0a5dcebc5b719cfb97ca225482b1");
                con.setRequestProperty("Connection", "keep-alive");
                con.setRequestProperty("Content-Length", "98");
                con.setRequestProperty("Host","vk.com");

                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(String.format(format, uid));

                int responseCode = con.getResponseCode();
                //System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"windows-1251" ));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
            finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            int start = response.indexOf("[[");
            int end = response.lastIndexOf("]]");
            if (start < 0 || end < 0)
                continue;

            String audio_list = response.substring(start + 1, end+1);
            //System.out.println(audio_list);
            int totalCountIndex = response.indexOf("totalCount");
            int totalCount = Integer.parseInt(response.substring(totalCountIndex + 12, response.indexOf(",", totalCountIndex + 12)));

            LinkedList<SongPartition> artists = ListParser.parse(audio_list, totalCount);

            used_uids.add(uid);
            lists.add(artists);

            total += artists.size();
            //System.out.println(total);
        }
        System.out.println("total = "+total);
        //ObjectOutputStream oos = new ObjectOutputStream(fos);
        //oos.writeObject(lists);
        //oos.close();

        /*
        LinkedList<Choise> choises = Filter.group(used_uids.size(),lists,total);
        */
        //System.out.println(choises);
        long timeEnd = System.currentTimeMillis();

        //System.out.println("after grouping = "+choises.size());
        System.out.println("time = "+ (timeEnd-timeStart));
        System.out.println(lists.get(lists.size()-1).getLast().name);
        //System.out.println(used_uids.get(used_uids.size());
        return uids.size();
    }

}
