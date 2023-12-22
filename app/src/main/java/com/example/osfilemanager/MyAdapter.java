package com.example.osfilemanager;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    File[] filesAndFolders;

    public MyAdapter(Context context, File[] filesAndFolders){
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.baseline_folder_24);
        }else if (selectedFile.getName().endsWith(".jpg") ||
                selectedFile.getName().endsWith(".jpeg") ||
                selectedFile.getName().endsWith(".png")) {
            holder.imageView.setImageResource(R.drawable.baseline_image_24);
        }else {
                 holder.imageView.setImageResource(R.drawable.baseline_insert_drive_file_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path",path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (selectedFile.getName().endsWith(".jpg") ||
                        selectedFile.getName().endsWith(".jpeg") ||
                        selectedFile.getName().endsWith(".png")){
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "image/*";
                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file: " + selectedFile.getName(),Toast.LENGTH_SHORT).show();
                    }
                }
                else if (selectedFile.getName().endsWith(".txt")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "text/plain";
                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file: " + selectedFile.getName(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context.getApplicationContext(),"Cannot open the file: " + selectedFile.getName(),Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenu().add("DELETE");
                popupMenu.getMenu().add("SORT");
                popupMenu.getMenu().add("DETAILS");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("DELETE")){
                            boolean deleted = selectedFile.delete();
                            if(deleted){
                                Toast.makeText(context.getApplicationContext(),"DELETED ",Toast.LENGTH_SHORT).show();
                                v.setVisibility(View.GONE);
                            }
                        }
                        if (item.getTitle().equals("SORT")) {
                            sortByName();
                            Toast.makeText(context.getApplicationContext(), "SORTED BY NAME", Toast.LENGTH_SHORT).show();
                        }
                        if (item.getTitle().equals("DETAILS")) {
                            String fileDetails = "File name: " + selectedFile.getName() + "\n" + getFileDetails(selectedFile);
                            Toast.makeText(context, fileDetails, Toast.LENGTH_LONG).show();
                        }

                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });
    }

    public void sortByName() {
        Arrays.sort(filesAndFolders, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareToIgnoreCase(f2.getName());
            }
        });
        notifyDataSetChanged();
    }

    private String getFileDetails(File file) {
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        String fileSize;
        if (fileSizeInMB > 0) {
            fileSize = fileSizeInMB + " MB";
        } else if (fileSizeInKB > 0) {
            fileSize = fileSizeInKB + " KB";
        } else {
            fileSize = fileSizeInBytes + " bytes";
        }
        long creationTimeInMillis = file.lastModified();
        String creationTime = new Date(creationTimeInMillis).toString();

        return "File size: " + fileSize + "\n" + "Created: " + creationTime;
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }
}