package ml.truecoder.tcrypter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class EncryptActivity extends AppCompatActivity {

    private static final int PICK_FILE_RESULT_CODE = 2;
    private WebView window;
    private File chosenFile;
    private static ProgressDialog progressDialog;
    private static Activity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        self=this;
        window=findViewById(R.id.encryptWindow);
        window.getSettings().setJavaScriptEnabled(true);
        window.addJavascriptInterface(new Object(){

            @JavascriptInterface
            public void runFunc(String action, String[] extras) {
                switch (action){
                    case "choose":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFileSelectDialog();
                            }
                        });
                        break;
                    case "copyEncrypt":
                        break;
                    case "encrypt":
                        String password=extras[0], passwordConfirm=extras[1];
                        if(chosenFile==null) {
                            new AlertDialog.Builder(EncryptActivity.this)
                                    .setTitle("No File")
                                    .setMessage("Please select a file")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        else if(password.length()<8){
                            new AlertDialog.Builder(EncryptActivity.this)
                                    .setMessage("Password Must be 8 digits long")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        else if(!password.equals(passwordConfirm)){
                            new AlertDialog.Builder(EncryptActivity.this)
                                    .setMessage("Passwords do not match")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        else{
                            encryptFile(password);
                        }

                        break;
                }
            }
        }, "jsInterface");
        window.loadUrl("file:///android_asset/encryptView.html");

        FileWorker.requestPermissionsAtStartup(this);

        Intent intent=getIntent();
        if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_SEND)){
            String path= null;
            try {
                path = FileWorker.getRealPathFromURI(this, (Uri)intent.getExtras().get(Intent.EXTRA_STREAM));
                chosenFile=new File(path);
                window.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView webView, String url){
                        window.loadUrl("javascript:loadFile('"+chosenFile.getAbsolutePath()+"')");
                        window.setWebViewClient(null);
                    }
                });
            } catch (UnableToFindPathException e) {
                new AlertDialog.Builder(EncryptActivity.this)
                        .setTitle("Error")
                        .setMessage("Unable to fetch file")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==FileWorker.REQUEST_ID){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
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

                                FileWorker.quitApp(EncryptActivity.this);
                            }
                        })
                        .show();
            }
        }
    }

    private void showFileSelectDialog(){
        new ChooserDialog(this)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String s, File file) {
                        chosenFile=file;
                        window.loadUrl("javascript:loadFile('"+chosenFile.getAbsolutePath()+"')");
                    }
                })
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    private void encryptFile(String password){
        try {
            progressDialog=new ProgressDialog(EncryptActivity.this);
            progressDialog.setMessage("Encrypting...");
            progressDialog.show();
            Thread encryptThread = new Thread(new Encrypt(true, password, chosenFile, chosenFile, getApplicationContext()));
            encryptThread.start();
        }
        catch (FileNotFoundException e){
            new AlertDialog.Builder(EncryptActivity.this)
                    .setTitle("Error:")
                    .setMessage(e.getMessage())
                    .show();
            e.printStackTrace();
        }
    }

    public static void showFinishPrompt() {
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(self, "File Encrypted Successfully!", Toast.LENGTH_LONG).show();
            }
        });
    }
}