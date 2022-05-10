import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SiteTester {
    public static void main(String[] args) {
        String url = "http://th.if.uj.edu.pl/";
        String pattern = "Institute of Theoretical Physics";

        if (isValid(url, pattern)) {
            System.out.println("OK");
            System.exit(0);
        } else {
            System.out.println("ERROR");
            System.exit(1);
        }
    }

    static boolean isValid(String url, String pattern) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            if (!connection.getContentType().contains("text/html")) {
                return false;
            }
            if (!hasPattern(connection, pattern)) {
                return false;
            }
            connection.disconnect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    static boolean hasPattern(HttpURLConnection connection, String pattern) {
        try {
            BufferedReader site = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = site.readLine()) != null) {
                if (line.contains(pattern)) {
                    break;
                }
            }
            site.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
