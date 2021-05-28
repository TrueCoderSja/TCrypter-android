package ml.truecoder.tcrypter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class EncryptedFilesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypted_files_list);

        ArrayList<String> filesList=new ArrayList<String>();
        ListView listView=findViewById(R.id.encrptedFilesListView);
        int loopCount=0;
        try {
            DataInputStream din=new DataInputStream(new FileInputStream(new File(getFilesDir()+File.separator+Constants.INFO_FILE_NAME)));
            while(true){
                try{
                    String encryptedFile=din.readUTF();
                    filesList.add(encryptedFile);
                } catch (EOFException e){
                    break;
                }
                loopCount++;
            }
            din.close();

            listView.setAdapter(new EncryptedFilesListAdapter(filesList, this));

            if(loopCount==0)
                showNoEncrypts();
        } catch (FileNotFoundException e) {
            showNoEncrypts();
        } catch (IOException e) {
            Toast.makeText(this, "Error: "+e.getMessage()  , Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void showNoEncrypts(){
        TextView textView=new TextView(this);
        textView.setText("No Encrypts");
        setContentView(textView);
    }
}