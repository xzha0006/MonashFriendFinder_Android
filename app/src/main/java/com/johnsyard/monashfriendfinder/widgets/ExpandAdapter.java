package com.johnsyard.monashfriendfinder.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.entities.MovieInfo;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.description;

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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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
        //click showArea
        holder.showArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "item is clicked", Toast.LENGTH_SHORT).show();
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
        //click checkbox
        holder.cbItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Toast.makeText(context, "checkbox is clicked", Toast.LENGTH_SHORT).show();
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

        holder.cbItem.setChecked(getIsSelected().get(position));
        //set movie info
        holder.tvMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_movie, parent, false);
                final ImageView imgMovie = (ImageView) dialog.findViewById(R.id.img_movie);
                final TextView tvMovieInfo = (TextView) dialog.findViewById(R.id.tv_movie_info);
                String favoriteMovie = ((TextView) view).getText().toString();
                String movieName = favoriteMovie.substring(favoriteMovie.indexOf(":")+1).trim();
                new AsyncTask<String, Void, MovieInfo>(){
                    @Override
                    protected MovieInfo doInBackground(String... strings) {
                        MovieInfo movie = RestClient.getMovieInfo(strings[0]);
                        return movie;
                    }
                    @Override
                    protected void onPostExecute(MovieInfo movie) {
                        String description = movie.getDescription() == null ? "" : movie.getDescription();
                        Bitmap bitmap = movie.getImage() == null ? null : movie.getImage();
                        if (description.length() > 0){
                            tvMovieInfo.setText(description);
                        }else{
                            tvMovieInfo.setText("No information about this movie: " + ((TextView) view).getText().toString());
                        }

                        if(bitmap != null){
                            imgMovie.setImageBitmap(bitmap);
                        }

                    }

                }.execute(movieName);
                builder.setTitle("Movie Info:");
                builder.setView(dialog);
                builder.setCancelable(true);
                AlertDialog dialogAlert = builder.create();
                dialogAlert.setCanceledOnTouchOutside(true);
                dialogAlert.show();
            }
        });
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