package id.co.npad.favmov;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class MovieInfo {
    // Judul
    public String title;
    // Tanggal rilis
    public Date release;
    // Durasi, dalam detik
    public int duration;
    // Sinopsis
    public String synopsis;
    // Genre
    public String genre;
    // Pemeran
    public String cast;

    // Formatter, selalu dalam Bahasa Indonesia
    SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMMM Y", new Locale("id"));

    MovieInfo() {
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // Meng-enkode data menjadi data biner yang bisa ditulis.
    // Jika ada elemen yang null, akan melempar IllegalStateException
    public byte[] encode() throws IOException, IllegalStateException {
        check();

        // Tempat data ter-enkode
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        
        // Judul film dalam byte. UTF-8 adalah cara enkode karakter
        // yang sekarang populer dan kompatibel dengan ASCII, dan
        // muat dalam tipe data byte.
        byte titleByte[] = title.getBytes(StandardCharsets.UTF_8);

        // Dapatkan timestamp tanggal rilis. Timestamp yang dimaksud disini
        // adalah banyak detik semenjak 1 Januari 1970.
        // Method Date.getTime() menghasilkan waktu dalam milidetik (1/1000 detik)
        // jadi harus dibagi dengan 1000.
        // Tipe data long digunakan untuk menghindari masalah pada tahun 2038.
        long releaseTS = release.getTime() / 1000;

        // Sinopsis dalam bentuk byte.
        byte synopsisByte[] = synopsis.getBytes(StandardCharsets.UTF_8);
        // Daftar genre dalam bentuk byte, dipisah dengan koma.
        byte genreByte[] = genre.getBytes(StandardCharsets.UTF_8);
        // Sama untuk pemeran
        byte castByte[] = cast.getBytes(StandardCharsets.UTF_8);

        // Tulis panjang judul dalam bytes
        Utils.write(byteStream, titleByte.length);
        // Tulis judul
        byteStream.write(titleByte);

        // Tulis tanggal rilis
        Utils.write(byteStream, releaseTS);
        // Tulis durasi film
        Utils.write(byteStream, duration);

        // Tulis panjang sinopsis
        Utils.write(byteStream, synopsisByte.length);
        // Tulis sinopsis
        byteStream.write(synopsisByte);

        // Sama untuk genre
        Utils.write(byteStream, genreByte.length);
        byteStream.write(genreByte);

        // Dan pemeran
        Utils.write(byteStream, castByte.length);
        byteStream.write(castByte);

        byte result[] = byteStream.toByteArray();

        try {
            byteStream.close();
        } catch (IOException p) {
            // Masalah serius!
            p.printStackTrace(System.out);
            throw p;
        }

        return result;
    }

    // Method ini membalikkan method diatas, yaitu men-dekode
    // data yang sudah ter-enkode menjadi objek kembali.
    public static MovieInfo decode(InputStream stream) throws IOException {
        int length = 0;

        // Baca panjang judul
        length = Utils.readInt(stream);
        // Baca judul
        byte titleByte[] = new byte[length];
        stream.read(titleByte);

        // Baca tanggal rilis
        long releaseTS = Utils.readLong(stream);
        // Baca durasi
        int dur = Utils.readInt(stream);

        // Baca panjang sinopsis
        length = Utils.readInt(stream);
        // Baca sinopsis
        byte synopsisByte[] = new byte[length];
        stream.read(synopsisByte);

        // Sama untuk genre
        length = Utils.readInt(stream);
        byte genreByte[] = new byte[length];
        stream.read(genreByte);

        // Dan pemeran
        length = Utils.readInt(stream);
        byte castByte[] = new byte[length];
        stream.read(castByte);

        // Buat objek.
        MovieInfo ret = new MovieInfo();
        // Ubah kembali byte array menjadi String dengan charset UTF-8.
        ret.title = new String(titleByte, StandardCharsets.UTF_8);
        // Ubah tanggal rilis menjadi objek Date. Date mengharapkan waktu dalam
        // milidetik jadi kali dengan 1000.
        ret.release = new Date(releaseTS * 1000);
        // ...
        ret.duration = dur;
        // Ubah kembali byte array menjadi String.
        ret.synopsis = new String(synopsisByte, StandardCharsets.UTF_8);
        // Sama seperti diatas.
        ret.genre = new String(genreByte, StandardCharsets.UTF_8);
        // Sama juga.
        ret.cast = new String(castByte, StandardCharsets.UTF_8);

        // Return objek
        return ret;
    }

    // Method ini menghasilkan tanggal rilis dalam bentuk string
    public String getReleaseDate() {
        return dateFormatter.format(release);
    }

    // Men-cek apakah ada elemen yang null. Jika ada, lempar exception.
    void check() throws IllegalStateException {
        if (
            title == null ||
            release == null ||
            duration == 0 ||
            synopsis == null ||
            genre == null ||
            cast == null
        )
            throw new IllegalStateException("incomplete type");
    }
}
