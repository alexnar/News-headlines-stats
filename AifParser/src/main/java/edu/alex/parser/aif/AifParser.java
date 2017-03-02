package edu.alex.parser.aif;


import edu.alex.parser.api.Parser;
import edu.alex.urlreader.api.UrlReader;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * This is AifParser service which
 * provides parsing of RSS AiF.
 */
@Component(name = "Aif Parser", immediate = true)
@Service(value = Parser.class)
public class AifParser implements Parser {
    private static final String PARSER_NAME = "AiF";
    private static final String URL_AIF = "http://www.aif.ru/rss/news.php";

    @Reference
    private UrlReader urlReader;

    /**
     * Get all headlines from html.
     * This method parsing through xml.parser
     * Use UrlReader#getHtml(..)
     * Used by {@link AifParser#splitHeadlinesIntoWords()}
     *
     * @return List of headlines
     * @throws Exception if connection failed
     */
    @Override
    public List<String> getHeadLinesFromHtml() throws Exception {
        List<String> headLinesList = new LinkedList<>();
        String xml = urlReader.getHtml(URL_AIF);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("title");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element node = (Element) nodeList.item(i);
                headLinesList.add(node.getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * @param title - input string title
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
