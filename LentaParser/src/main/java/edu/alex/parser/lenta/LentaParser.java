package edu.alex.parser.lenta;

import edu.alex.parser.api.Parser;
import edu.alex.urlreader.api.UrlReader;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * This is LentaParser service which
 * provides parsing of Lenta.ru API.
 */
@Component(name = "Lenta Parser", immediate = true)
@Service(value = Parser.class)
public class LentaParser implements Parser {
    private static final String PARSER_NAME = "Lenta";
    private static final String URL_LENTA = "https://api.lenta.ru/lists/latest";

    @Reference
    private UrlReader urlReader;

    /**
     * Get all headlines from html
     * This method parsing through JSON
     * Use UrlReader#getHtml(..)
     * Used by {@link LentaParser#splitHeadlinesIntoWords()}
     *
     * @return List of headlines
     * @throws Exception if connection failed
     */
    @Override
    public List<String> getHeadLinesFromHtml() throws Exception {
        List<String> headLinesList = new LinkedList<>();
        String jsonString = urlReader.getHtml(URL_LENTA);
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray headlines = jsonObject.getJSONArray("headlines");
        for (int i = 0; i < headlines.length(); i++) {
            JSONObject info = headlines.getJSONObject(i).getJSONObject("info");
            String title = info.getString("title");
            headLinesList.add(title);
        }
        return headLinesList;
    }

    /**
     * Split headlines into word array.
     * Use {@link Parser#getHeadLinesFromHtml()} to get
     * headlines list.
     *
     * @return List of headlines words arrays.
     * @throws Exception if connection failed
     */
    @Override
    public List<String[]> splitHeadlinesIntoWords() throws Exception {
        List<String[]> headLinesWordsList = new LinkedList<>();
        List<String> headLinesList = getHeadLinesFromHtml();
        for (String title : headLinesList) {
            headLinesWordsList.add(titleToWords(title));
        }
        return headLinesWordsList;
    }

    /**
     * Split title on words
     *
     * @param title
     * @return String array of title words
     */
    private String[] titleToWords(String title) {
        title = title.toLowerCase();
        title = title.replaceAll("[^\\wа-я]", " ");
        String[] words = title.split("\\s+");
        return words;
    }

    @Override
    public String getParserName() {
        return PARSER_NAME;
    }
}
