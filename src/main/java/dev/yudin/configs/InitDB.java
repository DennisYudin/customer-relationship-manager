package dev.yudin.configs;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
public class InitDB {
	private DataSource adminDataSource;
	private DataSource dataSource;

	@Autowired
	public InitDB(@Qualifier("adminDataSource") DataSource adminDataSource,
				  @Qualifier("dataSource") DataSource dataSource) {
		this.adminDataSource = adminDataSource;
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void init() throws SQLException {
	//todo try to solve problem with force
//		PreparedStatement statement = null;
//		try (Connection conn = adminDataSource.getConnection()) {
//			statement = conn.prepareStatement(
//					"SELECT 'CREATE DATABASE eventholderdb'\n" +
//					"WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'eventholderdb')");
//
//
//			var isExist = statement.execute();
//			if (isExist == 0) {
//				statement = conn.prepareStatement("create database eventholderdb");
//				statement.executeUpdate();
//			}
//		} finally {
//			Objects.requireNonNull(statement).close();
//		}
		//todo perfect picture run scripts like this
//		runScript(adminDataSource, "createDBlink.sql");
//		runScript(adminDataSource, "createDB.sql");

		runScript(dataSource, "createTables.sql");
		runScript(dataSource, "populateTables.sql");
	}

	public void runScript(DataSource dataSource, String fileName) {
		try (Connection connection = dataSource.getConnection()) {
			ScriptRunner runner = new ScriptRunner(connection);
			InputStreamReader reader = new InputStreamReader(getFileFromResourceFolder(fileName));
			runner.runScript(reader);
		} catch (SQLException ex) {
			throw new RuntimeException("Could not get connection", ex);
		}
	}

	private InputStream getFileFromResourceFolder(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new RuntimeException("file not found! " + fileName);
		} else {
			return inputStream;
		}
	}
}
