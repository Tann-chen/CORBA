package server;

import java.io.File;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import DCMS.CenterServer;
import DCMS.CenterServerHelper;
import thread.UdpListener;

public class Laval {
	private static CenterServerImp centerServerSurvant;
	private static File logFile;

	public static void main(String[] args){

		try{
			// setup the logging file
			logFile =new File("lvl.txt");
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			// create servant and register it with the ORB
			centerServerSurvant= new CenterServerImp(logFile,"LVL");
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
			String name = "LVL";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			//create a new thread to listen udp requests between servers
			new UdpListener(centerServerSurvant).start();
			System.out.println("LVLServer ready and waiting ...");
			// wait for invocations from clients
			orb.run();
		}
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		//when client shutdown the CenterServer,orb stops waiting, and output
		System.out.println("Laval Server Exiting ...");
	}
}
