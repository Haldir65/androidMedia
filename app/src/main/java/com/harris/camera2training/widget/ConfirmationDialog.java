package com.harris.camera2training.widget;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Shows OK/Cancel confirmation dialog about camera permission.
 */
public class ConfirmationDialog extends DialogFragment {

    public static final int REQUEST_CAMERA_PERMISSION = 1;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity parent = getActivity();
        return new AlertDialog.Builder(getActivity())
                .setMessage("该应用需要相机权限才能使用")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(parent,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION
                        );
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (parent != null) {
                                    parent.finish();
                                }
                            }
                        })
                .create();
    }
}