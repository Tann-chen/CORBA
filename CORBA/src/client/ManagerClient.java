package client;


import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class ManagerClient {


	public static void main(String[] args){
		System.out.print("running");
		try{
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			//setup the NamingContextExt inside the Manager
			Manager.setNamingContextExt(ncRef);
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}

		Manager manager1=new Manager("MTL1111");
		manager1.createSRecord("chen1","A","Math","active","2008-10-02");
		manager1.getRecordCounts();

		Manager manager2=new Manager("DDO1111");
		manager2.createSRecord("chen2","B","English","active","2008-10-03");

		Manager manager3=new Manager("LVL1111");
		manager3.createSRecord("chen3","C","French","active","2008-10-04");
		manager3.getRecordCounts();

		manager1.shutdown();
		manager2.shutdown();
		manager3.shutdown();
	}
}
