package com.midea.fridge.fridgedoodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.midea.fridge.fridgedoodle.bean.DoodleInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenxiaofei on 2016/12/31.
 */
public class DoodleListAdapter extends RecyclerView.Adapter<DoodleListAdapter.MyViewHolder> {
    private Context mContext;
    private List<DoodleInfo> mData;
    private DoodleInfo mSelectDoodleInfo;

    public DoodleListAdapter(MainActivity context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<DoodleInfo> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void selectDoodle(DoodleInfo doodleInfo) {
        mSelectDoodleInfo = doodleInfo;
        notifyDataSetChanged();
    }

    public DoodleInfo getSelectDoodle() {
        return mSelectDoodleInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doodle_preview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DoodleInfo doodleInfo = mData.get(position);
        if(null != doodleInfo) {
            if(null != mSelectDoodleInfo && doodleInfo.getName().equals(mSelectDoodleInfo.getName())) {
                holder.doodlePreviewUnselected.setVisibility(View.GONE);
                holder.doodlePreviewUnselected.setClickable(false);
                holder.doodlePreviewSelected.setVisibility(View.VISIBLE);
                Bitmap image = BitmapFactory.decodeFile(doodleInfo.getImagePath());
                holder.doodleThumbSelected.setImageBitmap(image);
                holder.doodleModifyTimeSelected.setText(formatTime(doodleInfo.getTimestamp()));
            } else {
                holder.doodlePreviewSelected.setVisibility(View.GONE);
                holder.doodlePreviewUnselected.setVisibility(View.VISIBLE);
                Bitmap image = BitmapFactory.decodeFile(doodleInfo.getImagePath());
                holder.doodleThumbUnselected.setImageBitmap(image);
                holder.doodleModifyTimeUnselected.setText(formatTime(doodleInfo.getTimestamp()));

                holder.doodlePreviewUnselected.setClickable(true);
                holder.doodlePreviewUnselected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectDoodle(doodleInfo);
                        if(mContext instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) mContext;
                            mainActivity.onSelectDoodle(doodleInfo);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String formatTime(long timeMills) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(new Date(timeMills));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View doodlePreviewSelected;
        ImageView doodleThumbSelected;
        TextView doodleModifyTimeSelected;
        View doodlePreviewUnselected;
        ImageView doodleThumbUnselected;
        TextView doodleModifyTimeUnselected;

        public MyViewHolder(View itemView) {
            super(itemView);
            doodlePreviewSelected = itemView.findViewById(R.id.preview_selected);
            doodleThumbSelected = (ImageView) itemView.findViewById(R.id.preview_selected_image);
            doodleModifyTimeSelected = (TextView) itemView.findViewById(R.id.preview_selected_text);

            doodlePreviewUnselected = itemView.findViewById(R.id.preview_unselected);
            doodleThumbUnselected = (ImageView) itemView.findViewById(R.id.preview_unselected_image);
            doodleModifyTimeUnselected = (TextView) itemView.findViewById(R.id.preview_unselected_text);
        }
    }
}
