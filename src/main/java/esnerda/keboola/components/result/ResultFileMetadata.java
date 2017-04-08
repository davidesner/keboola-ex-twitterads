package esnerda.keboola.components.result;

import java.io.File;

/**
 * @author David Esner
 */
public class ResultFileMetadata {
	
	private final File resFile;
	private final String[] idColums;
	public ResultFileMetadata(File resFile, String[] idColums) {
		super();
		this.resFile = resFile;
		this.idColums = idColums;
	}
	public File getResFile() {
		return resFile;
	}
	public String[] getIdColums() {
		return idColums;
	}
	
	

}
