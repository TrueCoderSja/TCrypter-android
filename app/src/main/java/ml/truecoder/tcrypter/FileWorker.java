package ml.truecoder.tcrypter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class FileWorker {
    public static final int REQUEST_ID = 3099;

    public static void copy(InputStream in, OutputStream out)throws IOException {
        byte[] buffer=new byte[4096];
        int read;

        while((read=in.read(buffer))>0){
            out.write(buffer, 0, read);
        }
    }

    public static void move(File src, File dest)throws IOException{
        copy(new FileInputStream(src), new FileOutputStream(dest));
        src.delete();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) throws UnableToFindPathException {
        if(contentUri.getScheme().equals("file")) {
            try {
                return new File(new URI(contentUri.toString())).getAbsolutePath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new UnableToFindPathException(e.getMessage());
            }
        }
        else if(contentUri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } catch (Exception e) {
                throw new UnableToFindPathException(e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        throw new UnableToFindPathException("Null entry");
    }

    public static void requestPermissionsAtStartup(Activity activity) {
        String[] permissions={
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean allGranted=true;
        for(String permission:permissions){
            if(activity.checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED)
                allGranted=false;
        }
        if(!allGranted)
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_ID);
    }

    public static void quitApp(Activity activity) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(homeIntent);
    }
}

class UnableToFindPathException extends Exception{
    UnableToFindPathException(String message){
        super("Unable to get file path due to: "+message);
    }
}
