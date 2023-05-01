package dev.yudin.configs;

import dev.yudin.exceptions.AppConfigurationException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
public class InitDB {

	private DataSource dataSource;

	@Autowired
	public InitDB(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void init() {
		runScript(dataSource, "createTables.sql");
		runScript(dataSource, "populateTables.sql");
	}

	public void runScript(DataSource dataSource, String fileName) {
		try (Connection connection = dataSource.getConnection()) {
			ScriptRunner runner = new ScriptRunner(connection);
			InputStreamReader reader = new InputStreamReader(getFileFromResourceFolder(fileName));
			runner.runScript(reader);
		} catch (SQLException ex) {
			throw new AppConfigurationException("Could not get connection", ex);
		}
	}

	private InputStream getFileFromResourceFolder(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new AppConfigurationException("file not found! " + fileName);
		} else {
			return inputStream;
		}
	}
}
