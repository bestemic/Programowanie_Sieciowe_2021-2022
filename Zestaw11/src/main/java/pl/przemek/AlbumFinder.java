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

    public static void main(String[] args) {
        if (performOperation()) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private static boolean performOperation() {
        Set<String> albums = getAlbums();
        if (albums == null) {
            return false;
        }

        for (String album : albums) {
            System.out.println(album);
        }

        return true;
    }

    private static Set<String> getAlbums() {
        int page = 1;
        final Set<String> albums = new HashSet<>();

        try {
            while (true) {
                String response = getResponse(API + "/search?type=release&artist=Budka-Suflera&format=album&key=" + CONSUMER_KEY + "&secret=" + CONSUMER_SECRET + "&page=" + page + "&per_page=" + PAGINATION);
                if (response == null) {
                    return null;
                }

                JSONObject jsonResponse = new JSONObject(response);
                getAlbumsFromResponse(jsonResponse, albums);

                if (page == getNumberOFPages(jsonResponse)) {
                    break;
                }
                page++;
            }
        } catch (IOException e) {
            return null;
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

        final StringBuilder responseContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        getResponseContent(responseContent, reader);

        connection.disconnect();
        reader.close();
        return responseContent.toString();
    }

    private static void getResponseContent(final StringBuilder responseContent, final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
    }

    private static int getNumberOFPages(final JSONObject response) {
        int pages;
        try {
            JSONObject pagination = (JSONObject) response.get("pagination");
            pages = pagination.getInt("pages");
        } catch (JSONException e) {
            pages = 1;
        }
        return pages;
    }

    private static void getAlbumsFromResponse(final JSONObject response, final Set<String> albums) {
        JSONArray results = response.getJSONArray("results");
        for (Object result : results) {
            JSONObject album = (JSONObject) result;
            String title = album.getString("title").replaceAll("\\s+$", "");
            albums.add(title);
        }
    }
}