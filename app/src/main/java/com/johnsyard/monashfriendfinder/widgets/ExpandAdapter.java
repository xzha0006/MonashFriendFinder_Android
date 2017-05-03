package com.johnsyard.monashfriendfinder.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnsyard.monashfriendfinder.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This adapter is used for expanding listview with checkbox
 * Created by xuanzhang on 1/05/2017.
 */

public class ExpandAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> list;
    private static HashMap<Integer, Boolean> isSelected;//record checkboxes
    private int currentItem = -1; //record the on click item

    public ExpandAdapter(Context context,
                            ArrayList<HashMap<String, String>> list) {
        super();
        this.context = context;
        this.list = list;
        this.isSelected = new HashMap<Integer, Boolean>();
        initSelected();
    }

    private void initSelected() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //initialise convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expand, parent, false);
            holder = new ViewHolder();
            holder.showArea = (LinearLayout) convertView.findViewById(R.id.layout_showArea);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvMovie = (TextView) convertView.findViewById(R.id.tv_favorite_movie);
            holder.tvStudentId = (TextView) convertView.findViewById(R.id.tv_student_id);
            holder.tvOtherContent = (TextView) convertView.findViewById(R.id.tv_other_content);
            holder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);
            holder.hideArea = (LinearLayout) convertView.findViewById(R.id.layout_hideArea);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> item = list.get(position);

        //record the click event
        holder.showArea.setTag(position);

        holder.tvName.setText(item.get("name"));
        holder.tvMovie.setText(item.get("movie"));
        holder.tvStudentId.setText(item.get("studentId"));
        holder.tvOtherContent.setText(item.get("otherContent"));

        //if the content should be set into visible
        if (currentItem == position) {
            holder.hideArea.setVisibility(View.VISIBLE);
        } else {
            holder.hideArea.setVisibility(View.GONE);
        }

        holder.showArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(context, "item is clicked", Toast.LENGTH_SHORT).show();
                //record the click position
                int tag = (Integer) view.getTag();
                if (tag == currentItem) { //click again
                    currentItem = -1; //assign a invalid value
                } else {
                    currentItem = tag;
                }
                //claim adapter to change data
                notifyDataSetChanged();
            }
        });

        holder.cbItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "checkbox is clicked", Toast.LENGTH_SHORT).show();
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                }
                notifyDataSetChanged();
            }
        });

//        holder.tvPhoneType.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "hehe", Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.cbItem.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ExpandAdapter.isSelected = isSelected;
    }

    public ArrayList<HashMap<String, String>> getList(){
        return this.list;
    }
    private static class ViewHolder {
        private LinearLayout showArea;

        private TextView tvName;
        private TextView tvMovie;
        private TextView tvStudentId;
        private TextView tvOtherContent;
        private CheckBox cbItem;

        private LinearLayout hideArea;
    }
}