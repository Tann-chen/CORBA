package server;

import java.io.File;

import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import DCMS.CenterServer;
import DCMS.CenterServerHelper;
import thread.UdpListener;


public class DDO {
	private static CenterServerImp centerServerSurvant;
	private static File logFile;

	public static void main(String[] args){

		try{
			// setup the logging file
			logFile = new File("ddo.txt");
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			// create servant and register it with the ORB
			centerServerSurvant= new CenterServerImp(logFile,"DDO");
			centerServerSurvant.setORB(orb);
			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(centerServerSurvant);
			CenterServer href = CenterServerHelper.narrow(ref);
			// get the root naming context
			// NameService invokes the name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// bind the Object Reference in Naming
			String name = "DDO";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			//define port number,create a new thread to listen udp requests between servers
			int port=6789;
			new UdpListener(centerServerSurvant,port).start();
			System.out.println("DDOServer ready and waiting ...");
			// wait for invocations from clients
			orb.run();
		}
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		//when client shutdown the CenterServer,orb stops waiting, and output
		System.out.println("DDO Server Exiting ...");
	}
}
