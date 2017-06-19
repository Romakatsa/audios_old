import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println(StreamScrapper.stream_scrap());
            //System.out.println(AudioScrapper.scrap());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
