package esnerda.keboola.components.configuration.parser;
/**
 * @author David Esner
 */
public interface OAuthResponseDataParser<T> {
	public T parseOAuthData(String data) throws Exception;
}
