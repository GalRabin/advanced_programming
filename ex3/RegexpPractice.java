// Gal Rabin 312473721
package ex3;

import java.util.*;
import java.util.regex.*;

/**
 * This class is only used to practice regular expressions.
 *
 * @author talm
 */
public class RegexpPractice {
    /**
     * Search for the first occurrence of text between single quotes, return the text (without the quotes).
     * Allow an empty string. If no quoted text is found, return null.
     * Some examples :
     * <ul>
     * <li>On input "this is some 'text' and some 'additional text'" the method should return "text".
     * <li>On input "this is an empty string '' and another 'string'" it should return "".
     * </ul>
     *
     * @param input
     * @return the first occurrence of text between single quotes
     */
    public static String findSingleQuotedTextSimple(String input) {
        Pattern p = Pattern.compile("'([^']*)'");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Search for the first occurrence of text between double quotes, return the text (without the quotes).
     * (should work exactly like {@link #findSingleQuotedTextSimple(String)}), except with double instead
     * of single quotes.
     *
     * @param input
     * @return the first occurrence of text between double quotes
     */
    public static String findDoubleQuotedTextSimple(String input) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Search for the all occurrences of text between single quotes <i>or</i> double quotes.
     * Return a list containing all the quoted text found (without the quotes). Note that a double-quote inside
     * a single-quoted string counts as a regular character (e.g, on the string [quote '"this"'] ["this"] should be returned).
     * Allow empty strings. If no quoted text is found, return an empty list.
     *
     * @param input
     * @return
     */
    public static List<String> findDoubleOrSingleQuoted(String input) {
        List<String> quoted = new ArrayList<String>();
        Pattern p1 = Pattern.compile("'([^']*)'");
        Matcher m1 = p1.matcher(input);
        Pattern p2 = Pattern.compile("\"([^\"]+?)\"");
        Matcher m2 = p2.matcher(input);
        boolean m1_exists = m1.find();
        boolean m2_exists = m2.find();
        // Iterate until no matches
        do {
            // check which of the pattern exists to be error prone
            if (m1_exists && m2_exists) {
                // Check if pattern p1 start before pattern p2
                if (m1.start() < m2.start()) {
                    // Check if pattern p2 subset of pattern p1
                    if (m2.start() < m1.end() - 1) {
                        quoted.add(m1.group(1));
                    }
                    // Check if pattern p2 isn't subset of p1 and add them in right order
                    else if (m2.start() >= m1.end()) {
                        quoted.add(m1.group(1));
                        quoted.add(m2.group(1));
                    }
                }
                // Check if pattern p2 start before pattern p1
                else if (m1.start() > m2.start()) {
                    // Check if pattern p1 subset of pattern p2
                    if (m1.start() < m2.end() - 1) {
                        quoted.add(m2.group(1));
                    }
                    // Check if pattern p1 isn't subset of p2 and add them in right order
                    else if (m1.start() >= m2.end()) {
                        quoted.add(m2.group(1));
                        quoted.add(m1.group(1));
                    }
                }
                // Search from last found char to end of string
                m1_exists = m1.find(m1.end() - 1);
                m2_exists = m2.find(m2.end() - 1);
            } else if (m1_exists) {
                quoted.add(m1.group(1));
                // Search from last found char to end of string
                m1_exists = m1.find(m1.end() - 1);
            } else if (m2_exists) {
                quoted.add(m2.group(1));
                // Search from last found char to end of string
                m2_exists = m2.find(m2.end() - 1);
            }

        } while (m1_exists || m2_exists);

        return quoted;
    }

    /**
     * Separate the input into <i>tokens</i> and return them in a list.
     * A token is any mixture of consecutive word characters and single-quoted strings (single quoted strings
     * may contain any character except a single quote).
     * The returned tokens should not contain the quote characters.
     * A pair of single quotes is considered an empty token (the empty string).
     * <p>
     * For example, the input "this-string 'has only three tokens'" should return the list
     * {"this", "string", "has only three tokens"}.
     * The input "this*string'has only two@tokens'" should return the list
     * {"this", "stringhas only two@tokens"}
     *
     * @param input
     * @return
     */
    public static List<String> wordTokenize(String input) {
        List<String> token_str = new ArrayList<String>();
        Pattern p_quoted = Pattern.compile("\\w*('[^']*')+\\w*");
        Matcher m_quoted = p_quoted.matcher(input);
        boolean m_qouted_exists = m_quoted.find();

        Pattern p_no_quote = Pattern.compile("\\w+");
        Matcher m_no_quote = p_no_quote.matcher(input);
        boolean m_no_quote_exists = m_no_quote.find();


        while (m_no_quote_exists || m_qouted_exists) {
            if (m_no_quote_exists && m_qouted_exists) {
                if (m_no_quote.start() >= m_quoted.start()) {
                    while (m_no_quote_exists) {
                        if (m_no_quote.start() < m_quoted.end() - 1) {
                            m_no_quote_exists = m_no_quote.find();
                            continue;
                        }
                        break;
                    }
                    token_str.add(input.substring(m_quoted.start(), m_quoted.end()).replaceAll("'", ""));
                    m_qouted_exists = m_quoted.find();
                } else if (m_no_quote.start() < m_quoted.start()) {
                    token_str.add(input.substring(m_no_quote.start(), m_no_quote.end()));
                    m_no_quote_exists = m_no_quote.find();
                    while (m_no_quote.start() < m_quoted.start()) {
                        token_str.add(input.substring(m_no_quote.start(), m_no_quote.end()));
                        m_no_quote_exists = m_no_quote.find();
                    }
                }
            } else if (m_no_quote_exists) {
                token_str.add(input.substring(m_no_quote.start(), m_no_quote.end()));
                m_no_quote_exists = m_no_quote.find();
            } else if (m_qouted_exists) {
                token_str.add(input.substring(m_quoted.start(), m_quoted.end()));
                m_qouted_exists = m_quoted.find();
            }
        }

        return token_str;
    }

    /**
     * Parse a date string with the following general format:<br>
     * Wdy, DD-Mon-YYYY HH:MM:SS GMT<br>
     * Where:
     * <i>Wdy</i> is the day of the week,
     * <i>DD</i> is the day of the month,
     * <i>Mon</i> is the month,
     * <i>YYYY</i> is the year, <i>HH:MM:SS</i> is the time in 24-hour format,
     * and <i>GMT</i> is a the constant timezone string "GMT".
     * <p>
     * You should also accept variants of the format:
     * <ul>
     * <li>a date without the weekday,
     * <li>spaces instead of dashes (i.e., "DD Mon YYYY"),
     * <li>case-insensitive month (e.g., allow "Jan", "JAN" and "jAn"),
     * <li>a two-digit year (assume it's between 1970 and 2069 in that case)
     * <li>a missing timezone
     * <li>allow multiple spaces wherever a single space is allowed.
     * </ul>
     * <p>
     * The method should return a java {@link Calendar} object with fields
     * set to the corresponding date and time. Return null if the input is not a valid date string. For validity
     * checking, consider only <i>local</i> validity: that is, only checks that don't require connecting different
     * parts of the date. For example, the weekday string "XXX" is invalid, but "Fri, 09-Jun-2015" is considered valid
     * even though June 9th was a Tuesday. In the same way, "40-Feb-2015" is invalid, but "31-Feb-2015" is ok because
     * you have to know the month is February in order to realize that 31 is not a possible day-of-the-month.
     *
     * @param input
     * @return
     */
    public static Calendar parseDate(String input) {
        List<String> months = Arrays.asList("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec");
        List<String> wdys = Arrays.asList("sun", "monday", "tue", "wed", "thu", "fri", "sat");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Pattern p = Pattern.compile("(sun|monday|tue|wed|thu|fri|sat)?,?\\s*(\\d{2})(\\s*|-)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(\\s*|-)(\\d{2,4})\\s+(\\d{1,2}):(\\d{2}):(\\d{2})\\s*(gmt)?$");
        Matcher m = p.matcher(input.toLowerCase());
        if (m.find()) {
            // setting day in wdy
            if (m.group(1) != null){
                cal.set(Calendar.DAY_OF_WEEK, wdys.indexOf(m.group(1)));
            }
            // setting day in month
            int day_in_month = Integer.parseInt(m.group(2));
            // setting month
            int month = months.indexOf(m.group(4));
            // setting year
            int year = Integer.parseInt(m.group(6));
            if (year >= 69 && year <= 99) {
                year = 1900 + year;
            } else if (year >= 0 && year <= 70) {
                year = 2000 + year;
            }
            // setting hours
            int hour = Integer.parseInt(m.group(7));
            // setting minutes
            int minute = Integer.parseInt(m.group(8));
            // setting seconds
            int second = Integer.parseInt(m.group(9));
            cal.set(year, month, day_in_month, hour, minute, second);
            return cal;
        }
        return null;
    }

    /**
     * Search for the all occurrences of text between single quotes, but treating "escaped" quotes ("\'") as
     * normal characters. Return a list containing all the quoted text found (without the quotes, and with the quoted escapes
     * replaced).
     * Allow empty strings. If no quoted text is found, return an empty list.
     * Some examples :
     * <ul>
     * <li>On input "'This is not wrong' and 'this is isn\'t either", the method should return a list containing
     * 		("This is not wrong" and "This isn't either").
     * <li>On input "No quoted \'text\' here" the method should return an empty list.
     * </ul>
     *
     * @param input
     * @return all occurrences of text between single quotes, taking escaped quotes into account.
     */
    public static List<String> findSingleQuotedTextWithEscapes(String input) {
        List<String> quoted_str = new ArrayList<String>();
        Pattern p = Pattern.compile("(^|[^\\\\]|\\G)'(((.*?)[^\\\\])??)'");
        Matcher m = p.matcher(input);
        while(m.find()) {
            quoted_str.add(m.group(2).replaceAll("\\\\'","'"));
        }

        return quoted_str;
    }

    /**
     * Search for the all occurrences of text between single quotes, but treating "escaped" quotes ("\'") as
     * normal characters. Return a list containing all the quoted text found (without the quotes, and with the quoted escapes
     * replaced).
     * Allow empty strings. If no quoted text is found, return an empty list.
     * Some examples :
     * <ul>
     * <li>On input "'This is not wrong' and 'this is isn\'t either", the method should return a list containing
     * 		("This is not wrong" and "This isn't either").
     * <li>On input "No quoted \'text\' here" the method should return an empty list.
     * </ul>
     *
     * @param input
     * @return all occurrences of text between single quotes, taking escaped quotes into account.
     */
    public static List<String> findDoubleQuotedTextWithEscapes(String input) {
        List<String> quoted_str = new ArrayList<String>();
        Pattern p = Pattern.compile("(^|[^\\\\]|\\G)\"(((.*?)[^\\\\])??)\"");
        Matcher m = p.matcher(input);
        while (m.find()) {
            quoted_str.add(m.group(2).replaceAll("\\\\\"", "\""));
        }

        return quoted_str;
    }

    /**
     * A class that holds an attribute-value pair.
     * <p>
     * Attributes are "HTTP tokens": a sequence of non-special non-whitespace
     * characters. The special characters are control characters, space, tab and the characters from the following set: <pre>
     * ()[]{}'"<>@,;:\/?=
     * </pre>
     * Values are arbitrary strings and may be missing.
     */
    public static class AVPair {
        public String attr;
        public String value;

        public AVPair(String attr, String value) {
            this.attr = attr;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || (!(obj instanceof AVPair)))
                return false;
            AVPair other = (AVPair) obj;
            if (attr == null) {
                if (other.attr != null)
                    return false;
            } else if (!attr.equals(other.attr))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            if (value != null)
                return attr + "=\"" + value.replaceAll("\"", "\\\"") + "\"";
            else
                return attr;
        }

    }

    /**
     * Parse the input into a list of attribute-value pairs.
     * The input should be a valid attribute-value pair list: attr=value; attr=value; attr; attr=value...
     * If a value exists, it must be either an HTTP token (see {@link AVPair}) or a double-quoted string.
     * <p>
     * (Note: for the second of the bonus question, implement the {@link RegexpPractice#parseAvPairs2(String)}
     * method.)
     *
     * @param input
     * @return
     */
    public static List<AVPair> parseAvPairs(String input) {
        // TODO: implement
        return null;
    }

    /**
     * Parse the input into a list of attribute-value pairs.
     * The input should be a valid attribute-value pair list: attr=value; attr=value; attr; attr=value...
     * If a value exists, it must be either an HTTP token (see {@link AVPair}) or a double-quoted string.
     * <p>
     * This  method should return null if the input is not a list of attribute-value pairs with the format specified above.
     * (this is for the second part of the bonus question).
     *
     * @param input
     * @return
     */
    public static List<AVPair> parseAvPairs2(String input) {
        // TODO: implement
        return null;
    }
}
