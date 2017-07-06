package client;


public class ManagerClient {

	public static void main(String[] args) throws Exception {

		Manager manager1=new Manager("MTL1111","manager1",args);
		manager1.createSRecord("MTL1111","chen1","A","Math","active","2008-10-02");
		manager1.getRecordCounts("MTL1111");

		Manager manager2=new Manager("DDO1111","manager2",args);
		manager2.createSRecord("DDO1111","chen2","B","English","active","2008-10-03");

		Manager manager3=new Manager("LVL1111","manager3",args);
		manager3.createSRecord("LVL1111","chen3","C","French","active","2008-10-04");
		manager3.getRecordCounts("LVL1111");

		Manager manager4=new Manager("MTL2222","manager4",args);
		manager4.createTRecord("MTL2222","li4","D","Tupper street","123456789","Math","LVL");
		manager4.createSRecord("MTL2222","li5","E","math","active","2016-09-01");

		Manager manager5=new Manager("DDO2222","manager5",args);
		manager5.createTRecord("DDO2222","li6","F","Du fort","987654321","English","MTL");
		manager5.getRecordCounts("DDO2222");
		
		Manager manager6=new Manager("LVL2222","manager6",args);
		manager6.editRecord("LVL2222","SR100", "coursesRegistered", "Spanish");
		
		manager1.createTRecord("MTL1111","li7","G","Guy","1357924680","Chinese","MTL");
		manager1.getRecordCounts("MTL1111");

		Manager manager7=new Manager("MTL3333","manager7",args);
		manager7.editRecord("MTL3333","SR10001", "coursesRegistered", "english");
		manager7.createTRecord("MTL3333","lli","xi","mtl","12121","xxx","mtl");
		manager7.getRecordCounts("MTL3333");


		Manager manager8=new Manager("DDO3333","manager8",args);
		manager8.editRecord("DDO3333","TR10001", "phone", "1239876540");
		manager8.getRecordCounts("DDO3333");
		manager8.createSRecord("DDO3333","ti","yi","math","active","2018-01-01");
		manager8.getRecordCounts("DDO3333");
		
		manager2.createSRecord("DDO1111","chen2","B","English","active","2008-10-03");
		manager2.getRecordCounts("DDO1111");
	}
}
