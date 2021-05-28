package ml.truecoder.tcrypter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class EncryptedFilesListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public EncryptedFilesListAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.encrypt_list_layout, null);
        }

        String entry=list.get(position);
        String filePath=entry.substring(0, entry.lastIndexOf(Constants.SEPRATOR));
        int count=Integer.parseInt(entry.substring(entry.lastIndexOf(Constants.SEPRATOR)+2));
        //Handle TextView and display string from your list

        TextView fileLbl= (TextView)view.findViewById(R.id.fileLbl);
        fileLbl.setText(filePath);

        if(count>1) {
            TextView countLbl = (TextView) view.findViewById(R.id.countLbl);
            countLbl.setText(String.valueOf(count));
        }


        //Handle buttons and add onClickListeners
        Button callBtn= (Button)view.findViewById(R.id.btn);

        callBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DecryptActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}