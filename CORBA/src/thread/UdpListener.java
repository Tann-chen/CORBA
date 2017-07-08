package thread;

import server.CenterServerImp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener extends Thread{

    private CenterServerImp centerServerImp;
    private int port;

    public UdpListener(CenterServerImp centerServerImp,int portNumber){
        this.centerServerImp=centerServerImp;
        this.port=portNumber;
    }


    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {
            //create belonging socket
            datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            System.out.println(centerServerImp.centerName+"is ready to listen UDP requests between servers");
            //listening
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(request);
                String requestStrng=new String(request.getData());
                    new UdpHandler(request.getAddress(),request.getPort(),datagramSocket,centerServerImp,requestStrng).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
    }
}
