package id.co.npad.favmov;

import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.application.Application;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import javafx.stage.*;

public class App extends Application {
    static ArrayList<MovieInfo> movies;
    static String filename;

    @Override
    public void start(Stage stage) {
        // Layout (aturan UI) utama
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(8, 8, 8, 8));

        // Judul aplikasi
        Label title = new Label("Film Favorit");
        mainPane.setTop(title);

        // Layout tombol-tombol
        VBox buttons = new VBox();
        // Set "spacing"
        buttons.setPadding(new Insets(8, 8, 8, 8));
        buttons.setPrefWidth(100);
        buttons.setSpacing(8);
        // Supaya tombol-tombol ada di tengah
        buttons.setAlignment(Pos.CENTER);

        // Tombol detail
        Button detailButton = new Button("Detail");
        detailButton.setMinWidth(buttons.getPrefWidth());

        // Tombol tambah
        Button addButton = new Button("Tambah");
        addButton.setMinWidth(buttons.getPrefWidth());
        
        // Tombol hapus
        Button removeButton = new Button("Hapus");
        removeButton.setMinWidth(buttons.getPrefWidth());

        // Tambahkan tombol-tombol
        buttons.getChildren().addAll(detailButton, addButton, removeButton);
        mainPane.setLeft(buttons);

        // Layout daftar film
        VBox movieListPane = new VBox(8);
        // Daftar film
        ObservableList<String> movieList = FXCollections.observableArrayList();
        // Salin nama film ke ObservableList
        movies.forEach((MovieInfo n) -> movieList.add(n.title));

        // Buat filtered list, agar bisa dicari berdasarkan sistem pencari
        FilteredList<String> movieListFilter = new FilteredList<>(movieList, null);
        // Buat ListView
        ListView<String> listView = new ListView<>(movieListFilter);

        // Pencarian
        // Simbol "Cari" hanya ada dalam UTF-8
        //final byte searchText[] = new byte[] {(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x8D, 'C', 'a', 'r', 'i', '.', '.', '.'};
        //final String searchString = new String(searchText, StandardCharsets.UTF_8);
        TextField search = new TextField();
        search.setPromptText("Cari...");
        //search.setPromptText(searchString);
        // Filter ListView berdasarkan inputan yang ada pada teks "search"
        search.textProperty().addListener(dummy -> {
            String input = search.getText();

            if (input == null || input.isEmpty())
                // setPredicate(null) artinya munculkan semua item
                movieListFilter.setPredicate(null);
            else {
                String inputLower = input.toLowerCase();
                movieListFilter.setPredicate(s -> s.toLowerCase().contains(inputLower));
            }
        });
        // Tambahkan ke layout daftar film
        movieListPane.getChildren().addAll(search, listView);

        // Tambahkan ke layout utama.
        mainPane.setCenter(movieListPane);

        // Kode yang akan dieksekusi jika tombol "Detail" ditekan.
        detailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    String searchText = search.getText();
                    MovieInfo info = null;

                    // Jika tidak dalam pencarian, langsung indeks
                    if (searchText == null || searchText.isEmpty())
                        info = movies.get(index);
                    else {
                        searchText = searchText.toLowerCase();

                        // Ada dalam pencarian. Cari satu-satu. Indeks yang
                        // diberikan tidak akurat.
                        int i = -1;
                        for (MovieInfo movieInfo: movies) {
                            if (movieInfo.title.toLowerCase().contains(searchText))
                                i++;
                            if (i == index) {
                                info = movieInfo;
                                break;
                            }
                        }
                    }

                    // Jika ada ditemukan, tampilkan detailnya.
                    if (info != null)
                        MovieSubWindow.showMovie(stage, info);
                }
            }
        });

        // Kode yang akan dieksekusi jika tombol "Tambah" ditekan.
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Munculkan jendela penambahan film
                MovieInfo info = MovieSubWindow.showMovie(stage, null);
                
                // Jika sukses, tambahkan.
                if (info != null) {
                    movieList.add(info.title);
                    movies.add(info);
                }
            }
        });

        // Kode yang akan dieksekusi jika tombol "Hapus" ditekan.
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    String searchText = search.getText();
                    int actualIndex = -1;

                    // Jika tidak dalam pencarian, berarti sudah indeks yang benar
                    if (searchText == null || searchText.isEmpty())
                        actualIndex = index;
                    else {
                        searchText = searchText.toLowerCase();

                        // Ada dalam pencarian. Cari satu-satu. Indeks yang
                        // diberikan tidak akurat.
                        int i = -1;
                        for (MovieInfo movieInfo: movies) {
                            if (movieInfo.title.toLowerCase().contains(searchText))
                                i++;
                            if (i == index) {
                                actualIndex = i;
                                break;
                            }
                        }
                    }

                    // Jika indeks valid, konfirmasi untuk menghapus.
                    if (actualIndex >= 0) {
                        // Harus ditaruh lagi di variabel sementara
                        final int idx = actualIndex;

                        // Ambil judul film
                        MovieInfo info = movies.get(idx);
                        String rm = "Hapus '" + info.title + "'?";

                        // Buat message box
                        ButtonType yes = new ButtonType("Ya", ButtonData.YES);
                        ButtonType no = new ButtonType("Tidak", ButtonData.NO);
                        Alert confirm = new Alert(AlertType.CONFIRMATION, rm, yes, no);

                        confirm.setTitle(rm);
                        confirm.initOwner(stage);
                        confirm.initModality(Modality.APPLICATION_MODAL);
                        confirm.setResizable(true);
                        // Munculkan
                        confirm.showAndWait().ifPresent(action -> {
                            if (action.equals(yes)) {
                                movies.remove(idx);
                                movieList.remove(idx);
                            }
                        });
                    }
                }
            }
        });

        // Layout inti
        StackPane rootPane = new StackPane(mainPane);
        Scene scene = new Scene(rootPane, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Film Favorit");
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Java " + SystemInfo.javaVersion());
        System.out.println("JavaFX " + SystemInfo.javafxVersion());

        // File yang digunakan
        filename = args.length >= 1 ? args[0] : "favmov.bin";

        // Baca film
        movies = MovieHelper.readMovies(filename);

        // Jalankan
        launch(args);

        // Tulis film
        if (MovieHelper.writeMovies(movies, filename) == false)
            System.out.println("Failed to write movies");
    }
}
