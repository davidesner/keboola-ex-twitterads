/*
 */
package esnerda.keboola.components.configuration.handler;

import java.io.File;
import java.util.List;

import esnerda.keboola.components.KBCException;
import esnerda.keboola.components.appstate.LastState;
import esnerda.keboola.components.configuration.IKBCParameters;
import esnerda.keboola.components.configuration.OAuthCredentials;
import esnerda.keboola.components.configuration.tableconfig.ManifestFile;
import esnerda.keboola.components.configuration.tableconfig.StorageTable;

/**
 *
 * author David Esner <code>&lt;esnerda at gmail.com&gt;</code>
 * created 2016
 */
public interface KBCConfigurationEnvHandler {

	public void processConfigFile(String[] args) throws KBCException;

    public void processConfigFile(File confFile) throws KBCException;

    public List<StorageTable> getInputTables() throws KBCException;

    public IKBCParameters getParameters();

    public void validateConfig(File confFile) throws KBCException;

    public void writeManifestFile(ManifestFile manifest) throws KBCException;

    public void writeStateFile(LastState stFile) throws KBCException;

    public LastState getStateFile() throws KBCException;

    public String getDataPath();

    public String getOutputTablesPath();

    public String getInTablesPath();

    public String getInPath();

    public String getOutPath();

    public OAuthCredentials getOAuthCredentials();
}
