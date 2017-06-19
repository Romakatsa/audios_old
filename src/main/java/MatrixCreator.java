import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Roma on 10.05.2017.
 */
public class MatrixCreator {



    public static byte[][] getChoisesMatrix(List<SongListeners> listenersList) {

        long startMatrix = System.currentTimeMillis();

        BiMap<Integer, String> usersBiMap = HashBiMap.create();
        //BiMap<String, Integer> songsBiMap = HashBiMap.create();
        Set<String> uniqueUsers = listenersList.stream().flatMap(i->i.uids.stream()).collect(Collectors.toSet());
        int userIndex = 0;
        int choises = 0;
        System.out.println("ser:");
        byte[][] choisesMatrix = new byte[listenersList.size()][uniqueUsers.size()];

        for(String user:uniqueUsers) {
            usersBiMap.put(userIndex,user);
            userIndex++;
        }

        try {
            FileOutputStream biMapout = new FileOutputStream("biMap00000_50000_v2.ser");
            ObjectOutputStream biMap_oos = new ObjectOutputStream(biMapout);
            biMap_oos.writeObject(usersBiMap);
            biMap_oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int songIndex = 0;
        for(SongListeners listeners : listenersList) {
            for(String uid:listeners.uids) {
                choisesMatrix[songIndex][usersBiMap.inverse().get(uid)] = 1;
                choises++;
            }
            songIndex++;
        }

        long endMatrix = System.currentTimeMillis();

        System.out.println("Matrix created "+ (endMatrix-startMatrix) + "millisec's.");

        return choisesMatrix;



    }



}
