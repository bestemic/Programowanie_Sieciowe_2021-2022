package pl.przemek;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScrapper {
    private static final Pattern currencyNamePattern = Pattern.compile("^[A-Z][a-z]+( [A-Z][a-z]+)*$");
    private static final Pattern exchangeRatePattern = Pattern.compile("^\\d+(\\.\\d)\\d*$");

    public static void main(String[] args) {
        final String url = "https://www.x-rates.com/table/?from=USD&amount=1";

        try {
            final Connection.Response response = getPage(url);
            if (response == null) {
                System.exit(1);
            }

            final Elements rows = getRows(response);
            if (rows == null) {
                System.exit(1);
            }

            if (!parseRows(rows)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.exit(1);
        }

        System.exit(0);
    }

    public static Connection.Response getPage(String url) throws IOException {
        final Connection.Response response = Jsoup.connect(url).timeout(10000).execute();
        if (response.statusCode() != 200) {
            return null;
        }
        if (!response.contentType().contains("text/html")) {
            return null;
        }
        if (!response.body().contains("US Dollar Exchange Rates Table")) {
            return null;
        }
        return response;
    }

    private static Elements getRows(Connection.Response response) throws IOException {
        final Document document = response.parse();
        final Elements tables = document.select(".tablesorter.ratesTable");
        if (tables.size() != 1) {
            return null;
        }

        final Element table = tables.first();
        if (table.childrenSize() < 2) {
            return null;
        }

        final Element tbody = table.child(1);
        if (tbody.childrenSize() < 1) {
            return null;
        }

        final Elements rows = tbody.children();
        if (rows.size() < 1) {
            return null;
        }
        return rows;
    }

    public static boolean parseRows(Elements rows) throws IOException {
        for (Element row : rows) {
            if (row.childrenSize() != 3) {
                return false;
            }

            String currencyName = row.child(0).text();
            final Matcher currencyNameMatcher = currencyNamePattern.matcher(currencyName);
            if (!currencyNameMatcher.find()) {
                return false;
            }

            currencyName = currencyName.replace(" ", "_");
            final String exchangeRate = row.child(1).text();
            final Matcher exchangeRateMatcher = exchangeRatePattern.matcher(exchangeRate);
            if (!exchangeRateMatcher.find()) {
                return false;
            }

            System.out.println(currencyName + " " + exchangeRate);
        }
        return true;
    }
}