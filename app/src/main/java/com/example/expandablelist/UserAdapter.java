package com.example.expandablelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class UserAdapter extends BaseExpandableListAdapter {

    private ViewHolder holder;
    private PhoneHolder pHolder;

    private ArrayList<Users> usersList;
    private Context myContext;

    public UserAdapter (Context context, ArrayList<Users> objects) {
        usersList = objects;
        myContext = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return usersList.get(groupPosition).getPhoneNumber();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            pHolder = new PhoneHolder();
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_child, null);
            pHolder.phoneNumber = (TextView) view.findViewById(R.id.userPhoneNumber);
            view.setTag(pHolder);
        } else {
            pHolder = (PhoneHolder) view.getTag();
        }
        
        pHolder.phoneNumber.setText(myContext.getString(R.string.item_phone) + " " + usersList.get(groupPosition).getPhoneNumber());

        return view;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return usersList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
            holder.image = (ImageView) view.findViewById(R.id.icon);
            holder.firstName = (TextView) view.findViewById(R.id.userFirstName);
            holder.lastName = (TextView) view.findViewById(R.id.userLastName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //Android-Universal-Image-Loader lib
        String imageUrl = usersList.get(groupPosition).getImage();
        ImageLoader imageLoader = ((MyApplication) myContext).getImageLoader();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(myContext)
                .memoryCacheExtraOptions(100, 100)
                        //.diskCacheExtraOptions(100, 100, Bitmap.CompressFormatJPEG, 75)
                .threadPoolSize(10)
                .denyCacheImageMultipleSizesInMemory()
                        //.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .writeDebugLogs()
                .build();
        imageLoader.init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_avatar) //заглушка пока грузится
                .showImageForEmptyUri(R.drawable.default_avatar) //если url = null
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        imageLoader.displayImage(imageUrl, holder.image, options);
        //lib ends

        holder.firstName.setText(usersList.get(groupPosition).getFirstName() + " ");
        holder.lastName.setText(usersList.get(groupPosition).getLastName());

        return view;
    }

    static class ViewHolder {
        public ImageView image;
        public TextView firstName;
        public TextView lastName;
    }

    static class PhoneHolder {
        public TextView phoneNumber;
    }

    @Override
    public int getGroupCount() {
        return usersList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
