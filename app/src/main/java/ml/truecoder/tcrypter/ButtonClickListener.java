package ml.truecoder.tcrypter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ButtonClickListener implements View.OnClickListener {
    private Context context;

    ButtonClickListener(Context context){
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.gotoEncryptBtn:
                intent=new Intent(context, EncryptActivity.class);
                context.startActivity(intent);
                break;

            case R.id.gotoDecryptBtn:
                intent=new Intent(context, DecryptActivity.class);
                context.startActivity(intent);
                break;

            case R.id.viewEncryptedFilesListBtn:
                intent=new Intent(context, EncryptedFilesListActivity.class);
                context.startActivity(intent);
                break;
        }
    }
}
