package edu.alex.parser.api;

import java.util.List;

/**
 * This is service interface of Parser implementations.
 * Used by NewsStat service, which contain Map of
 * all Parser implementations.
 */
public interface Parser {
    /**
     * Get all headlines from html
     * Use UrlReader#getHtml(..)
     * Used by {@link Parser#splitHeadlinesIntoWords()}
     *
     * @return List of headlines
     * @throws Exception if connection failed
     */
    List<String> getHeadLinesFromHtml() throws Exception;

    /**
     * Split headlines into word array.
     * Use {@link Parser#getHeadLinesFromHtml()} to get
     * headlines list.
     *
     * @return List of headlines words arrays.
     * @throws Exception if connection failed
     */
    List<String[]> splitHeadlinesIntoWords() throws Exception;

    /**
     * Return specified name of source used by Parser
     *
     * @return String name
     */
    String getParserName();
}
