package server;

import DCMS.CenterServerPOA;
import records.Record;
import records.StudentRecord;
import records.TeacherRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class CenterServerImp extends CenterServerPOA{

    private HashMap<Character,ArrayList<Record>> storedRecords = new HashMap<>();
    private File loggingFile;
    private String centerName;
    private Semaphore mutex=new Semaphore(1,true);


    public CenterServerImp(File loggingFile,String centerName){
        this.loggingFile=loggingFile;
        this.centerName=centerName;
    }


    @Override
    public boolean createTRecord(String managerId, String firstName, String lastName, String address, String phone, String specialization, String location) {
        TeacherRecord teacherRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);
        int beforeNum=getLocalRecordsCount();
        storingRecord(teacherRecord);
        int afterNum=getLocalRecordsCount();

        String log=(new Date().toString()+"-"+managerId+" - creating a teacher record - "+teacherRecord.recordID);
        writeLog(log);
        return beforeNum+1==afterNum;
    }

    @Override
    public boolean createSRecord(String managerId, String firstName, String lastName, String coursesRegistered, String status, String date) {
        StudentRecord studentRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, date);
        int beforeNum=getLocalRecordsCount();
        storingRecord(studentRecord);
        int afterNum=getLocalRecordsCount();
        String log=(new Date().toString()+"-"+managerId+" - creating a student record - "+studentRecord.recordID);
        writeLog(log);
        return beforeNum+1==afterNum;
    }

    @Override
    public String getRecordCounts(String managerId) {
        return null;
    }

    @Override
    public void editRecord(String managerId, String recordID, String fieldName, String newValue) {
        Record targetRecord=null;

        Collection<ArrayList<Record>> arrayListsSet=storedRecords.values();
        for(ArrayList<Record> recordArrayListSet :arrayListsSet){
            for(Record record:recordArrayListSet){
                if(record.recordID.equalsIgnoreCase(recordID))
                    targetRecord=record;
            }
        }

        System.out.println(targetRecord);

        if(targetRecord!=null){
            if(targetRecord instanceof TeacherRecord){
                ((TeacherRecord)targetRecord).setValue(fieldName,newValue);
                System.out.println(targetRecord);
            }
            else {
                ((StudentRecord)targetRecord).setValue(fieldName,newValue);
                System.out.println(targetRecord);
            }
        }

        String log=(new Date().toString()+"-"+managerId+" - editing a record ");
        writeLog(log);
    }

    @Override
    public void transferRecord(String managerId, String recordID, String remoteCenterServerName) {

    }

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

    public synchronized int getLocalRecordsCount(){
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
            FileWriter fileWriter = new FileWriter(loggingFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            mutex.acquire();   //semaphore - lock
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            mutex.release();   //semaphore - unlock
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }


}
