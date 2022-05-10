import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScrapper {
    public static void main(String[] args) {
        String url = "https://www.x-rates.com/table/?from=USD&amount=1";
        if (getPage(url)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    public static boolean getPage(String url) {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(10000).execute();
            if (response.statusCode() != 200) {
                return false;
            }
            if (!response.contentType().contains("text/html")) {
                return false;
            }
            if (!response.body().contains("US Dollar Exchange Rates Table")) {
                return false;
            }
            if (!parsePage(response)) {
                return false;
            }
        } catch (IOException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean parsePage(Connection.Response response) {
        try {
            Document document = response.parse();
            Elements tables = document.select(".tablesorter.ratesTable");

            if (tables.size() != 1) {
                return false;
            }

            Element table = tables.first();

            if (table.childrenSize() < 2) {
                return false;
            }

            Element tbody = table.child(1);

            if (tbody.childrenSize() < 1) {
                return false;
            }

            Elements rows = tbody.children();

            if (rows.size() < 1) {
                return false;
            }

            Pattern exchangeRatePattern = Pattern.compile("^\\d+(\\.\\d)\\d*$");
            Pattern currencyNamePattern = Pattern.compile("^[A-Z][a-z]+( [A-Z][a-z]+)*$");
            Matcher exchangeRateMatcher;
            Matcher currencyNameMatcher;

            for (Element row : rows) {
                if (row.childrenSize() != 3) {
                    return false;
                }

                String currency = row.child(0).text();
                currencyNameMatcher = currencyNamePattern.matcher(currency);

                if (!currencyNameMatcher.find()) {
                    return false;
                }

                currency = currency.replace(" ", "_");

                String exchangeRate = row.child(1).text();
                exchangeRateMatcher = exchangeRatePattern.matcher(exchangeRate);

                if (!exchangeRateMatcher.find()) {
                    return false;
                }

                System.out.println(currency + " " + exchangeRate);
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
