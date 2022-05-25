package pl.przemek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ArtistFinder {
    private static final String CONSUMER_KEY = "key";
    private static final String CONSUMER_SECRET = "secret";
    private static final String API_DATABASE = "https://api.discogs.com/database";
    private static final String API = "https://api.discogs.com/artists";

    private static String parentGroupName;
    private static String errorMessage;

    public static void main(String[] args) {
        if (performOperation(args)) {
            System.exit(0);
        } else {
            setErrorMessage("other error occurred");
            System.err.println("ERROR - " + errorMessage);
            System.exit(1);
        }
    }

    private static boolean performOperation(String[] args) {
        final String groupIdentity = getGroupIdentity(args);
        if (groupIdentity == null) {
            return false;
        }

        try {
            Map<String, String> artists = getArtists(groupIdentity);
            if (artists == null) {
                return false;
            }

            Map<String, Set<String>> groups = getGroups(artists);
            if (groups == null) {
                return false;
            }

            printGroups(groups);
        } catch (IOException e) {
            setErrorMessage("IOException occurred");
            return false;
        }

        return true;
    }

    private static String getGroupIdentity(String[] args) {
        if (args.length != 1) {
            setErrorMessage("wrong number of arguments");
            return null;
        }
        return args[0];
    }

    private static Map<String, String> getArtists(final String groupIdentity) throws IOException {
        final String url = createUrl(groupIdentity);
        if (url == null) {
            return null;
        }

        String response = getResponse(url);
        if (response == null) {
            return null;
        }

        JSONObject jsonResponse = new JSONObject(response);
        parentGroupName = getGroupNameFromResponse(jsonResponse);

        return getArtistsFromResponse(jsonResponse);
    }

    private static Map<String, String> getArtistsFromResponse(final JSONObject response) {
        final Map<String, String> artists = new HashMap<>();

        try {
            JSONArray results = response.getJSONArray("members");
            for (Object result : results) {
                JSONObject artist = (JSONObject) result;
                String artistName = artist.getString("name");
                String artistUrl = artist.getString("resource_url");
                artists.put(artistName, artistUrl);
            }
        } catch (JSONException e) {
            setErrorMessage("can't get all artists from group");
            return null;
        }

        return artists;
    }

    private static Map<String, Set<String>> getGroups(final Map<String, String> artists) throws IOException {
        final Map<String, Set<String>> groups = new TreeMap<>(Collator.getInstance(new Locale("pl", "PL")));
        for (String artist : artists.keySet()) {
            String url = String.format("%s?key=%s&secret=%s", artists.get(artist), CONSUMER_KEY, CONSUMER_SECRET);
            String response = getResponse(url);
            if (response == null) {
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            List<String> groupsFromResponse = getGroupsFromResponse(jsonResponse);
            if (groupsFromResponse == null) {
                return null;
            }

            for (String group : groupsFromResponse) {
                if (groups.containsKey(group)) {
                    groups.get(group).add(artist);
                } else {
                    Set<String> groupMembers = new TreeSet<>(Collator.getInstance(new Locale("pl", "PL")));
                    groupMembers.add(artist);
                    groups.put(group, groupMembers);
                }
            }
        }

        return groups;
    }

    private static List<String> getGroupsFromResponse(final JSONObject response) {
        List<String> groups = new ArrayList<>();

        try {
            JSONArray results = response.getJSONArray("groups");
            for (Object result : results) {
                JSONObject group = (JSONObject) result;
                String groupName = group.getString("name");
                groups.add(groupName);
            }
        } catch (JSONException e) {
            setErrorMessage("artist groups can't be found");
            return null;
        }

        return groups;
    }

    private static String getGroupIdFromResponse(JSONObject response) {
        try {
            JSONArray results = response.getJSONArray("results");
            JSONObject result = results.getJSONObject(0);
            return String.valueOf(result.getInt("id"));
        } catch (JSONException e) {
            setErrorMessage("group id not found");
            return null;
        }
    }

    private static String getGroupNameFromResponse(JSONObject response) {
        try {
            return String.valueOf(response.getString("name"));
        } catch (JSONException e) {
            setErrorMessage("group name not found");
            return null;
        }
    }

    private static String createUrl(String groupIdentity) throws IOException {
        if (!groupIdentity.matches("[0-9]+")) {
            String url = String.format("%s/search?type=artist&q=%s&per_page=1&key=%s&secret=%s", API_DATABASE, groupIdentity, CONSUMER_KEY, CONSUMER_SECRET);
            String response = getResponse(url);
            if (response == null) {
                setErrorMessage("group not found");
                return null;
            }
            JSONObject jsonResponse = new JSONObject(response);
            groupIdentity = getGroupIdFromResponse(jsonResponse);
            if (groupIdentity == null) {
                return null;
            }
        }

        return String.format("%s/%s?key=%s&secret=%s", API, groupIdentity, CONSUMER_KEY, CONSUMER_SECRET);
    }

    private static void printGroups(Map<String, Set<String>> groups) {
        for (String groupName : groups.keySet()) {
            if (groupName.equals(parentGroupName)) {
                continue;
            }
            Set<String> members = groups.get(groupName);
            if (members.size() == 1) {
                continue;
            }

            System.out.print(groupName + ": ");
            System.out.println(String.join(", ", members));
        }
    }

    private static String getResponse(final String url) throws IOException {
        final URL connectionUrl = new URL(url);
        final HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
        connection.setConnectTimeout(5000);

        if (connection.getResponseCode() == 429) {
            try {
                connection.disconnect();
                System.err.println("WAITING - to many requests");
                TimeUnit.SECONDS.sleep(60);
                return getResponse(url);
            } catch (InterruptedException e) {
                setErrorMessage("failed to wait");
                return null;
            }
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            setErrorMessage("unsuccessful response");
            return null;
        }
        if (!connection.getContentType().contains("application/json")) {
            setErrorMessage("bad response type");
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

    private static void setErrorMessage(final String message) {
        if (errorMessage == null) {
            errorMessage = message;
        }
    }
}