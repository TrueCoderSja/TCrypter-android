package ml.truecoder.tcrypter;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntryManager {
    private File infoFile;
    EntryManager(File infoFile) throws IOException {
        this.infoFile=infoFile;
        if(!infoFile.exists())
            infoFile.createNewFile();
    }

    public void addEntry(String filePath) throws IOException {
        //Add to encrypted List
        File newEntryList=new File(infoFile.getAbsolutePath()+".tmp"+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()));
        DataOutputStream dout=new DataOutputStream(new FileOutputStream(newEntryList));
        DataInputStream din=new DataInputStream(new FileInputStream(infoFile));
        boolean written=false;
        try{
            while (true){
                String entry=din.readUTF();
                String entryFilePath=entry.substring(0, entry.lastIndexOf(Constants.SEPRATOR));
                if(filePath.equals(entryFilePath)){
                    int count=Integer.parseInt(entry.substring(entry.lastIndexOf(Constants.SEPRATOR)+2));
                    dout.writeUTF(filePath+Constants.SEPRATOR+(count+1));
                    written=true;
                }
                else
                    dout.writeUTF(entry);
            }
        } catch (EOFException e)
        {} //Do nothing
        if(!written)
            dout.writeUTF(filePath+Constants.SEPRATOR+1);
        dout.close();
        din.close();
        FileWorker.move(newEntryList, infoFile);
    }

    public void removeEntry(String filePath) throws IOException {
        File newEntryList=new File(infoFile.getAbsolutePath()+".tmp"+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()));
        DataOutputStream dout=new DataOutputStream(new FileOutputStream(newEntryList));
        DataInputStream din=new DataInputStream(new FileInputStream(infoFile));
        int count;
        try{
            while (true){
                String entry=din.readUTF();
                String entryFilePath=entry.substring(0, entry.lastIndexOf(Constants.SEPRATOR));
                count=Integer.parseInt(entry.substring(entry.lastIndexOf(Constants.SEPRATOR)+2));
                if(filePath.equals(entryFilePath)){
                    if(count==1)
                        continue;
                    else
                        count-=1;
                }
                dout.writeUTF(entryFilePath+Constants.SEPRATOR+count);
            }
        } catch (EOFException e)
        {} //Do nothing
        dout.close();
        din.close();
        FileWorker.move(newEntryList, infoFile);
    }
}
