package com.example.android.aidsdruginformation;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GetDrugsService extends IntentService {
    private final String LOG_TAG = GetDrugsService.class.getSimpleName();
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.example.android.aidsdruginformation.action.FOO";
    public static final String ACTION_BAZ = "com.example.android.aidsdruginformation.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.example.android.aidsdruginformation.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.example.android.aidsdruginformation.extra.PARAM2";

    public GetDrugsService() {
        super("GetDrugsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 14;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "https://aidsinfo.nih.gov/api/drugs";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.

                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.

                    return;
                }
                forecastJsonStr = buffer.toString();

                try {
                    Log.v(LOG_TAG + "Json True 1:", String.valueOf(forecastJsonStr));
                    getDrugDataFromJson(forecastJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                Toast.makeText(this, "Incorrect data reeived from server",
                        Toast.LENGTH_LONG).show();
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }

    private void getDrugDataFromJson(String forecastJsonStr
    )
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.


        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String LIST = "drugs";

        final String DRUG_ID = "Id";

        try {
            String jsonObject = "{ \"drugs\":" + forecastJsonStr + "}";
            JSONObject forecastJson = new JSONObject(jsonObject);

            Log.v(LOG_TAG + "Json True 1:", String.valueOf(forecastJson));
            JSONArray drugArray = forecastJson.getJSONArray(LIST);
            //JSONArray drugArray = forecastJson;

            //long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(drugArray.length());

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.


            for (int i = 0; i < drugArray.length(); i++) {
                // These are the values that will be collected.

                int drugId;
                String approvalStatus;
                String drugClass;
                String name;
                String company;
                String imageUrl;
                String approvedUse;


                // Get the JSON object representing the day
                JSONObject drug = drugArray.getJSONObject(i);


                drugId = drug.getInt(DRUG_ID);

                JSONObject approvalStatusObject = drug.getJSONObject("ApprovalStatus");

                approvalStatus = approvalStatusObject.getString("English");

                JSONObject classObject = drug.getJSONObject("Class");

                drugClass = classObject.getString("English");


                JSONArray namesArray = drug.getJSONArray("Names");
                if(namesArray.length() != 0){
                    JSONObject namesObject = namesArray.getJSONObject(0);
                    name = namesObject.getString("Title");}else{
                    name = "Not Available";
                }

                //JSONObject companyObject = drug.getJSONObject("Companies");
                JSONArray companyArray = drug.getJSONArray("Companies");
                if(companyArray.length() != 0){
                JSONObject companyObject = companyArray.getJSONObject(0);
                company = companyObject.getString("Name");}else{
                    company = "Not Available";
                }

                JSONArray imagesArray = drug.getJSONArray("Images");
                //JSONObject imageObject = drug.getJSONObject("Images");
                if(imagesArray.length() != 0) {
                    JSONObject imageObject = imagesArray.getJSONObject(0);

                    imageUrl = imageObject.getString("URL");
                }else{
                    imageUrl = "Unavailable";
                }

                JSONObject approvedUseObject = drug.getJSONObject("ApprovedUse");

                approvedUse = approvedUseObject.getString("English");

                //Log.v(LOG_TAG, "Done to debug");
//
                ContentValues drugValues = new ContentValues();
//
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_DRUG_ID, drugId);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_APPROVAL_STATUS, approvalStatus);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_DRUG_CLASS, drugClass);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_NAME, name);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_COMPANY, company);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_IMAGE_URL, imageUrl);
                drugValues.put(DrugsContract.DrugsEntry.COLUMN_APPROVED_USE, approvedUse);

                // TODO: Add drug information to columns using the above as an example

                cVVector.add(drugValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(DrugsContract.DrugsEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Drugs Service Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
