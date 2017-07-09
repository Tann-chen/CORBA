package client;

import DCMS.CenterServer;
import DCMS.CenterServerHelper;
import org.omg.CosNaming.NamingContextExt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Manager{
	private String managerID;
	private CenterServer centerServerImp;
	private static File loggingFile=new File("Manager.txt");

	private static NamingContextExt ncRef;

	public Manager(String managerID){
		this.managerID = managerID;
		try{
			// resolve the Object Reference in Naming
			String name = managerID.substring(0,3);
			centerServerImp = CenterServerHelper.narrow(ncRef.resolve_str(name));
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}

	public static void setNamingContextExt(NamingContextExt namingContextExt){
		Manager.ncRef=namingContextExt;
	}

	public void createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location){
		boolean flag;
		flag=centerServerImp.createTRecord(managerID,firstName, lastName, address, phone, specialization, location);
		//log
		String log;
		if(flag)
			log=(new Date().toString())+" - "+managerID+ "- create teacher record - Success";
		else
			log=(new Date().toString())+" - "+managerID+ "- create teacher record - Fail";
		writelog(log);
	}

	public void createSRecord(String firstName, String lastName, String coursesRegistered, String status, String date){
		boolean flag;
		flag=centerServerImp.createSRecord(managerID,firstName, lastName, coursesRegistered, status, date);
		String log;
		if(flag)
			 log=(new Date().toString())+" - "+managerID+ "- create student record - Success";
		else
			log=(new Date().toString())+" - "+managerID+ "- create student record - Fail";
		writelog(log);
	}

	public void getRecordCounts(){
		String result;
		result=centerServerImp.getRecordCounts(managerID);
		String log=(new Date().toString())+" - "+managerID+ " - get records count - result:"+ result;
		writelog(log);
	}

	public void editRecord(String recordID, String fieldName, String newValue){
		boolean flag;
		flag=centerServerImp.editRecord(managerID,recordID, fieldName, newValue);
		//log
		String log;
		if(flag)
		 	log=(new Date().toString())+" - "+managerID+ "- edit record - "+recordID+" - Success";
		else
			log=(new Date().toString())+" - "+managerID+ "- edit record - "+recordID+" - Fail";
		writelog(log);
	}

	public void transferRecord(String recordID, String remoteCenterServerName){
		boolean flag;
		flag=centerServerImp.transferRecord(managerID,recordID,remoteCenterServerName);
		//log
		String log;
		if(flag)
			log=(new Date().toString())+" - "+managerID+ "- transfer record - "+recordID+" - Success";
		else
			log=(new Date().toString())+" - "+managerID+ "- transfer record - "+recordID+" - Fail";
		writelog(log);
	}

	private static void writelog(String log){
		try {
			synchronized (loggingFile) {
				FileWriter fileWriter = new FileWriter(loggingFile, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(log);
				bufferedWriter.newLine();
				bufferedWriter.close();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
