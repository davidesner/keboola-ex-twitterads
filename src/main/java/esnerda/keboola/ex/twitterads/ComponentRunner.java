package esnerda.keboola.ex.twitterads;

import java.util.List;

import esnerda.keboola.components.KBCException;
import esnerda.keboola.components.appstate.LastState;
import esnerda.keboola.components.configuration.handler.KBCConfigurationEnvHandler;
import esnerda.keboola.components.configuration.tableconfig.ManifestFile;
import esnerda.keboola.components.logging.KBCLogger;
import esnerda.keboola.components.result.ResultFileMetadata;

/**
 * @author David Esner
 */
public abstract class ComponentRunner {

	protected long startTime;

	protected KBCConfigurationEnvHandler handler;

	protected abstract void run() throws Exception;
		
	protected abstract KBCConfigurationEnvHandler initHandler(String[] args, KBCLogger log);

	protected abstract void initWriters() throws Exception;

	public abstract KBCLogger getLogger();

	protected abstract long getTimeout();

	protected abstract ManifestFile generateManifestFile(ResultFileMetadata result) throws KBCException;

	protected void setHandler(KBCConfigurationEnvHandler handler) {
		this.handler = handler;
	}

	protected KBCConfigurationEnvHandler getHandler() {
		return this.handler;
	}

	protected void handleException(KBCException ex) {
		getLogger().log(ex);
		if (ex.getSeverity() > 0) {
			System.exit(ex.getSeverity() - 1);
		}
	}

	protected void saveResults(List<ResultFileMetadata> results) throws KBCException {
		for (ResultFileMetadata res : results) {
			getHandler().writeManifestFile(generateManifestFile(res));
		}
	}

	protected void finalize(List<ResultFileMetadata> results, LastState thisState) {
		try {
			saveResults(results);

			getHandler().writeStateFile(thisState);
		} catch (KBCException e) {
			handleException(e);
		}
	}

	/* -- time counter methods -- */
	protected void startTimer() {
		startTime = System.currentTimeMillis();
	}

	protected boolean isTimedOut() {
		long elapsed = System.currentTimeMillis() - startTime;
		return elapsed >= getTimeout();
	}
}
