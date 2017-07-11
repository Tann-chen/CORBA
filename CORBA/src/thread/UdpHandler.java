package thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import records.Record;
import server.CenterServerImp;

public class UdpHandler extends Thread{
	private InetAddress address;
	private int port;
	private DatagramSocket datagramSocket;
	private CenterServerImp centerServerImp;
	private String operation;

	public UdpHandler(InetAddress address, int port, DatagramSocket datagramSocket, CenterServerImp centerServer,String operation){
		this.address = address;
		this.port = port;
		this.datagramSocket = datagramSocket;
		this.centerServerImp = centerServer;
		this.operation=operation;
	}

	@Override
	public void run() {
		byte[] message;
		if(operation.startsWith("$COUNT")){
			int num = centerServerImp.getLocalRecordsCount();
			message = String.valueOf(num).getBytes();
		}
		else{
			boolean flag;
			String[] temp=operation.split(",");
			if(temp[1].startsWith("TR"))
				flag=centerServerImp.addTRecord(temp[2],temp[3],temp[4],temp[5],temp[6],temp[7]);
			else if(temp[1].startsWith("SR"))
				flag=centerServerImp.addSRecord(temp[2],temp[3],temp[4],temp[5],temp[6]);
			else
				flag=false;

			if(flag)
				message="SUCCESS".getBytes();
			else
				message="FAIL".getBytes();
		}

		try {
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			datagramSocket.send(reply);
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(datagramSocket != null)
				datagramSocket.close();
		}
	}

}
