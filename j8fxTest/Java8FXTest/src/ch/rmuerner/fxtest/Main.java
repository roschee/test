package ch.rmuerner.fxtest;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.h2.tools.Server;

public class Main extends Application {

	private static final String JDBCSUBPROTOCOL = "jdbc:h2:";
	private static final String FOLDERFILE = "data/test";
	private static final String USER = "sa";
	private static final String PASSWORD = "sa";

	private static Server server;

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass()
					.getResource("Main.fxml"));
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		startServer();
		launch(args);
		stopServer();
	}

	private static void startServer() {
		// Start Server
		// start the server, allows to access the database remotely
		try {
			System.out.println("Starting server...");
			server = Server.createTcpServer("-tcpPort", "9081");
			server.start();
			System.out.println("Server is up: " + server.isRunning(false));
			if (server.isRunning(false)) {
				System.out
						.println("You can access the database remotely now, using the URL:");
				System.out.println(JDBCSUBPROTOCOL + server.getURL() + "/"
						+ FOLDERFILE + " (user: " + USER + ", password: "
						+ PASSWORD + ")");

			}
		} catch (SQLException e) {
			System.out.println("Could not start server:");
			System.out.println(e.getMessage());
		}
	}

	private static void stopServer() {
		System.out.println("Shutdown server...");
		server.shutdown();
		server.stop();
		System.out.println("Server is down: " + !server.isRunning(false));
	}
}
