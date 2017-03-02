package edu.alex.urlreader.impl;

import edu.alex.urlreader.api.UrlReader;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service for reading html by url.
 * Used by Parser implementations,
 * as Reference to interface
 */
@Component(name = "UrlReaderService", immediate = true)
@Service(value = UrlReader.class)
public class UrlReaderImpl implements UrlReader {
    /**
     * Connect to url and get html as String value.
     * Used by Parser implementations.
     *
     * @param urlToRead - url of page, which html we want to get
     * @return html as String
     * @throws Exception if connection failed
     */
    public String getHtml(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }
}
