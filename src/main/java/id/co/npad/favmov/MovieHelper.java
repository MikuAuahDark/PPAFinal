package id.co.npad.favmov;

import java.io.*;
import java.util.*;

public class MovieHelper {
    // Penanda bahwa ini file yang diinginkan.
    // NPad, FaVorite, Movie
    static byte signature[] = {'N', 'F', 'V', 'M'};

    public static ArrayList<MovieInfo> readMovies(String filename) {
        // Daftar film kesukaan
        ArrayList<MovieInfo> movies = new ArrayList<>();

        // Stream untuk membaca file
        FileInputStream stream = null;

        try {
            // Buat FileInputStream baru
            stream = new FileInputStream(filename);

            // Coba baca apakah ini file yang benar
            byte test[] = new byte[4];
            if (stream.read(test) < 4 || Arrays.equals(test, signature) == false)
                throw new IOException("invalid file");
            
            // Baca berapa banyak elemen dalam file ini
            int amount = Utils.readInt(stream);

            // Baca semua elemen
            for (int i = 0; i < amount; i++)
                movies.add(MovieInfo.decode(stream));
        } catch (IOException e) {
            System.out.println("Partial read only");
            e.printStackTrace(System.out);
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException l) {
                // Masalah serius!
                l.printStackTrace(System.out);
            }
        }

        return movies;
    }

    // Method ini menulis daftar film ke file.
    // Return true jika sukses, false jika gagal.
    public static boolean writeMovies(ArrayList<MovieInfo> movies, String filename) {
        FileOutputStream stream = null;
        boolean ok = true;

        try {
            stream = new FileOutputStream(filename);

            // Tulis penanda bahwa ini file yang benar
            stream.write(signature);
            // Tulis banyak elemen
            Utils.write(stream, movies.size());

            for (MovieInfo movie: movies)
                stream.write(movie.encode());
        } catch (IOException l) {
            System.out.println("Probably partial write. File may corrupt!");
            l.printStackTrace(System.out);
            ok = false;
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Masalah serius
                e.printStackTrace(System.out);
            }
        }

        return ok;
    }
}
