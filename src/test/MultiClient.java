package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import graphics.Player;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MultiClient extends Application {
		
	public static void main(String[] args) {
		Application.launch();
		timer.cancel();
		System.exit(0);
	}

	private final int WIDTH = 400, HEIGHT = 400;
	private final int PORT = 4444;
	private boolean connected;
	
	Canvas cv;
	GraphicsContext gc;
	ArrayList<Player> players = new ArrayList<Player>();
	DatagramSocket socket;
	InetAddress address;
	Player j1;

	ByteArrayOutputStream arrayOut;
	ByteArrayInputStream arrayIn;
	ObjectOutputStream out;
	ObjectInputStream in;

	static Timer timer;

	@SuppressWarnings("unchecked")
	public void createClient() throws UnknownHostException, IOException {
		j1 = new Player(4, 200, 200);
		socket = new DatagramSocket();
		address = InetAddress.getByName("192.168.1.21");
	}

	public void start(Stage stage) throws IOException, ClassNotFoundException {
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
			showPlayers();
		});
		showPlayers();
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.show();
		loop();
	}

	@SuppressWarnings("unchecked")
	private void loop() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					arrayOut = new ByteArrayOutputStream(128);
					out = new ObjectOutputStream(arrayOut);
					out.writeObject(j1);
					
					byte[] data = arrayOut.toByteArray();
					out.reset();

					////System.out.println("Sending packet");
					////System.out.println(data.length);
					DatagramPacket packet = new DatagramPacket(data, data.length, address, 4444);
					socket.send(packet);
					
					
					byte[] dataReceive = new byte[1024];
					packet = new DatagramPacket(dataReceive, dataReceive.length);
					try {
						socket.receive(packet);
						//System.out.println("Receive packet");
						//System.out.println(dataReceive.length);
					} catch (IOException e1) {
						System.err.println("Error while receiving packets from client");
						System.exit(1);
					}
					
					ByteArrayInputStream bais = new ByteArrayInputStream(dataReceive);
					try {
						ObjectInputStream ois = new ObjectInputStream(bais);
						players = (ArrayList<Player>) ois.readObject();
					} catch (ClassNotFoundException | IOException e1) {
						System.err.println("Error while transforming packet into object");
						System.exit(1);
					}
					showPlayers();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}, 0, 33);

	}

	private void showPlayers() {
		gc.clearRect(0, 0, WIDTH, HEIGHT);
		gc.setFill(Color.RED);
		gc.setStroke(Color.BLACK);

		gc.fillRect(j1.getX(), j1.getY(), j1.getSize(), j1.getSize());
		gc.strokeRect(j1.getX(), j1.getY(), j1.getSize(), j1.getSize());
		for(Player p: players) {
			if(p.getId() != j1.getId()) {

				gc.fillRect(p.getX(), p.getY(), p.getSize(), p.getSize());
				gc.strokeRect(p.getX(), p.getY(), p.getSize(), p.getSize());
			}
		}
	}

}
