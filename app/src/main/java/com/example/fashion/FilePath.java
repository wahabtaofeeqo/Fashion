package com.example.fashion;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FilePath {

    private Activity a;

    public FilePath(Activity activity) {
        a = activity;
    }

    public String getPath(Context context, Uri uri) {


        String docId;
        String type;
        String[] splits;
        String path = uri.getPath();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (isExternal(uri)) {

                docId = DocumentsContract.getDocumentId(uri);

                splits = docId.split(":");
                type = splits[0];

                path = Environment.getExternalStorageDirectory() + "/" + splits[1];
            }

            if (isMedia(uri)) {

                docId = DocumentsContract.getDocumentId(uri);
                splits = docId.split(":");
                type = splits[0];

                Uri contentUri = null;

                if ("image".equalsIgnoreCase(type))
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


                if ("audio".equalsIgnoreCase(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


                if ("video".equalsIgnoreCase(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                String selection = "_id=?";
                String[] args = new String[]{splits[1]};

                path = getColumn(contentUri, selection, args);
            }

            if (isDownload(uri)) {

                docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));

                path = getColumn(contentUri, null, null);
            }
        }

        return path;
    }

    private String getColumn(Uri uri, String selection, String[] args) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = a.getContentResolver().query(uri, projection, selection, args, null);

            if (cursor != null && cursor.moveToFirst()) {

                int index = cursor.getColumnIndexOrThrow(column);

                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    private boolean isExternal(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isMedia(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isDownload(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
