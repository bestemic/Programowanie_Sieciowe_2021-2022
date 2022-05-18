package pl.przemek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AlbumFinder {
    private static final String CONSUMER_KEY = "key";
    private static final String CONSUMER_SECRET = "secret";
    private static final String API = "https://api.discogs.com/database";
    private static final int PAGINATION = 100;
    private static final String AUTHOR = "Budka-Suflera";

    public static void main(String[] args) {
        if (performOperation()) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private static boolean performOperation() {
        Set<String> albums;
        try {
            albums = getAlbums();
            if (albums == null) {
                return false;
            }

            for (String album : albums) {
                System.out.println(album);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static Set<String> getAlbums() throws IOException {
        final Set<String> albums = new HashSet<>();

        for (int page = 1; ; page++) {
            final String url = String.format("%s/search?type=release&artist=%s&format=album&key=%s&secret=%s&page=%d&per_page=%d", API, AUTHOR, CONSUMER_KEY, CONSUMER_SECRET, page, PAGINATION);
            String response = getResponse(url);
            if (response == null) {
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            albums.addAll(getAlbumsFromResponse(jsonResponse));

            if (page == getNumberOFPages(jsonResponse)) {
                break;
            }
        }

        return albums;
    }

    private static String getResponse(final String url) throws IOException {
        final URL connectionUrl = new URL(url);
        final HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();

        if (connection.getResponseCode() != 200) {
            return null;
        }
        if (!connection.getContentType().contains("application/json")) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String responseContent = getResponseContent(reader);

        connection.disconnect();
        reader.close();
        return responseContent;
    }

    private static String getResponseContent(final BufferedReader reader) throws IOException {
        final StringBuilder responseContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        return responseContent.toString();
    }

    private static int getNumberOFPages(final JSONObject response) {
        try {
            JSONObject pagination = response.getJSONObject("pagination");
            return pagination.getInt("pages");
        } catch (JSONException e) {
            return 1;
        }
    }

    private static Set<String> getAlbumsFromResponse(final JSONObject response) {
        Set<String> albums = new HashSet<>();
        JSONArray results = response.getJSONArray("results");
        for (Object result : results) {
            JSONObject album = (JSONObject) result;
            String title = album.getString("title").replaceAll("\\s+$", "");
            albums.add(title);
        }
        return albums;
    }
}