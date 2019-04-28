package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import graphics.Player;

public class MultiServer extends Thread {
	
	protected DatagramSocket socket = null;
	protected ByteArrayOutputStream baos;
	protected ObjectOutputStream oos;
    protected BufferedReader in = null;
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected Player p;
	protected boolean online = false;
	protected DatagramPacket packet;
	protected InetAddress address;
	protected int port;
	
	public MultiServer() {
		this("QuoteServerThread");
	}
	
	public MultiServer(String name) {
		super(name);
		try {
			socket = new DatagramSocket(4444);
			online = true;
			for(int i = 0 ; i < 10 ; i++) {
				players.add(new Player(0,-50,-50));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("Run()");
		while(online) {
			/*
			 * Receive packets from client
			 */
			byte[] dataReceive = new byte[128];
			packet = new DatagramPacket(dataReceive, dataReceive.length);
			try {
				socket.receive(packet);
				System.out.println("Receive packet");
			} catch (IOException e1) {
				System.err.println("Error while receiving packets from client");
				System.exit(1);
			}
			address = packet.getAddress();
			port = packet.getPort();
			
			ByteArrayInputStream bais = new ByteArrayInputStream(dataReceive);
			try {
				ObjectInputStream ois = new ObjectInputStream(bais);
				p = (Player) ois.readObject();
				
				//CHANGE THIS
				int id = p.getId();
				System.out.println("players ID:" + id);
				players.set(id, p);
				System.out.println("players:" + players.size());
			} catch (ClassNotFoundException | IOException e1) {
				System.err.println("Error while transforming packet into object");
				System.exit(1);
			}
			/*
			 * Sending packets to client
			 */

			baos = new ByteArrayOutputStream(1024);
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(players);
				byte[] data = baos.toByteArray();
				oos.reset();
				System.out.println(data.length);
				packet = new DatagramPacket(data, data.length, address, port);
				System.out.println("Send packet");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				socket.send(packet);
			} catch (IOException e) {
				System.err.println("Error while sending packets to client");
				System.exit(1);
			}
		}
		socket.close();
	}

}
