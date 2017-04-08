/*
 */
package esnerda.keboola.components.logging;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import esnerda.keboola.components.KBCException;

/**
 *
 * author David Esner <esnerda at gmail.com>
 * created 2016
 */
public interface KBCLogger {

    public void log(int severity, String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause);

    public void log(int severity, String message, Exception cause);

    public void log(KBCException ex);

    public void info(String shortMessage, String detailedMessage, Map<String, String> additionalFields);

    public void warning(String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause);

    public void error(String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause);

    public void info(String detailedMessage);

    public void warning(String detailedMessage, Exception cause);

    public void error(String detailedMessage, Exception cause);

    public void debug(String shortMessage, String detailedMessage, Map<String, String> additionalFields);

    public void setLevel(Level lvl);

    /**
     * Get underlying log4j logger
     * @return
     */
    public Logger getLogger();

    static enum Level {
        debug, normal
    }
}
