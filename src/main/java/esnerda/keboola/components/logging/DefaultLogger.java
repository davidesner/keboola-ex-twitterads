/*
 */
package esnerda.keboola.components.logging;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import esnerda.keboola.components.KBCException;

/**
 *
 * author David Esner <esnerda at gmail.com>
 * created 2016
 */
public class DefaultLogger implements KBCLogger {

    private final Logger logger;
    private LoggerContext CTX;

    public DefaultLogger(Class c) {

        ConfigurationFactory.setConfigurationFactory(new CustomConfigurationFactory());

        logger = LogManager.getLogger(c.getName());

    }

    @Override
    public void log(int severity, String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause) {
        switch (severity) {
            case 0:
                logger.info(detailedMessage, cause);
                break;
            case 1:
                logger.warn(detailedMessage, cause);
                break;
            case 2:
                logger.error(detailedMessage, cause);
                break;
        }
        if (severity > 2) {
            logger.error(detailedMessage, cause);
        }
    }

    @Override
    public void log(KBCException ex) {
        log(ex.getSeverity(), ex.getMessage(), ex.getDetailedMessage(), null, (Exception) ex.getCause());
    }

    @Override
    public void info(String shortMessage, String detailedMessage, Map<String, String> additionalFields) {
        logger.info(detailedMessage);
    }

    @Override
    public void warning(String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause) {
        logger.warn(detailedMessage, cause);
    }

    @Override
    public void debug(String shortMessage, String detailedMessage, Map<String, String> additionalFields) {
        logger.debug(detailedMessage);
    }

    @Override
    public void error(String shortMessage, String detailedMessage, Map<String, String> additionalFields, Exception cause) {
        logger.error(detailedMessage, cause);
    }

    @Override
    public void setLevel(Level lvl) {
        switch (lvl) {
            case debug:
                CTX.getConfiguration().getRootLogger().setLevel(org.apache.logging.log4j.Level.DEBUG);
                break;

            case normal:
                CTX.getConfiguration().getRootLogger().setLevel(org.apache.logging.log4j.Level.ERROR);
        }
    }

    @Override
    public void log(int severity, String message, Exception cause) {
        log(severity, "", message, null, cause);
    }

    @Override
    public void info(String detailedMessage) {
        log(0, "", detailedMessage, null, null);
    }

    @Override
    public void warning(String detailedMessage, Exception cause) {
        log(1, "", detailedMessage, null, cause);
    }

    @Override
    public void error(String detailedMessage, Exception cause) {
        log(2, "", detailedMessage, null, cause);
    }

	@Override
	public Logger getLogger() {
		return logger;
	}

}
