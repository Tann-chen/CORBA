package client;


import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class ManagerClient {

	public static void main(String[] args){
		try{
			ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", "-ORBInitialPort", "1050"}, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			//setup the NamingContextExt inside the Manager
			Manager.setNamingContextExt(ncRef);
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}

		String managerID;
		System.out.print("Enter Manager ID: ");
		Scanner input = new Scanner(System.in);
		managerID = input.nextLine();
		Manager themanager=new Manager(managerID);

		int user_input;

		do {
			System.out.print("==="+managerID+"=== \n"
					+"1.Create Teacher Record  \n"
					+"2.Create Student Record\n"
					+"3.Get Record Count \n"
					+"4.Editing Record  \n"
					+"5.Transfer Record  \n"
					+"6.Switch managerID \n"
					+"0.Quit \n"
					);
			input= new Scanner(System.in);
			String responseLine = input.nextLine();
			user_input = Integer.parseInt(responseLine.trim());

			boolean result;

			switch (user_input) {
				case 1:{
					System.out.println("Enter: firstName lastName address phone specialization location(mtl,lvl,do) ");
					String firstName = input.next();
					String lastName = input.next();
					String address = input.next();
					String phone = input.next();
					String specialization = input.next();
					String location = input.next();
					result=themanager.createTRecord(firstName,lastName, address, phone, specialization, location);
					if(result)
						System.out.println("Success");
					else
						System.out.println("Fail");
					break;
				}
				case 2: {
					System.out.println("Enter:firstName lastName courseRegistered status statusDate");
					String firstName = input.next();
					String lastName = input.next();
					String courseRegistered = input.next();
					String status = input.next();
					String statusDate = input.next();
					result=themanager.createSRecord(firstName, lastName, courseRegistered, status, statusDate);
					if(result)
						System.out.println("Success");
					else
						System.out.println("Fail");
					break;
				}
				case 3:{
					String consequence=themanager.getRecordCounts();
					System.out.println(consequence);
					break;
				}
				case 4:{
					System.out.println("Enter:recordID fieldName newValue");
					String recordID = input.next();
					String fieldName = input.next();
					String newValue = input.next();
					result=themanager.editRecord(recordID, fieldName, newValue);
					if(result)
						System.out.println("Success");
					else
						System.out.println("Fail");
					break;
				}
				case 5: {
					System.out.println("Enter:record ID destinationLocation");
					String recordID = input.next();
					String destinationLocation = input.next();
					result=themanager.transferRecord(recordID, destinationLocation);
					if(result)
						System.out.println("Success");
					else
						System.out.println("Fail");
					break;
				}
				case 6:{
					System.out.println("Enter: new managerID");
					input=new Scanner(System.in);
					managerID = input.nextLine();
					themanager=new Manager(managerID);
				}
				case 0:
					break;
				default:
					System.out.println("wrong input!!!!!");
			}

		} while (user_input != 0);
	}
}
