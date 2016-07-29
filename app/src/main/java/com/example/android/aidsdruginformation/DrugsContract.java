package com.example.android.aidsdruginformation;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 20-05-2016.
 */
public class DrugsContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.aidsdruginformation";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "drugs";

    public static final class DrugsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String TABLE_NAME = "drugs";


        // The location setting string is what will be sent to openweathermap
        // as the location query.

        public static final String COLUMN_DRUG_ID = "drug_id";
        public static final String COLUMN_APPROVAL_STATUS = "approval_status";
        public static final String COLUMN_DRUG_CLASS = "drug_class";
        public static final String COLUMN_COMPANY = "company";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_APPROVED_USE = "approved_use";
       //TODO: Add columns here using the above as an example


        public static Uri buildDrugUri() {
            //return ContentUris.withAppendedId(CONTENT_URI);
            return CONTENT_URI.buildUpon().build();
        }
    }
}
