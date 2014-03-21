package ch.rmuerner.fxtest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import org.h2.tools.Csv;
import org.h2.tools.RunScript;
import org.h2.tools.Script;

public class MainController implements Initializable {

	private static final String JDBCSUBPROTOCOL = "jdbc:h2:";
	private static final String FOLDERFILE = "data/test";
	private static final String USER = "sa";
	private static final String PASSWORD = "sa";

	@FXML
	DatePicker datePicker;

	@FXML
	TextField txtFieldConnURL;
	@FXML
	TextField txtFieldUser;
	@FXML
	TextField txtFieldPW;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LocalDate date = LocalDate.now();
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(JDBCSUBPROTOCOL
					+ FOLDERFILE, USER, PASSWORD);
			Statement stat = conn.createStatement();

			// this line would initialize the database
			// from the SQL script file 'init.sql'
			// stat.execute("runscript from 'init.sql'");

			ResultSet rs;
			rs = stat.executeQuery("select * from test");
			while (rs.next()) {
				Date sqldate = rs.getDate("testdate");
				date = LocalDate.parse(sqldate.toString());
			}
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		datePicker.setValue(date);
	}

	@FXML
	public void actionSave() {
		// DeleteDbFiles.execute("data", "test", true);
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(JDBCSUBPROTOCOL
					+ FOLDERFILE, USER, PASSWORD);
			Statement stat = conn.createStatement();

			// this line would initialize the database
			// from the SQL script file 'init.sql'
			// stat.execute("runscript from 'init.sql'");
//			RunScript
//					.execute(conn, new FileReader("resources/dropTableH2.sql"));
			RunScript.execute(conn, new FileReader(
					"resources/createTableH2.sql"));
			stat.execute("INSERT INTO test(name, testDate) values('Hello', '"
					+ datePicker.getValue().toString() + "');");
			ResultSet rs;
			rs = stat.executeQuery("select * from test");
			while (rs.next()) {
				System.out.println(rs.getString("name") + ", "
						+ rs.getDate("testdate"));
			}
			stat.close();
			Script.execute(JDBCSUBPROTOCOL + FOLDERFILE, USER, PASSWORD,
					"data/backup.sql");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void actionTest() {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(
					txtFieldConnURL.getText(), txtFieldUser.getText(),
					txtFieldPW.getText());

			Statement stat = conn.createStatement();

			// this line would initialize the database
			// from the SQL script file 'init.sql'
			// stat.execute("runscript from 'init.sql'");

			// stat.execute("create table test(id int auto_increment, name varchar(255))");
			// stat.execute("insert into test(name) values('Hello')");
			ResultSet rs;
			rs = stat.executeQuery("select * from test");
			while (rs.next()) {
				System.out.println(rs.getString("name"));
			}
			stat.close();
			conn.close();
			txtFieldPW.setText("Connection OK");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read a CSV file.
	 */
	@FXML
	public void actionReadCSV() {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(JDBCSUBPROTOCOL
					+ FOLDERFILE, USER, PASSWORD);
			Statement stat = conn.createStatement();

			// this line would initialize the database
			// from the SQL script file 'init.sql'
			// stat.execute("runscript from 'init.sql'");

			ResultSet rs = new Csv().read("data/test.csv", null, null);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				StringBuilder sbColumns = new StringBuilder();
				StringBuilder sbValues = new StringBuilder();
				for (int i = 0; i < meta.getColumnCount(); i++) {

					if (i > 0) {
						sbColumns.append(", ");
						sbValues.append(", ");
					}
					sbColumns.append(meta.getColumnLabel(i + 1));
					sbValues.append("'" + rs.getString(i + 1) + "'");
				}
				String query = "INSERT INTO test(" + sbColumns.toString()
						+ ") values(" + sbValues.toString() + ");";
				System.out.println(query);
				stat.execute(query);
			}
			rs.close();

			ResultSet rsDB;
			rsDB = stat.executeQuery("SELECT * FROM test");
			while (rsDB.next()) {
				System.out.println(rsDB.getString("name") + ", "
						+ rsDB.getDate("testdate"));
			}
			
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
