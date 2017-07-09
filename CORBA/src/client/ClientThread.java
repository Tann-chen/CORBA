package client;

public class ClientThread extends Thread{
    @Override
    public void run() {
        Manager manager4=new Manager("MTL2222");
        manager4.createTRecord("li4","D","Tupper street","123456789","Math","LVL");
        manager4.createSRecord("li5","E","math","active","2016-09-01");
        System.out.println("manager4 running");

        Manager manager5=new Manager("DDO2222");
        manager5.createTRecord("li6","F","Du fort","987654321","English","MTL");
        manager5.getRecordCounts();
        System.out.println("manager5 running");

        Manager manager6=new Manager("LVL2222");
        manager6.editRecord("SR10001", "coursesRegistered", "Spanish");
        System.out.println("manager6 running");
    }
}
