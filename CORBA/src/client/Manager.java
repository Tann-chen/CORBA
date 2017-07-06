package client;


import servers.CenterServer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Manager {
	private String managerID;
	private String name;
	private Registry registry;
	private CenterServer centerServer;
	private static File loggingFile=new File("Manager.txt");

	public Manager(String managerID, String name, String[] args){
		this.managerID = managerID;
		this.name = name;
		//distributing server

		String nameService;
		if(this.managerID.startsWith("MTL")){
			nameService="MTL";
		}
		else if(this.managerID.startsWith("LVL")){
			nameService="LVL";
		}
		else if(this.managerID.startsWith("DDO")){
			nameService="DDO";
		}
		else{
			System.out.println("Error:invalid managerID");
			return;
		}
		try{
	        // create and initialize the ORB
	        ORB orb = ORB.init(args, null);
	         // get the root naming context
	        org.omg.CORBA.Object objRef =
	            orb.resolve_initial_references(nameService);
	        // Use NamingContextExt instead of NamingContext. This is
	        // part of the Interoperable naming Service.
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	        // resolve the Object Reference in Naming
	        String message = "Hello";
	        helloImpl = HelloHelper.narrow(ncRef.resolve_str(message));
	         System.out.println("Obtained a handle on server object: " + helloImpl);
	        System.out.println(helloImpl.sayHello());
//	        helloImpl.shutdown();
	         } catch (Exception e) {
	          System.out.println("ERROR : " + e) ;
	          e.printStackTrace(System.out);
	          }
	}

	public void createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location){
		boolean flag=false;
		try {
			flag=centerServer.createTRecord(firstName, lastName, address, phone, specialization, location);
		}catch (RemoteException re){
			re.getStackTrace();
		}
		String log=(new Date().toString())+" | "+this.name + "- create teacher record - "+ String.valueOf(flag);
		writelog(log);
	}

	public void createSRecord(String firstName, String lastName, String coursesRegistered, String status, String date){
		boolean flag=false;
		try{
			flag=centerServer.createSRecord(firstName, lastName, coursesRegistered, status, date);
		}catch (RemoteException re) {
			re.getStackTrace();
		}
		String log=(new Date().toString())+" | "+this.name + "- create student record - "+ flag;
		writelog(log);
	}

	public void getRecordCounts() throws RemoteException {
			String result=null;
		try{
			result=centerServer.getRecordCounts();
		}catch (RemoteException re){
			re.getStackTrace();
		}
		String log=(new Date().toString())+" | "+this.name + "- get records count - "+ result;
		writelog(log);
		System.out.println(result);
	}

	public void editRecord(String recordID, String fieldName, String newValue) throws RemoteException {
		try {
			centerServer.editRecord(recordID, fieldName, newValue);
		}catch (RemoteException re){
			re.getStackTrace();
		}
		String log=(new Date().toString())+" | "+this.name + "- edit record - ";
		writelog(log);
	}

	private static void writelog(String log){
		try {
			FileWriter fileWriter = new FileWriter(loggingFile, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(log);
			bufferedWriter.newLine();
			bufferedWriter.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
