package server;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import thread.MyThread;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import DCMS.CenterServer;
import DCMS.CenterServerHelper;



public class DDO {
	private static CenterServer centerServer;

	public static void main(String[] args) throws Exception {
//		File logFile = new File("ddo.txt");
//		centerServer= new CenterServerImp(logFile,"DDO");
//
//		Registry registry = LocateRegistry.createRegistry(3002);
//		registry.bind("DDOCenter", centerServer);
//		System.out.println("DDO start");
		
		try{
		      // create and initialize the ORB
		      ORB orb = ORB.init(args, null);
		      // get reference to rootpoa & activate the POAManager
		      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		      rootpoa.the_POAManager().activate();
		      // create servant and register it with the ORB
		      HelloImpl helloImpl = new HelloImpl();
		      helloImpl.setORB(orb);
		      // get object reference from the servant
		      org.omg.CORBA.Object ref = rootpoa.servant_to_reference(helloImpl);
		      CenterServer href = CenterServerHelper.narrow(ref);
		      // get the root naming context
		      // NameService invokes the name service
		      org.omg.CORBA.Object objRef =
		          orb.resolve_initial_references("NameService");
		      // Use NamingContextExt which is part of the Interoperable
		      // Naming Service (INS) specification.
		      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		    // bind the Object Reference in Naming
		      String name = "Hello";
		      NameComponent path[] = ncRef.to_name( name );
		      ncRef.rebind(path, href);
		         System.out.println("CenterServer ready and waiting ...");
		        // wait for invocations from clients
		      orb.run();
		    } 
		        catch (Exception e) {
		        System.err.println("ERROR: " + e);
		        e.printStackTrace(System.out);
		      }
		          System.out.println("CenterServer Exiting ...");
		        

	
		//listening to request
		DatagramSocket datagramSocket = null;
		try {
			//create belonging socket
			datagramSocket = new DatagramSocket(6789);
			byte[] buffer = new byte[1000];
			//listening
			System.out.println("DDO start listening");
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(request);
				new MyThread(request.getAddress(),request.getPort(),datagramSocket,centerServer).start();
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			if(datagramSocket != null)
				datagramSocket.close();
		}
		
		
	}
}
