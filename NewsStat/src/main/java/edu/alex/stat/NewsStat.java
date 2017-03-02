package edu.alex.stat;

import edu.alex.parser.api.Parser;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;

import java.util.*;

/**
 * News Stat service, provides methods for getting
 * statistic of most popular words in news sources.
 * This class support all sources for which
 * Service implementation of Parser registered.
 * To add new source you just need add
 * SourceNameParser service which implements Parser.
 */
@Component(name = "News stat", immediate = true)
@Service(value = Object.class)
@Properties({
        @Property(name = "osgi.command.scope", value = "news"),
        @Property(name = "osgi.command.function", value = "stats")
})
public class NewsStat {
    @Reference(cardinality = ReferenceCardinality.MANDATORY_MULTIPLE, bind = "bind", unbind = "unbind",
            referenceInterface = Parser.class, policy = ReferencePolicy.DYNAMIC)
    private Map<String, Parser> parserMap;
    private static final String ALL_OPTION = "all";

    public void stats() {
        String chosenOption = choseOption();
        stats(chosenOption);
    }

    /**
     * This method provides getting top 10 most frequent
     * words from source`s which implied by chosen option.
     * Break out if there is no such {@code optionName}.
     *
     * @param optionName - Could be {@value ALL_OPTION} or one of
     *                   registered Parser Service name which could
     *                   be gotten by: {@link Parser#getParserName()}
     */
    public void stats(String optionName) {
        List<String[]> headlinesWords = null;
        try {
            headlinesWords = getHeadlineWords(optionName);
            if (headlinesWords == null) {
                return;
            }
        } catch (Exception e) {
            System.out.println("Can't connect to chosen source");
        }
        Map<String, Integer> wordFrequencyMap = putHeadlineWordsInMap(headlinesWords);
        wordFrequencyMap = filterPrepositions(wordFrequencyMap);
        wordFrequencyMap = sortMap(wordFrequencyMap);
        Map<String, Integer> firstTenWordsMap = getFirstTenWords(wordFrequencyMap);
        System.out.println("Most popular words:");
        showMap(firstTenWordsMap);
    }

    /**
     * Print available options and allows you
     * to chose one of them.
     *
     * @return chosen option as String
     */
    private String choseOption() {
        System.out.println("Chose one of this options:");
        for (Map.Entry<String, Parser> parser : parserMap.entrySet()) {
            System.out.println(parser.getValue().getParserName());
        }
        System.out.println(ALL_OPTION);

        Scanner in = new Scanner(System.in);
        String chosenOption = in.nextLine();
        System.out.println("Your chose:" + chosenOption);
        return chosenOption.toLowerCase();
    }

    /**
     * This method list of headlines words arrays, from choosen
     * sources
     *
     * @param chosenOption - Could be {@value ALL_OPTION} or one of
     *                     registered Parser Service name which could
     *                     be gotten by: {@link Parser#getParserName()}
     * @return null if {@code chosenOption} is wrongly specified
     * otherwise return list of headlines words arrays
     * @throws Exception - if connection failed
     */
    private List<String[]> getHeadlineWords(String chosenOption) throws Exception {
        List<String[]> headlinesWords = new LinkedList<>();
        if (chosenOption.equals(ALL_OPTION)) {
            for (Map.Entry<String, Parser> parser : parserMap.entrySet()) {
                headlinesWords.addAll(parser.getValue().splitHeadlinesIntoWords());
            }
        } else {
            Parser parser = parserMap.get(chosenOption);
            if (parser == null) {
                System.out.println("WARNING: THERE IS NO SUCH OPTION: " + chosenOption + " !!!");
                return null;
            }
            headlinesWords.addAll(parser.splitHeadlinesIntoWords());
        }
        return headlinesWords;
    }

    /**
     * Get list of headlines words arrays, and converts it
     * to frequency map, where key is the word, and value
     * is the Integer value of this word frequency
     *
     * @param headlinesWords - list of headlines words arrays
     * @return - word frequency map
     */
    private Map<String, Integer> putHeadlineWordsInMap(List<String[]> headlinesWords) {
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        for (String[] words : headlinesWords) {
            for (String word : words) {
                Integer wordCount = wordFrequencyMap.get(word);
                if (wordCount == null) {
                    wordCount = 1;
                } else {
                    ++wordCount;
                }
                wordFrequencyMap.put(word, wordCount);
            }
        }
        return wordFrequencyMap;
    }

    /**
     * This is simple filter that try to divide
     * prepositions from words. This method assume
     * that prepositions are all words whose
     * length <= 2.
     * Warning: this is very simple filter, it could
     * make mistakes.
     *
     * @param map non filtered map, which contain
     *            prepositions, conjunctions and so on.
     * @return filtered map without prepositions,
     * conjunctions and so on.
     */
    private Map<String, Integer> filterPrepositions(Map<String, Integer> map) {
        Map<String, Integer> filteredMap = new HashMap<>();
        int nonWordsLength = 2;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getKey().length() > nonWordsLength) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredMap;
    }

    /**
     * Sort map by its value by descending.
     *
     * @param inputMap unsorted map
     * @return sorted LinkedHashMap
     */
    private Map<String, Integer> sortMap(Map<String, Integer> inputMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(inputMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * This method get sored map and return new
     * map, which contain top 10 most popular words
     *
     * @param wordFrequencyMap - map, should be sorted
     * @return LinkedHashMap, sorted if wordFrequencyMap
     * was sorted
     */
    private Map<String, Integer> getFirstTenWords(Map<String, Integer> wordFrequencyMap) {
        int wordMaxCount = 10;
        int entryCounter = 0;
        Map<String, Integer> firstTenWordsMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            if (entryCounter > wordMaxCount) {
                break;
            }
            firstTenWordsMap.put(entry.getKey(), entry.getValue());
            entryCounter++;
        }
        return firstTenWordsMap;
    }

    private void showMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }


    protected void bind(Parser parser) {
        if (parserMap == null) {
            parserMap = new HashMap<>();
        }
        parserMap.put(parser.getParserName().toLowerCase(), parser);
    }

    protected void unbind(Parser parser) {
        parserMap.remove(parser.getParserName());
    }
}
