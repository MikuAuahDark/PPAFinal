package id.co.npad.favmov;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.*;

public class MovieSubWindow {
    // Method ini bisa berfungsi sebagai method untuk memunculkan detail
    // film atau menambah film.
    // * Jika digunakan sebagai "Detail", maka parameter kedua tidak null
    //   dan method ini akan return NULL.
    // * Jika digunakan sebagai "Tambah", maka parameter kedua null dan
    //   method ini mereturn MovieInfo jika data valid dan pengguna menekan
    //   tombol Tambahkan, atau mereturn NULL jika operasi dibatalkan.
    public static MovieInfo showMovie(Stage parent, MovieInfo data) {
        // Buat jendela baru
        Stage dialog = new Stage();

        // Layout utama jendela kedua.
        GridPane mainPane = new GridPane();
        mainPane.setPadding(new Insets(8, 8, 8, 8));
        mainPane.setHgap(8);
        mainPane.setVgap(8);

        // Judul film
        TextField movieName = new TextField();
        // Teks "placeholder"
        movieName.setPromptText("Judul Film...");
        mainPane.add(new Label("Judul"), 0, 0);
        mainPane.add(movieName, 1, 0);

        // Tanggal rilis
        DatePicker movieRelease = new DatePicker();
        mainPane.add(new Label("Tanggal"), 0, 1);
        mainPane.add(movieRelease, 1, 1);

        // Durasi film
        TextField movieDuration = new TextField();
        movieDuration.setPromptText("n jam m menit/n:mm");
        mainPane.add(new Label("Durasi"), 0, 2);
        mainPane.add(movieDuration, 1, 2);

        // Genre
        TextField movieGenre = new TextField();
        // Teks "placeholder"
        movieGenre.setPromptText("Genre1, Genre2, ...");
        mainPane.add(new Label("Genre"), 0, 3);
        mainPane.add(movieGenre, 1, 3);

        // Cast
        TextField movieCast = new TextField();
        // Teks "placeholder"
        movieCast.setPromptText("Nama Satu, Nama Dua, ...");
        mainPane.add(new Label("Pemeran"), 0, 4);
        mainPane.add(movieCast, 1, 4);

        // Sinopsis
        TextArea movieSynopsis = new TextArea();
        mainPane.add(new Label("Sinopsis"), 0, 5);
        mainPane.add(movieSynopsis, 1, 5);

        // Tombol bawah
        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        // Tombol Tambahkan/Salin
        Button actionButton = new Button("Tambahkan");
        // Tombol Batal/Tutup
        Button closeButton = new Button("Batal");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dialog.close();
            }
        });

        // Status
        Label status = new Label();
        status.setAlignment(Pos.CENTER);

        // Salin data-data film
        if (data != null) {
            // Judul film
            movieName.setText(data.title);
            // Jangan biarkan diubah
            movieName.setEditable(false);

            // Set tanggal rilis
            movieRelease.setValue(LocalDate.ofInstant(data.release.toInstant(), ZoneOffset.UTC));
            movieRelease.setEditable(false);

            // Durasi
            movieDuration.setText(Utils.toDuration(data.duration));
            movieDuration.setEditable(false);

            // Genre
            movieGenre.setText(data.genre);
            movieGenre.setEditable(false);

            // Pemeran
            movieCast.setText(data.cast);
            movieCast.setEditable(false);

            // Sinopsis
            movieSynopsis.setText(data.synopsis);
            movieSynopsis.setEditable(false);

            actionButton.setText("Salin");
            closeButton.setText("Tutup");

            // Salin ke papan klip
            actionButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // Karakter baris baru, bergantung OS
                    String lf = System.lineSeparator();
                    StringBuilder sb = new StringBuilder();

                    // Nama
                    sb.append("Judul  : "); sb.append(data.title); sb.append(lf);
                    // Rilis
                    sb.append("Rilis  : "); sb.append(data.getReleaseDate()); sb.append(lf);
                    // Durasi
                    sb.append("Durasi : "); sb.append(Utils.toDuration(data.duration)); sb.append(lf);
                    // Genre
                    sb.append("Genre  : "); sb.append(data.genre); sb.append(lf);
                    // Pemeran
                    sb.append("Pemeran: "); sb.append(data.cast); sb.append(lf);

                    // Sinopsis
                    sb.append(lf);
                    sb.append(data.synopsis);

                    ClipboardContent content = new ClipboardContent();
                    content.putString(sb.toString());
                    if (Clipboard.getSystemClipboard().setContent(content))
                        status.setText("Tersalin ke papan klip.");
                    else
                        status.setText("Gagal menyalin!");
                }
            });
        } else {
            // Tambahkan. Validasi semua data input
            actionButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // Cek judul film
                    if (movieName.getText().isEmpty()) {
                        status.setText("Judul film kosong.");
                        return;
                    }

                    // Cek tanggal rilis
                    if (movieRelease.getValue() == null) {
                        status.setText("Tanggal rilis kosong.");
                        return;
                    }

                    // Cek durasi
                    int duration = 0;
                    try {
                        duration = Utils.parseDuration(movieDuration.getText());
                    } catch (NumberFormatException fe) {
                        status.setText("Durasi kosong atau tidak benar.");
                        return;
                    }

                    // Cek genre
                    if (movieGenre.getText().isEmpty()) {
                        status.setText("Genre kosong.");
                        return;
                    }

                    // Cek pemeran
                    if (movieCast.getText().isEmpty()) {
                        status.setText("Pemeran kosong.");
                        return;
                    }

                    // Cek sinopsis
                    if (movieSynopsis.getText().isEmpty()) {
                        status.setText("Sinopsis kosong.");
                        return;
                    }

                    // Valid.
                    status.setText("OK");
                    dialog.close();
                }
            });
        }

        // Tambahkan ke HBox
        buttons.getChildren().addAll(actionButton, closeButton);

        // Layout inti jendela
        BorderPane rootPane = new BorderPane();
        rootPane.setPadding(new Insets(8, 8, 8, 8));
        rootPane.setTop(mainPane);
        rootPane.setCenter(status);
        rootPane.setBottom(buttons);

        // Atur jendela
        dialog.setTitle(data == null ? "Tambah Film" : ("Detail Film - " + data.title));
        dialog.setScene(new Scene(rootPane, 656, 440));
        // 3 kode dibawah agar jendela muncul dan memblokir jendela sebelumnya
        dialog.initOwner(parent);
        dialog.initModality(Modality.APPLICATION_MODAL); 
        dialog.showAndWait();

        // Jika pengguna menggunakan "Tambah", maka:
        // * data harusnya NULL
        // * status.getText() berisikan "OK".
        if (data == null && status.getText().equals("OK")) {
            MovieInfo ret = new MovieInfo();
            ret.title = movieName.getText();
            ret.release = new Date(movieRelease.getValue().atStartOfDay(ZoneOffset.UTC).toEpochSecond() * 1000);
            ret.duration = Utils.parseDuration(movieDuration.getText());
            ret.synopsis = movieSynopsis.getText();
            ret.genre = movieGenre.getText();
            ret.cast = movieCast.getText();
            return ret;
        }

        return null;
    }
}
