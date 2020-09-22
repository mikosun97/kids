package edu.skku.map.kidquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class UserInfoAdapter extends BaseAdapter {
    LayoutInflater inflater;
    DrawerLayout drawerLayout;
    private ArrayList<UserInfo> items;

    public UserInfoAdapter (Context context, ArrayList<UserInfo> memos) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = memos;
    }
    public void clearData(){
        items.clear();
    }
    public void setData(ArrayList<UserInfo> memo_data){
        items.addAll(memo_data);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public UserInfo getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

       // if ( view == null ) {
      //      view = inflater.inflate(R.layout.fragment_personal, viewGroup, false);

     //   }
        UserInfo item = items.get(i);

        return view;
    }

}
