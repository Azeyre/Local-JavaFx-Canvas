package graphics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {
	
	private static String IP;
	
	public static void main(String[] args) {
		IP = (String) JOptionPane.showInputDialog(null,"Ip du serveur : ","IP",JOptionPane.QUESTION_MESSAGE,null,null,"");
		Application.launch();
		timer.cancel();
		System.exit(0);
	}

	private final int WIDTH = 400, HEIGHT = 400;
	private final int PORT = 4444;
	
	Canvas cv;
	GraphicsContext gc;
	Player j1, j2;

	boolean isServer = true; //false if Client
	ObjectOutputStream out;
	ObjectInputStream in;
	Socket socket;

	static Timer timer;

	public void createClient() throws UnknownHostException, IOException {
		socket = new Socket(IP, PORT);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		// socket.setTcpNoDelay(true);
	}

	public void start(Stage stage) throws IOException, ClassNotFoundException {
		j1 = new Player(1, 200, 200);
		createClient();

		Group root = new Group();
		cv = new Canvas(WIDTH, HEIGHT);

		root.getChildren().add(cv);
		gc = cv.getGraphicsContext2D();

		Scene scene = new Scene(root);

		scene.setOnKeyPressed(e -> {
			KeyCode kc = e.getCode();
			if (kc.equals(KeyCode.LEFT)) {
				j1.setX(j1.getX() - 5);
			} else if (kc.equals(KeyCode.RIGHT)) {
				j1.setX(j1.getX() + 5);
			} else if (kc.equals(KeyCode.UP)) {
				j1.setY(j1.getY() - 5);
			} else if (kc.equals(KeyCode.DOWN)) {
				j1.setY(j1.getY() + 5);
			}
			try {
				out.writeObject(j1);
				out.reset();
			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
			showPlayers();
		});
		showPlayers();
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.show();
		loop();
	}

	private void loop() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (in != null) {
					try {
						j2 = (Player) in.readObject();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
					showPlayers();
				}
			}

		}, 0, 10);
	}

	private void showPlayers() {
		gc.clearRect(0, 0, WIDTH, HEIGHT);
		gc.setFill(Color.RED);
		gc.setStroke(Color.BLACK);
		gc.fillRect(j1.getX(), j1.getY(), j1.getSize(), j1.getSize());
		gc.strokeRect(j1.getX(), j1.getY(), j1.getSize(), j1.getSize());
		
		if(j2 != null) {
			gc.setFill(Color.PURPLE);
			gc.setStroke(Color.BLACK);
			gc.fillRect(j2.getX(), j2.getY(), j2.getSize(), j2.getSize());
			gc.strokeRect(j2.getX(), j2.getY(), j2.getSize(), j2.getSize());
		}
	}
}
