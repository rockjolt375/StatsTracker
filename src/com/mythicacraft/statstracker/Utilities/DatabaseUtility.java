package com.mythicacraft.statstracker.Utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseUtility {
	
	private final Logger logger = Logger.getLogger("Minecraft");
	
	private String connUsername, connPassword, connHost, connPort, connDatabase, tableName;
	private Connection conn;
	private ResultSet result;
	private PreparedStatement queryStatement;

	/**
	 * Constructs the DatabaseUtility object
	 * <p>
	 * Uses provided information for database connectivity
	 * 
	 * @param config		- config.yml file for database info
	 * @param tableName		- Table where information is stored
	 */
	public DatabaseUtility(FileConfiguration config, String tableName){
		connUsername = config.getString("username");
		connPassword = config.getString("password");
		connHost = config.getString("host");
		connPort = config.getString("port");
		connDatabase = config.getString("dbase");
		this.tableName = tableName;
	}
	
	/**
	 * Constructs the DatabaseUtility object
	 * <p>
	 * Uses provided information for database connectivity
	 * 
	 * @param username		- Username for database access
	 * @param password		- Password for database access
	 * @param host			- Host IP //ex: box777.bluehost.com
	 * @param port			- SQL port //default: 3306
	 * @param databaseName	- Name of the database for access
	 * @param tableName		- Table where information is stored
	 */
	public DatabaseUtility(String username, String password, String host, String port,
			String databaseName, String tableName){
		this.connUsername = username;
		this.connPassword = password;
		this.connHost = host;
		this.connPort = port;
		this.connDatabase = databaseName;
		this.tableName = tableName;
	}
	
	/**
	 * Connects to the database provided and returns:
	 * <p>
	 * True: If successful connection is made<br>
	 * False: If connection could not be made
	 * 
	 * @throws SQLException
	 */
	public boolean connect(){
		try{
	        String sqlURL = "jdbc:mysql://" + connHost + ":" + connPort + "/" + connDatabase;
			conn = DriverManager.getConnection(sqlURL, connUsername, connPassword);
			return true;
		}
		catch(SQLException e){logger.severe("Database connection could not be established. Please " + 
				"check connection data."); return false;}
	}
	
	/**
	 * Closes database connection in full
	 * 
	 * This should be called every time data is done being worked with
	 * <p>
	 * Closes open Query statements and ResultSets along with database
	 * connection.
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException{
		if(queryStatement != null) queryStatement.close(); // Closes statement
		if(result != null) result.close(); // Closes ResultSet
		conn.close(); // Closes the connection
	}	
	
	/**
	 * Checks database for existing table
	 * <p>
	 * If the provided table is not found, this method creates
	 * a new table with the table column information provided.
	 * <p>
	 * Table column format as follows:<br>
	 * [columnName] INT AUTO_INCREMENT KEY **Primary Key<br>
	 * [columnName] varchar(255) NOT NULL **String, required<br>
	 * [columnName] int **Int column, can be null
	 * <p>
	 * Other column types include: BIGINT, SMALLINT, DOUBLE, DECIMAL,
	 * DATE, MEDIUMTEXT, CHAR...
	 * <p>
	 * Example:
	 * "issue_id INT AUTO_INCREMENT KEY, player varchar(255) NOT NULL,
	 * status int, reason varchar(255)"
	 * 
	 * @param tableColumns	- Columns to be created in new table
	 * @throws SQLException Exception required for SQL connections
	 */
	public void CreateTable(String tableColumns) throws SQLException { 
		connect();
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, tableName, null);
		this.logger.info("Checking for database table....");
		if (!tables.next()) {
			this.logger.info("Table not found, creating table...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE " + tableName + "(" + tableColumns + ")");
		}
		else
			this.logger.info("Database found! Continuing...");
		close();
	}
	
	/**
	 * Retrieves rows according to the parameters given
	 * <p>
	 * Example of parameters:<br>
	 * "[columnName] = value"<br>
	 * "[columnName] = value AND [columnName2] = value2"
	 * <p>
	 * Normal operators can be used: =, <>, >, >=, etc
	 * 
	 * @param params	- Parameters to search the table for
	 * @return			Returns the result set containing player info
	 * @throws SQLException
	 */
	public ResultSet getRows(String params) throws SQLException{
		connect();
		if(params == null) params = "1";
		queryStatement = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + params);
		result = queryStatement.executeQuery();
		return result;
	}

	/**
	 * Adds row to the database with the parameters given
	 * <p>
	 * Adds params in order of columns disregarding the first
	 * column (which is typically and should be the primary
	 * key) A prior knowledge of your database structure should
	 * be known before adding rows
	 * 
	 * @param params	Data values to be entered separated by commas
	 * @throws SQLException
	 */
	public void addRows(String params) throws SQLException{
		connect();
		queryStatement = conn.prepareStatement("INSERT INTO " + tableName + "(" + getColumnNamesString() +
				") VALUES (" + params + ")");
		queryStatement.executeQuery();
		close();
	}
	
	/**
	 * Updates the specified row with new values
	 * <p>
	 * Changes the changing column to the new value given based
	 * on the identifying columns and identifying value conditional
	 * 
	 * @param idColumn		 - Column to identify the row to change
	 * @param idValue		 - Value to idenitfy the row to change
	 * @param columnToChange - Column to hold the new value
	 * @param newValue		 - Value to be inserted into column
	 * @throws SQLException
	 */
	public void updateRow(String idColumn, String idValue, String columnToChange, String newValue)
			throws SQLException{
		connect();
		queryStatement = conn.prepareStatement("UPDATE " + tableName + " SET " + columnToChange + "=" +
				newValue + " WHERE " + idColumn + "=" + idValue);
		queryStatement.executeQuery();
		close();
	}
	
	/**
	 * Removes row from the database
	 * <p>
	 * Given a column=value, said row(s) will be removed
	 * from the database permanently
	 * 
	 * @param columnName	- Column to identify row to remove
	 * @param value			- Value to identify row to remove
	 * @throws SQLException
	 */
	public void removeRow(String columnName, String value) throws SQLException{
		connect();
		queryStatement = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + columnName + "=" +
				value);
		queryStatement.executeQuery();
		close();
	}
	
	/**
	 * Returns ArrayList of column names from provided table
	 * 
	 * @return	ArrayList of String values containing column names
	 * @throws SQLException
	 */
	public ArrayList<String> getColumnNames() throws SQLException{
		ResultSet result = getRows(null);
		ArrayList<String> columns = new ArrayList<String>();
		ResultSetMetaData rsmd = result.getMetaData();
		for(int i=1; i<rsmd.getColumnCount(); i++){
			columns.add(rsmd.getColumnName(i));
		}
		close();
		return columns;
	}
	
	/**
	 * Returns String of column names from provided table
	 * 
	 * @return String of column names separated by commas
	 * @throws SQLException
	 */
	public String getColumnNamesString() throws SQLException{
		ArrayList<String> columns = getColumnNames();
		String columnString = "";
		for(int i=0; i<columns.size(); i++){
			columnString += "," + columns.get(i);
		}
		return columnString.substring(1);
	}
}