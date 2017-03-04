package com.ana_pc.contactlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.ana_pc.contactlist.DBHelper.COL_LASTNAME;
import static com.ana_pc.contactlist.DBHelper.COL_NAME;
import static com.ana_pc.contactlist.DBHelper.TABLE_CONTACTS;
/**
 * Created by apereyra on 03/03/2017.
 */

public class ContactListFragment extends Fragment {
    private ListView lista;
    private DBHelper dbHelper;
    private CustomAdapter customAdapter;

    protected void initializeDB()
    {
        /* QUE ONDA CON ESTE ERROR:
         java.lang.OutOfMemoryError: Failed to allocate a 23970828 byte allocation with 8388608 free bytes and 20MB until OOM
         */
        dbHelper = new DBHelper(getActivity());

        // recupero datos de la base de datos
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
        Cursor cursor = dbWrite.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " ORDER BY name ASC", null);

        customAdapter = new CustomAdapter(getActivity(), cursor);
        lista = (ListView)getActivity().findViewById(R.id.lista);
        lista.setAdapter(customAdapter);
        lista.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = getActivity().getIntent();
                intent.putExtra("personID", id);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContactPreviewFragment fragment3 = new ContactPreviewFragment();
                transaction.replace(R.id.container, fragment3);
                transaction.addToBackStack(null);
                transaction.commit();

                /*Intent intent = new Intent(getActivity(), ContactPreviewFragment.class);
                intent.putExtra("personID", id);
                // TODO - not handled!
                startActivityForResult(intent,86);*/
            }
        });

        getActivity().findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), ContactFormFragment.class);
                startActivityForResult(intent, 85);*/
                getActivity().getIntent().putExtra("personID", 0);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContactFormFragment fragment2 = new ContactFormFragment();
                transaction.add(R.id.container, fragment2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.initializeDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }


        /*@Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == 85){
                if(resultCode == Activity.RESULT_OK)
                {
                    if ("true".equals(data.getStringExtra("create")))
                    {
                        Toast.makeText(getActivity(), "User " + data.getStringExtra("fullname") + " created", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "User " + data.getStringExtra("fullname") + " updated", Toast.LENGTH_LONG).show();
                    }

                    customAdapter.notifyDataSetChanged();
                }
            }
        }*/

    public class CustomAdapter extends CursorAdapter {

        public CustomAdapter(Context context, Cursor cursor){
            super(context, cursor, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater li = getActivity().getLayoutInflater();
            View newView = li.inflate(R.layout.row_persona,parent, false);

            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String lastname = cursor.getString(cursor.getColumnIndex(COL_LASTNAME));
            //String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));

            TextView nameView = (TextView) newView.findViewById(R.id.nombre_de_persona);
            //ImageView photoView = (ImageView) newView.findViewById(R.id.contact_photo);

            String fullname = (lastname != null && !lastname.equals("")) ? name + " " + lastname : name;
            nameView.setText(fullname);
            /*if(photo != null) {
                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
            }*/
            return newView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            //String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));

            TextView nameView = (TextView) view.findViewById(R.id.nombre_de_persona);
            //ImageView photoView = (ImageView) view.findViewById(R.id.contact_photo);

            nameView.setText(name);
            /*if(photo != null)
            {
                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
            }
            else
            {
                photoView.setImageResource(R.drawable.user_image);
            }*/
        }
    }
}