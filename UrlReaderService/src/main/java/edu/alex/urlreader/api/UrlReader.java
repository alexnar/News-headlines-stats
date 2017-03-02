package edu.alex.urlreader.api;

/**
 * Service Interface.
 * Parser implementations have reference
 * for this interfac.
 */
public interface UrlReader {
    /**
     * Connect to url and get html as String value.
     * Used by Parser implementations.
     *
     * @param urlToRead - url of page, which html we want to get
     * @return html as String
     * @throws Exception if connection failed
     */
    String getHtml(String urlToRead) throws Exception;
}
