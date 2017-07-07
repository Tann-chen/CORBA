package thread;

import server.CenterServerImp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener extends Thread{

    CenterServerImp centerServerImp;

    public UdpListener(CenterServerImp centerServerImp){
        this.centerServerImp=centerServerImp;
    }


    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {
            //create belonging socket
            datagramSocket = new DatagramSocket(6789);
            byte[] buffer = new byte[1000];
            System.out.println(centerServerImp.centerName+"is ready to listen UDP requests between servers");
            //listening
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(request);
                new UdpHandler(request.getAddress(),request.getPort(),datagramSocket,centerServerImp).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
    }
}
