package server;

import DCMS.CenterServerPOA;
import org.omg.CORBA.ORB;
import records.Record;
import records.StudentRecord;
import records.TeacherRecord;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class CenterServerImp extends CenterServerPOA{

    public String centerName;
    private HashMap<Character,ArrayList<Record>> storedRecords;
    private File loggingFile;
    private ORB orb;



    public CenterServerImp(File loggingFile,String centerName){
        storedRecords= new HashMap<Character,ArrayList<Record>>();
        this.loggingFile=loggingFile;
        this.centerName=centerName;
    }

    public void setORB(ORB orb_val){
        orb = orb_val;
    }

    @Override
    public void shutdown() {
        this.orb.shutdown(false);
    }

    @Override

    public  boolean  createTRecord(String managerId, String firstName, String lastName, String address, String phone, String specialization, String location) {
        TeacherRecord teacherRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);
        int beforeNum=getLocalRecordsCount();

        storingRecord(teacherRecord);
        //
        int afterNum=getLocalRecordsCount();
        //log
        String log=(new Date().toString()+" - "+managerId+" - creating a teacher record - "+teacherRecord.recordID);
        writeLog(log);
        return beforeNum+1<=afterNum;
    }


    @Override
    public  boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered, String status, String date) {
        StudentRecord studentRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, date);
        int beforeNum=getLocalRecordsCount();
        storingRecord(studentRecord);
        int afterNum=getLocalRecordsCount();
        String log=(new Date().toString()+" - "+managerId+" - creating a student record - "+studentRecord.recordID);
        writeLog(log);
        return beforeNum+1<=afterNum;
    }

    @Override
    public String getRecordCounts(String managerId) {
        String DDONum ;
        String LVLNum ;
        String MTLNum ;
        if(centerName == "MTL"){
            DDONum = messageForRecordCount(6789);
            LVLNum = messageForRecordCount(6790);
            MTLNum=String.valueOf(getLocalRecordsCount());
        }
        else if(centerName == "LVL"){
            DDONum = messageForRecordCount(6789);
            MTLNum = messageForRecordCount(6791);
            LVLNum = String.valueOf(getLocalRecordsCount());
        }
        else{
            MTLNum = messageForRecordCount(6791);
            LVLNum = messageForRecordCount(6790);
            DDONum=String.valueOf(getLocalRecordsCount());
        }
        //log
        String log=(new Date().toString()+" - "+managerId+" - get records number ");
        writeLog(log);

        return "Records Count: DDO:"+DDONum+" | LVL:"+LVLNum+" | MTL:"+MTLNum;
    }

    @Override
    public boolean editRecord(String managerId, String recordID, String fieldName, String newValue){
        Record targetRecord=null;

        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
        for(ArrayList<Record> recordArrayListSet:arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
                    break;
            }
        }
        if(targetRecord!=null){
            if(targetRecord instanceof TeacherRecord){
                synchronized (targetRecord) {
                    ((TeacherRecord) targetRecord).setValue(fieldName, newValue);  //shared resource - synchronized
                }
            }
            else {
                synchronized (targetRecord) {
                    ((StudentRecord) targetRecord).setValue(fieldName, newValue);   //shared resource - synchronized
                }
            }
            //log
            String log=(new Date().toString()+" - "+managerId+" - editing the record - "+recordID+" - Success");
            writeLog(log);
            return true;
        }
        else{
            //log
            String log=(new Date().toString()+" - "+managerId+" - editing the record - "+recordID+"- ERROR:Record not exist");
            writeLog(log);
            return false;
        }
    }

    @Override
    public boolean transferRecord(String managerId, String recordID, String remoteCenterServerName) {
        Record targetRecord=null;

        Collection<ArrayList<Record>>arrayListsSet=storedRecords.values();
        for(ArrayList<Record> recordArrayListSet : arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
                break;
            }
        }
        if(targetRecord==null){
            //log
            String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
            "Error:record not exist");
            writeLog(log);
            return false;
        }
        else{
            //remove
            ArrayList<Record>theArrayList=storedRecords.get(targetRecord.lastName.charAt(0));
            synchronized (targetRecord) {
                theArrayList.remove(targetRecord);
            }
            //add
            boolean flag;
            if(remoteCenterServerName.equalsIgnoreCase("DDO"))
                flag=messageForAddRecord(6789,targetRecord);
            else if(remoteCenterServerName.equalsIgnoreCase("LVL"))
                flag=messageForAddRecord(6790,targetRecord);
            else if(remoteCenterServerName.equalsIgnoreCase("MTL"))
                flag=messageForAddRecord(6791,targetRecord);
            else
                flag=false;
            //log
            if(flag){
                String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
                        "Success");
                writeLog(log);
            }
            else{
                String log=(new Date().toString()+" - "+managerId+" - transferring the record - "+recordID+" - "+
                        "Fail");
                writeLog(log);
            }
            return flag;
        }
    }

    @Override
    public String getRecordInfo(String recordID) {
        Record targetRecord=null;

        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
        for(ArrayList<Record> recordArrayListSet:arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
                break;
            }
        }
        if(targetRecord!=null)
            return targetRecord.toString();
        else
            return "the record is not exist";

    }

    public boolean addTRecord(String firstName, String lastName, String address, String phone, String specialization, String location) {
        TeacherRecord teacherRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);
        int beforeNum=getLocalRecordsCount();
        storingRecord(teacherRecord);
        int afterNum=getLocalRecordsCount();
        //log
        String log=(new Date().toString()+" - "+" - [transfer]adding a teacher record - "+teacherRecord.recordID);
        writeLog(log);
        return beforeNum+1==afterNum;
    }

    public boolean addSRecord(String firstName, String lastName, String coursesRegistered, String status, String date) {
        StudentRecord studentRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, date);
        int beforeNum=getLocalRecordsCount();
        storingRecord(studentRecord);
        int afterNum=getLocalRecordsCount();
        String log=(new Date().toString()+" - "+" - [transfer]adding a student record - "+studentRecord.recordID);
        writeLog(log);
        return beforeNum+1==afterNum;
    }

    //synchronized method:if two storingRecord execute concurrently,in the situation when hashMap does not include the key
    //the late put() will overlap the previous one -> previous record lost.
    private synchronized void storingRecord(Record record){
        char cap=record.lastName.charAt(0);
        if(!storedRecords.containsKey(cap)){
            ArrayList<Record> newArray=new ArrayList<Record>();
            newArray.add(record);
            storedRecords.put(cap,newArray);
        }
        else{
            ArrayList<Record> theArray= storedRecords.get(cap);
            theArray.add(record);
        }
    }

    public int getLocalRecordsCount(){
        int count=0;
        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
        for(ArrayList<Record> recordArrayListSet :arrayListsSet){
            for(Record record:recordArrayListSet){
                count++;
            }
        }
        return count;
    }


    public void writeLog(String log){
        if(!loggingFile.exists())
            return;
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

    private String messageForRecordCount(int port){
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            byte[] message = "$COUNT".getBytes();
            InetAddress host = InetAddress.getByName("localhost");

            DatagramPacket request = new DatagramPacket(message,"$COUNT".length(),host, port);
            datagramSocket.send(request);

            //get message
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(reply);
            System.out.println(new String(reply.getData()));
            return new String(reply.getData());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
        return "-1";
    }

    private boolean messageForAddRecord(int port,Record recordAdded){
    	boolean flag = false;
        DatagramSocket datagramSocket = null;
        String messageString="$ADD,";
        if(recordAdded instanceof StudentRecord)
            messageString+=recordAdded.recordID+","+recordAdded.firstName+","+recordAdded.lastName+","+((StudentRecord) recordAdded).coursesRegistered
                    +","+((StudentRecord) recordAdded).status+","+((StudentRecord) recordAdded).date;
        else if(recordAdded instanceof TeacherRecord){
            messageString+=recordAdded.recordID+","+recordAdded.firstName+","+recordAdded.lastName+","+((TeacherRecord) recordAdded).address
                    +","+((TeacherRecord) recordAdded).phone+","+((TeacherRecord) recordAdded).specialization+","+((TeacherRecord) recordAdded).location;
        }
        
        try {
            datagramSocket = new DatagramSocket();
            byte[] message = messageString.getBytes();
            InetAddress host = InetAddress.getByName("localhost");

            DatagramPacket request = new DatagramPacket(message, message.length,host, port);
            datagramSocket.send(request);

            //get message
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(reply);
            String replyString=new String(reply.getData()).trim();
//            System.out.println(new String(reply.getData()));
//            System.out.println(replyString);
            if(replyString.equals("SUCCESS")){
            	flag = true;
            }
               
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }
        return flag;
    }


}
