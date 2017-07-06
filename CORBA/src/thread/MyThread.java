package thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import servers.CenterServer;
import servers.CenterServerImp;

public class MyThread extends Thread{
	InetAddress address;
	int port;
	DatagramSocket datagramSocket;
	CenterServerImp centerServerImp;

	public MyThread(InetAddress address, int port, DatagramSocket datagramSocket, CenterServer centerServer){
		this.address = address;
		this.port = port;
		this.datagramSocket = datagramSocket;
		this.centerServerImp = (CenterServerImp) centerServer;
	}

	
	@Override
	public void run() {
		int num = centerServerImp.getLocalRecordsCount();
		byte[] message = String.valueOf(num).getBytes();

		try {
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			datagramSocket.send(reply);
			System.out.println("reply the count");
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(datagramSocket != null)
				datagramSocket.close();
		}

	}

}
