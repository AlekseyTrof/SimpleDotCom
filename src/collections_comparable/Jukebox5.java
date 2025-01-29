package collections_comparable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Jukebox5 {
    ArrayList<Song> songList = new ArrayList<>();

    public static void main(String[] args) {
        new Jukebox5().go();
    }

    public void go() {
        getSongs();
        //Выводим список песен и исполнителей
        System.out.println("Выводим список песен и исполнителей:");
        System.out.println(songList);
        System.out.println();
        //Сортируем список по названию песен. Интерфейс Comparabale в классе Song
        Collections.sort(songList);
        //Выводим отсортированный список
        System.out.println("Выводим отсортированный список песен по названию:");
        System.out.println(songList);
        System.out.println();
        //Сортируем список песен по исполнителям. Интерфейс Comparator во вложенном классе
        ArtistCompare artistCompare = new ArtistCompare();
        Collections.sort(songList,artistCompare);
        //Выводим отсортированный список по артистам.
        System.out.println("Выводим отсортированный список по артистам:");
        System.out.println(songList);
        System.out.println();
        //Создаем множество, что бы убрать дубликаты песен.
        // Для этого мы переопределили методы equals и hashCode в классе Song.
        HashSet<Song> songSet = new HashSet<>();
        songSet.addAll(songList);
        //Выводим список песен(без дубликатов, но неотсортированный)
        System.out.println("Выводим список песен(без дубликатов, но неотсортированный):");
        System.out.println(songSet);
        System.out.println();
        //
        System.out.println("Выводим список песен без дубликатов и отсортированный:");
        TreeSet<Song> songTreeSet = new TreeSet<>();
        songTreeSet.addAll(songList);
        System.out.println(songTreeSet);
    }

    public void getSongs() {
        try {
            File file = new File("SongList.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                addSong(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSong(String lineToParse) {
        String[] tokens = lineToParse.split("/");
        Song nextSong = new Song(tokens[0], tokens[1], tokens[2], tokens[3]);
        songList.add(nextSong);
    }

    class ArtistCompare implements Comparator<Song> {
        @Override
        public int compare(Song one, Song two) {
            return one.getArtist().compareTo(two.getArtist());
        }
    }
}
