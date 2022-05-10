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
    private static final Pattern CURRENCY_NAME_PATTERN = Pattern.compile("^[A-Z][a-z]+( [A-Z][a-z]+)*$");
    private static final Pattern EXCHANGE_RATE_PATTERN = Pattern.compile("^\\d+(\\.\\d)\\d*$");

    public static void main(String[] args) {
        final String url = "https://www.x-rates.com/table/?from=USD&amount=1";

        if (performOperation(url)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private static boolean performOperation(final String url) {
        try {
            final Connection.Response response = getPage(url);
            if (response == null) {
                return false;
            }

            final Elements rows = getRows(response);
            if (rows == null) {
                return false;
            }

            if (!parseRows(rows)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Connection.Response getPage(final String url) throws IOException {
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

    private static Elements getRows(final Connection.Response response) throws IOException {
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
        if (rows.isEmpty()) {
            return null;
        }
        return rows;
    }

    public static boolean parseRows(final Elements rows) {
        for (Element row : rows) {
            if (row.childrenSize() != 3) {
                return false;
            }

            String currencyName = row.child(0).text();
            final Matcher currencyNameMatcher = CURRENCY_NAME_PATTERN.matcher(currencyName);
            if (!currencyNameMatcher.find()) {
                return false;
            }

            currencyName = currencyName.replace(" ", "_");
            final String exchangeRate = row.child(1).text();
            final Matcher exchangeRateMatcher = EXCHANGE_RATE_PATTERN.matcher(exchangeRate);
            if (!exchangeRateMatcher.find()) {
                return false;
            }

            System.out.println(currencyName + " " + exchangeRate);
        }
        return true;
    }
}