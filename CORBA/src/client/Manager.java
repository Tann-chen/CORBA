package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import DCMS.CenterServer;
import DCMS.CenterServerHelper;


public class Manager {
	private String managerID;
	private String name;
	private CenterServer centerServerImp;
	private static File loggingFile = new File("Manager.txt");

	public Manager(String managerID, String name, String[] args){
		this.managerID = managerID;
		this.name = name;


		String nameContext;
		if(this.managerID.startsWith("MTL")){
			nameContext="MTL";
		}
		else if(this.managerID.startsWith("LVL")){
			nameContext="LVL";
		}
		else if(this.managerID.startsWith("DDO")){
			nameContext="DDO";
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
	            orb.resolve_initial_references("NameService");
	        // Use NamingContextExt instead of NamingContext. This is
	        // part of the Interoperable naming Service.
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	        // resolve the Object Reference in Naming

	        centerServerImp = CenterServerHelper.narrow(ncRef.resolve_str(nameContext));
	         System.out.println("Obtained a handle on server object: " + centerServerImp);
//	        System.out.println(centerServerImp.sayHello());
	         } catch (Exception e) {
	          System.out.println("ERROR : " + e) ;
	          e.printStackTrace(System.out);
	          }
	}

	public void createTRecord(String managerId, String firstName, String lastName, String address, String phone, String specialization, String location){
		boolean flag=false;
		flag=centerServerImp.createTRecord(managerId,firstName, lastName, address, phone, specialization, location);
		String log=(new Date().toString())+" | "+this.name + "- create teacher record - "+ String.valueOf(flag);
		writelog(log);
	}

	public void createSRecord(String managerId, String firstName, String lastName, String coursesRegistered, String status, String date){
		boolean flag=false;
		flag=centerServerImp.createSRecord(managerId,firstName, lastName, coursesRegistered, status, date);
		String log=(new Date().toString())+" | "+this.name + "- create student record - "+ flag;
		writelog(log);
	}

	public void getRecordCounts(String managerId) throws RemoteException {
			String result=null;
		result=centerServerImp.getRecordCounts(managerId);
		String log=(new Date().toString())+" | "+this.name + "- get records count - "+ result;
		writelog(log);
		System.out.println(result);
	}

	public void editRecord(String managerId,String recordID, String fieldName, String newValue) throws RemoteException {
		centerServerImp.editRecord(managerId,recordID, fieldName, newValue);
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
