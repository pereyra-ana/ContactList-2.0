package com.ana_pc.contactlist;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.ana_pc.contactlist.DBHelper.TABLE_CONTACTS;

public class ContactPreviewFragment extends Fragment {
    private TextView nameView;
    private TextView lastnameView;
    private TextView emailView;
    private TextView phoneView;
    //private ImageView photoView;

    protected void initializeUserView()
    {
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

        TextView userImage = (TextView)getActivity().findViewById(R.id.icon_image);
        userImage.setTypeface(font);

        Intent intent = getActivity().getIntent();
        final long id = intent.getLongExtra("personID", 0);
        final DBHelper bh = new DBHelper(getActivity());
        final String idString = Long.toString(id);

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

        getActivity().findViewById(R.id.dial_number_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneView.getText() != null && !phoneView.getText().equals(""))
                {
                    /*if (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d("CALL", "Ask for permission");

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                12345);
                    } else {*/
                        try
                        {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            // TODO Fix show attributes on dashboard
                            String phone = phoneView.getText().toString();
                            callIntent.setData(Uri.parse("tel:" + phone));
                            startActivity(callIntent);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                //}
                else
                {
                    Toast.makeText(getActivity(), "No phone number to dial", Toast.LENGTH_LONG).show();
                }
            }
        });

        getView().findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                intent.putExtra("personID", id);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContactFormFragment fragment2 = new ContactFormFragment();
                transaction.add(R.id.container, fragment2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        getView().findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setIcon(android.R.drawable.ic_delete);
                builder.setTitle("Warning!");
                builder.setMessage("Do you confirm to delete this person?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //@todo: Borramos a esta persona
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //@todo: No hacer nada
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.initializeUserView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_main3, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.initializeUserView();
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 85){
            if(resultCode == Activity.RESULT_OK)
            {
                Toast.makeText(ContactPreviewFragment.this, "User " + data.getStringExtra("fullname") + " updated", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
