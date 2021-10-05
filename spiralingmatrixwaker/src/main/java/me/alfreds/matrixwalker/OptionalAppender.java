package me.alfreds.matrixwalker;

/**
 * Simple class to make length and nullness checks when appending a separator to a <class>StringBuilder</class>
 */
class OptionalAppender {
    final CharSequence separator;

    /**
     * Construct with optional separator
     *
     * @param separator the char sequence to use between appended items
     */
    public OptionalAppender(CharSequence separator) {
        this.separator = separator;
    }

    /**
     * Append the string argument to the passed in StringBuilder instance with separator (unless string passed in is null or empty).
     *
     * @param sb StringBuilder to append to
     * @param checkMe String to append; may be empty or null
     */
    public void append(StringBuilder sb, String checkMe) {
    	if (checkMe != null && checkMe.length() != 0) {
    		sb.append(separator).append(checkMe);
    	}
    }
}
