package esnerda.keboola.ex.twitterads.result.wrapper;

import esnerda.keboola.components.result.impl.DefaultBeanResultWriter;

/**
 * @author David Esner
 */
public class AppCardResultWriter extends DefaultBeanResultWriter<AppDownloadCardWrapper> {

	public AppCardResultWriter(String resFileName, String[] idCols) {
		super(resFileName, idCols);
	}

	@Override
	protected void initHeader(AppDownloadCardWrapper type) {

		this.header = AppDownloadCardWrapper.COLUMNS;
	}
}
