package com.ana_pc.contactlist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ana_pc.contactlist.DBHelper.COL_NAME;
import static com.ana_pc.contactlist.DBHelper.COL_LASTNAME;
import static com.ana_pc.contactlist.DBHelper.COL_EMAIL;
import static com.ana_pc.contactlist.DBHelper.COL_PHONE;
import static com.ana_pc.contactlist.DBHelper.COL_PHOTO;
import static com.ana_pc.contactlist.DBHelper.TABLE_CONTACTS;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class Main2Activity extends Fragment
{
    //private static final int REQUEST_CAMERA = 1888;
    //private boolean fotoOk = false;
    private TextView nameView;
    private TextView lastnameView;
    private TextView emailView;
    private TextView phoneView;
    //private ImageView photoView;
    //private GoogleApiClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_main2, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        nameView = (TextView) getActivity().findViewById(R.id.nombre_de_persona);
        lastnameView = (TextView) getActivity().findViewById(R.id.lastName);
        emailView = (TextView) getActivity().findViewById(R.id.emailAddress);
        phoneView = (TextView) getActivity().findViewById(R.id.phoneNumber);
        //photoView = (ImageView) findViewById(R.id.image_contact);

        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );

        TextView iconMailView = (TextView)getActivity().findViewById(R.id.icon_mail);
        iconMailView.setTypeface(font);

        TextView iconPhoneView = (TextView)getActivity().findViewById(R.id.icon_phone);
        iconPhoneView.setTypeface(font);

        TextView editUserImage = (TextView)getActivity().findViewById(R.id.icon_edit);
        editUserImage.setTypeface(font);

        /*TextView editImage = (TextView)findViewById(R.id.edit_image);
        editImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

             File photoFile = null;
             try {
                 photoFile = createImageFile();
             } catch (IOException ex) {
                 // Error occurred while creating the File
                 Toast.makeText(Main2Activity.this, "Error taking photo", Toast.LENGTH_LONG).show();
                 return;
             }
             if (photoFile != null) {
                 Uri photoURI = FileProvider.getUriForFile(Main2Activity.this,
                         "com.ana_pc.contactlist",
                         photoFile);
                 intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                 startActivityForResult(intent, REQUEST_CAMERA);
             }
            }
            });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();*/

        Intent intent = getActivity().getIntent();
        final long id = intent.getLongExtra("personID", 0);
        final DBHelper bh = new DBHelper(getActivity());
        final String idString = Long.toString(id);
        if(id != 0)
        {
            SQLiteDatabase dbRead = bh.getReadableDatabase();
            Cursor cursor = dbRead.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE _id = ?", new String[]{ idString });
            cursor.moveToFirst();
            nameView.setText(cursor.getString(cursor.getColumnIndex("name")));
            lastnameView.setText(cursor.getString(cursor.getColumnIndex("lastname")));
            emailView.setText(cursor.getString(cursor.getColumnIndex("email")));
            phoneView.setText(cursor.getString(cursor.getColumnIndex("phone")));
            /*String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));
            if(photo != null)
            {
                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
            }
            else
            {
                photoView.setImageResource(R.drawable.user_image);
            }*/

            cursor.close();
            dbRead.close();
        }

        getActivity().findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView nameEdit = (TextView)getActivity().findViewById(R.id.nombre_de_persona);
                TextView lastNameEdit = (TextView)getActivity().findViewById(R.id.lastName);
                TextView emailEdit = (TextView)getActivity().findViewById(R.id.emailAddress);
                TextView phoneEdit = (TextView)getActivity().findViewById(R.id.phoneNumber);

                String name = nameEdit.getText().toString();

                if (name == null || name.isEmpty())
                {
                    Toast.makeText(getActivity(), "Name can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                String lastName = lastNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String phone = phoneEdit.getText().toString();

                SQLiteDatabase dbWrite = bh.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(COL_NAME, name);
                cv.put(COL_LASTNAME, lastName);
                cv.put(COL_EMAIL, email);
                cv.put(COL_PHONE, phone);
                /*if(fotoOk)
                    cv.put(COL_PHOTO, mCurrentPhotoPath);*/

                Intent action = new Intent();

                if(id != 0)
                {
                    // Hacemos un update
                    dbWrite.update(TABLE_CONTACTS, cv, "_id = ?", new String[]{Long.toString(id)});
                    action.putExtra("create", "false");
                }
                else
                {
                    // Hacemos un insert
                    dbWrite.insert(TABLE_CONTACTS, null, cv);
                    action.putExtra("create", "true");
                }
                String fullname = (lastName != null && !lastName.equals("")) ? name + " " + lastName : name;
                action.putExtra("fullname", fullname);

                dbWrite.close();

                Intent intent = getActivity().getIntent();
                intent.putExtra("personID", id);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                MainActivity fragment = new MainActivity();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
                //getActivity().setResult(getActivity().RESULT_OK, action);
                //getActivity().finish();
            }
        });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK)
            {
                Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ImageView image = (ImageView) findViewById(R.id.image_contact);
                image.setImageBitmap(imageBitmap);
                fotoOk = true;
                return;
            }
        }
    }*/

    /*String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }*/

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main2 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }*/
}
