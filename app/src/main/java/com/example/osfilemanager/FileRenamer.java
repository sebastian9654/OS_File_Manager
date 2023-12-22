package com.example.osfilemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import java.io.File;

public class FileRenamer {

    public void renameFile(Context context, String currentFilePath) {
        final File currentFile = new File(currentFilePath);

        // Create the EditText field for the new file name
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Create the AlertDialog and set the EditText field as the view
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rename file");
        builder.setMessage("Enter new file name:");
        builder.setView(input);

        // Add the "Rename" button and handle the click event
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFileName = input.getText().toString();
                File newFile = new File(currentFile.getParent(), newFileName);
                boolean success = currentFile.renameTo(newFile);
                // Handle the success or failure of the rename operation
            }
        });

        // Add the "Cancel" button to allow the user to cancel the operation
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the AlertDialog
        builder.show();
    }
}
