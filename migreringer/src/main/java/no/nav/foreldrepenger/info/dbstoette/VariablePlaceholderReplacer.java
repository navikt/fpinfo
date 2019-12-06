package no.nav.foreldrepenger.info.dbstoette;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO(Humle): skriv tester
 */
public class VariablePlaceholderReplacer {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";

    @SuppressWarnings("rawtypes")
    private Map placeholders;

    public VariablePlaceholderReplacer(@SuppressWarnings("rawtypes") Map placeholders) {
        this.placeholders = placeholders;
    }

    @SuppressWarnings("rawtypes")
    public String replacePlaceholders(String input) {
        String processedInput = input;

        String searchTerm;
        String value;
        Map myPlaceholders = placeholders;

        for (Iterator itr = myPlaceholders.keySet().iterator(); itr
                .hasNext(); processedInput = replaceAll(processedInput, searchTerm, value == null ? "" : value)) {
            String placeholder = (String) itr.next();
            searchTerm = PLACEHOLDER_PREFIX + placeholder + PLACEHOLDER_SUFFIX;
            value = (String) myPlaceholders.get(placeholder); //NOSONAR
        }

        checkForUnmatchedPlaceholderExpression(processedInput);
        return processedInput;
    }

    private String replaceAll(String str, String originalToken, String replacementToken) {
        return str.replaceAll(Pattern.quote(originalToken), Matcher.quoteReplacement(replacementToken));
    }

    private void checkForUnmatchedPlaceholderExpression(String input) {
        String regex = Pattern.quote(PLACEHOLDER_PREFIX) + "(.+?)" + Pattern.quote(PLACEHOLDER_SUFFIX);
        Matcher matcher = Pattern.compile(regex).matcher(input);
        TreeSet<String> unmatchedPlaceHolderExpressions = new TreeSet<>();

        while (matcher.find()) {
            unmatchedPlaceHolderExpressions.add(matcher.group());
        }

        if (!unmatchedPlaceHolderExpressions.isEmpty()) {
            throw new IllegalStateException("Ingen verdi funnet for placeholder: "
                    + String.join(", ", unmatchedPlaceHolderExpressions) + ".  Sjekk miljøvariabler");
        }
    }

}
