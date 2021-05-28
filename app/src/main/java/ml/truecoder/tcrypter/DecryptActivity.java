package ml.truecoder.tcrypter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DecryptActivity extends AppCompatActivity {

    private WebView window;
    private File encryptedFile;
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
            public void runFunc(String action, String[] extras){
                switch (action){
                    case "choose":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFileSelectDialog();
                            }
                        });
                        break;
                    case "copyDecrypt":
                        break;
                    case "decrypt":
                        decryptFile(extras[0]);
                        break;
                }
            }
        }, "jsInterface");
        window.loadUrl("file:///android_asset/decryptView.html");

        FileWorker.requestPermissionsAtStartup(this);

        Intent intent=getIntent();
        if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_SEND)){
            String path= null;
            try {
                path = FileWorker.getRealPathFromURI(this, (Uri)intent.getExtras().get(Intent.EXTRA_STREAM));
                encryptedFile=new File(path);
                window.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView webView, String url){
                        window.loadUrl("javascript:loadFile('"+encryptedFile.getAbsolutePath()+"')");
                        window.setWebViewClient(null);
                    }
                });
            } catch (UnableToFindPathException e) {
                new AlertDialog.Builder(DecryptActivity.this)
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

    private void showFileSelectDialog(){
        new ChooserDialog(this)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String s, File file) {
                        encryptedFile=file;
                        window.loadUrl("javascript:loadFile('"+encryptedFile.getAbsolutePath()+"')");
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

    public void decryptFile(String password){
        if(encryptedFile==null) {
            new AlertDialog.Builder(DecryptActivity.this)
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
        else{
            progressDialog=new ProgressDialog(DecryptActivity.this);
            progressDialog.setMessage("Decrypting...");
            try {
                Toast.makeText(DecryptActivity.this, "Started", Toast.LENGTH_SHORT);
                Thread decryptThread = new Thread(new Decrypt(password, encryptedFile, encryptedFile, getApplicationContext()));
                progressDialog.show();
                decryptThread.start();
            }
            catch (FileNotFoundException e){
                new AlertDialog.Builder(DecryptActivity.this)
                        .setTitle("Error:")
                        .setMessage(e.getMessage())
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Decrypt.WrongPasswordException e) {
                new AlertDialog.Builder(DecryptActivity.this)
                        .setTitle("Wrong Password!")
                        .setMessage("Entered password is incorrect")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } catch (Decrypt.NotEncryptedWithTcrypterException e) {
                new AlertDialog.Builder(DecryptActivity.this)
                        .setTitle("File not encrypted")
                        .setMessage(e.getMessage())
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

                                FileWorker.quitApp(DecryptActivity.this);
                            }
                        })
                        .show();
            }
        }
    }

    public static void showFinishPrompt(){
        self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(self, "File Decrypted Successfully", Toast.LENGTH_LONG).show();
            }
        });
    }
}