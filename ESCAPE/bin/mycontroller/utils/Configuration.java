package mycontroller.utils;

import mycontroller.MyAIController;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * A more convenient version of {@link java.util.Properties}
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class Configuration {

	/* Internal data storage */
	private Properties properties;

	private Configuration(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return True if there is a value associated with the given key.
	 */
	public boolean hasProperty(String key) {
		return properties.containsKey(key);
	}

	/**
	 * @return The value associated with the given key, or null if one does not exist.
	 * @see Configuration#hasProperty(String)
	 */
	public String get(String key) {
		return properties.getProperty(key);
	}

	/**
	 * @return the value assocated with the given key, parsed to an int. Or null if one does not exist.
	 * @see Configuration#hasProperty(String)
	 */
	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	/**
	 * @return A new builder object for this config.
	 */
	public static Configuration.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Properties properties = new Properties();

		/**
		 * Adds a default value to the config currently being built.
		 *
		 * @param key   Property key.
		 * @param value Property value.
		 * @return This builder (for chaining).
		 */
		public Builder withDefault(String key, Object value) {
			properties.setProperty(key, String.valueOf(value));
			return this;
		}

		/**
		 * Loads a properties file from the given location (relative to the MyAIController class in the classpath).
		 *
		 * @param resource resource location to load and parse.
		 * @return This builder (for chaining).
		 * @throws IOException If an error occurs when reading the properties file.
		 */
		public Builder load(String resource) throws IOException {
			try (InputStream is = MyAIController.class.getResourceAsStream(resource)) {
				this.properties.load(is);
				return this;
			}
		}

		public Builder loadFromString(String src) throws IOException {
			this.properties.load(new StringReader(src));
			return this;
		}

		/**
		 * @return A new config instance with all the state built up in this builder.
		 */
		public Configuration build() {
			return new Configuration(properties);
		}
	}
}
