package id.co.npad.favmov;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Method-method pembantu
public class Utils {
    // Variabel sementara
    static byte sizeTemp[] = new byte[8];
    // Pola pertama untuk parseDuration
    static Pattern pattern1 = Pattern.compile("^(\\d+) jam (\\d+) menit$");
    // Pola kedua untuk parseDuration
    static Pattern pattern2 = Pattern.compile("^(\\d+):(\\d+)$");

    // Menulis int kedalam stream
    public static void write(OutputStream stream, int v) throws IOException {
        // Enkode integer ke sizeTemp. Operator "& 0xFF" digunakan agar
        // memastikan nilai yang dienkode itu tepat.
        // Intinya method ini mengubah integer menjadi 4 byte array.
        sizeTemp[0] = (byte) (v & 0xFF);
        sizeTemp[1] = (byte) ((v >> 8) & 0xFF);
        sizeTemp[2] = (byte) ((v >> 16) & 0xFF);
        sizeTemp[3] = (byte) ((v >> 24) & 0xFF);
        stream.write(sizeTemp, 0, 4);
    }

    public static void write(OutputStream stream, long v) throws IOException {
        // Enkode long ke sizeTemp.
        // Intinya method ini mengubah long menjadi 8 byte array.
        sizeTemp[0] = (byte) (v & 0xFF);
        sizeTemp[1] = (byte) ((v >> 8L) & 0xFF);
        sizeTemp[2] = (byte) ((v >> 16L) & 0xFF);
        sizeTemp[3] = (byte) ((v >> 24L) & 0xFF);
        sizeTemp[4] = (byte) ((v >> 32L) & 0xFF);
        sizeTemp[5] = (byte) ((v >> 40L) & 0xFF);
        sizeTemp[6] = (byte) ((v >> 48L) & 0xFF);
        sizeTemp[7] = (byte) ((v >> 56L) & 0xFF);
        stream.write(sizeTemp, 0, 8);
    }

    public static int readInt(InputStream stream) throws IOException {
        if (stream.read(sizeTemp, 0, 4) < 4)
            throw new IOException("size read less than 4");

        // Intinya method ini mengubah 4 byte array menjadi satu integer.
        return
            (((int) sizeTemp[0]) & 0xFF) |
            ((((int) sizeTemp[1]) & 0xFF) << 8) |
            ((((int) sizeTemp[2]) & 0xFF) << 16) |
            ((((int) sizeTemp[3]) & 0xFF) << 24);
    }

    public static long readLong(InputStream stream) throws IOException {
        if (stream.read(sizeTemp, 0, 8) < 8)
            throw new IOException("size read less than 8");

        // Intinya method ini mengubah 8 byte array menjadi satu long.
        return
            (((long) sizeTemp[0]) & 0xFFL) |
            ((((long) sizeTemp[1]) & 0xFFL) << 8L) |
            ((((long) sizeTemp[2]) & 0xFFL) << 16L) |
            ((((long) sizeTemp[3]) & 0xFFL) << 24L) |
            ((((long) sizeTemp[4]) & 0xFFL) << 32L) |
            ((((long) sizeTemp[5]) & 0xFFL) << 40L) |
            ((((long) sizeTemp[6]) & 0xFFL) << 48L) |
            ((((long) sizeTemp[7]) & 0xFFL) << 56L);
    }

    // Method ini mengubah "n jam m menit" atau "n:mm" menjadi durasi dalam
    // bentuk integer
    public static int parseDuration(String dur) throws NumberFormatException {
        dur = dur.toLowerCase().trim();

        // Coba cocokkan n jam m menit
        Matcher match = pattern1.matcher(dur);
        boolean found = match.find();
        if (found == false) {
            // Jika tidak cocok, coba format nn:mm
            match = pattern2.matcher(dur);
            found = match.find();
        }

        if (found) {
            // match.group(1) = n
            // match.group(2) = m
            return Integer.parseInt(match.group(1)) * 3600 + Integer.parseInt(match.group(2)) * 60;
        }
        
        // Tidak ada kecocokan
        throw new NumberFormatException("invalid format");
    }

    // Method yang mengubah durasi dalam detik menjadi string yang bisa
    // dibaca, dalam Bahasa Indonesia.
    public static String toDuration(int dur) {
        return String.format("%d jam %d menit", dur / 3600, (dur / 60) % 60);
    }
}
