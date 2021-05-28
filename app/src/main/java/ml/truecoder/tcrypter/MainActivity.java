package ml.truecoder.tcrypter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button encryptBtn, decryptBtn, gotoListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButtonClickListener clickListener=new ButtonClickListener(MainActivity.this);
        encryptBtn=(Button)findViewById(R.id.gotoEncryptBtn);
        encryptBtn.setOnClickListener(clickListener);

        decryptBtn=(Button)findViewById(R.id.gotoDecryptBtn);
        decryptBtn.setOnClickListener(clickListener);

        gotoListBtn=findViewById(R.id.viewEncryptedFilesListBtn);
        gotoListBtn.setOnClickListener(clickListener);

        FileWorker.requestPermissionsAtStartup(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==FileWorker.REQUEST_ID){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                return;
            }
            else{
                new AlertDialog.Builder(this)
                        .setTitle("Permissions denied")
                        .setMessage("Cannot work without permissions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                FileWorker.quitApp(MainActivity.this);
                            }
                        })
                        .show();
            }
        }
    }
}