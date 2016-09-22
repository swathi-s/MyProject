package com.example.swathi.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EmployeeDatabase employeeDatabase;

    String user;

    ImageView selectImg;

    Uri imageUri = Uri.parse("android.resource://com.example.swathi.myproject/drawable/profile");

    List<EmployeeDatabase> Employees = new ArrayList<EmployeeDatabase>();
    ArrayAdapter<EmployeeDatabase> employeeDatabaseArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        employeeDatabase = new EmployeeDatabase(this);
        SQLiteDatabase db = employeeDatabase.getWritableDatabase();
    }


    public void loginUser(View view) {
        EditText username = (EditText) findViewById(R.id.userName);
        EditText password = (EditText) findViewById(R.id.password);

        String uname = username.getText().toString();
        String pword = password.getText().toString();

        if(TextUtils.isEmpty(uname) ||
                TextUtils.isEmpty(pword)){
            Toast.makeText(this,"Please enter username and password", Toast.LENGTH_LONG).show();
        }
        else
        {
            SQLiteDatabase db = employeeDatabase.getWritableDatabase();

            String[] columns = {employeeDatabase.NAME,employeeDatabase.USERNAME,employeeDatabase.PASSWORD,employeeDatabase.IMAGE};
            String[] selectionArgs = {uname,pword};

            Cursor cursor = db.query(employeeDatabase.TABLE_NAME,columns, employeeDatabase.USERNAME +"=? AND "+employeeDatabase.PASSWORD +" =? " , selectionArgs,null,null,null,null);
            Log.d("cursor", String.valueOf(cursor.getColumnNames()));
            if(cursor != null && cursor.getCount()> 0) {

                while (cursor.moveToNext()) {
                    int imgIndex = cursor.getColumnIndex(employeeDatabase.IMAGE);
                    Log.d("index", String.valueOf(imgIndex));

                    String uri = cursor.getString(imgIndex);
                   // int imgindex = cursor.getColumnIndex(employeeDatabase.IMAGE);
                    //String uri = cursor.getString(imgindex);
                    imageUri = Uri.parse(uri);
                }

                user = uname;
                //setContentView(R.layout.successfull_login_layout);
                dashboard();

            }
            else
            {
                Toast.makeText(this, "Give Valid Username and Password", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void signupUser(View view) {

        setContentView(R.layout.signup_layout);

        selectImg = (ImageView) findViewById(R.id.enterImg);
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"),1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK) {
            if(requestCode == 1) {
                imageUri = data.getData();
                selectImg.setImageURI(data.getData());
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveUser(View view) {
        EditText nameTxt = (EditText) findViewById(R.id.enterName);
        EditText usernameTxt = (EditText) findViewById(R.id.enterUserName);
        EditText passwordTxt = (EditText) findViewById(R.id.enterPassword);

        ImageView selectedImg = (ImageView) findViewById(R.id.enterImg);

        String name = nameTxt.getText().toString();
        String uname = usernameTxt.getText().toString();
        String pword = passwordTxt.getText().toString();
        String selctedImg = imageUri.toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(uname) || TextUtils.isEmpty(pword))
        {
            Toast.makeText(this,"all the fields are required",Toast.LENGTH_LONG).show();
        }
        else
        {
            SQLiteDatabase db = employeeDatabase.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(employeeDatabase.NAME,name);
            contentValues.put(employeeDatabase.USERNAME,uname);
            contentValues.put(employeeDatabase.PASSWORD,pword);
            contentValues.put(employeeDatabase.IMAGE,selctedImg);

            long id = db.insert(employeeDatabase.TABLE_NAME,null,contentValues);
            Toast.makeText(this,name + "has been inserted into DB ",Toast.LENGTH_SHORT).show();
            if(id > 0)
            {
                user = uname;
                dashboard();
            }
        }
    }

    public void logout(View view) {
        setContentView(R.layout.activity_main);
    }

    public void dashboard()
    {

        setContentView(R.layout.successfull_login_layout);
        TextView showUser = (TextView) findViewById(R.id.showName);
        showUser.setText(user);

        ImageView showImg= (ImageView) findViewById(R.id.showImg);
        Log.d("image is ",imageUri.toString());
        showImg.setImageURI(imageUri);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.options,android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void directLogin(View view) {
        dashboard();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView optionTxt = (TextView) view;
        String item = parent.getItemAtPosition(position).toString();
        Log.d("item",item);
        String option = optionTxt.getText().toString();

       // Toast.makeText(this,"you have selected "+optionTxt.getText().toString(),Toast.LENGTH_SHORT).show();
        Log.d("option",option);
        if(option.equals("Log Out"))
        {
            Log.d("option","entered into logout");
            setContentView(R.layout.activity_main);
        }
        else if(option.equals("List DB items"))
        {
            Log.d("option","entered into list db items");
            //setContentView(R.layout.list_db_items);
            listDbItems();
        }
        else if(option.equals("Dashboard"))
        {
            Log.d("options","entered into dashboard");
            dashboard();
        }

    }

    public void listDbItems()
    {
        setContentView(R.layout.list_db_items);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.options,android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ListView listView = (ListView) findViewById(R.id.listView);
        String[] columns = {employeeDatabase.NAME,employeeDatabase.USERNAME,employeeDatabase.PASSWORD};
        SQLiteDatabase db = employeeDatabase.getWritableDatabase();
        Cursor cursor = db.query(employeeDatabase.TABLE_NAME,columns,null,null,null,null,null);
        int count = cursor.getCount();
        String[] names = new String[count];
        String[] usernames = new String[count];
        String[] passwords = new String[count];
        if(cursor != null )
        {
            while(cursor.moveToNext())
            {

                int index1 = cursor.getColumnIndex(employeeDatabase.NAME);
                names[cursor.getPosition()] = cursor.getString(index1);

                int index2 = cursor.getColumnIndex(employeeDatabase.USERNAME);
                usernames[cursor.getPosition()] = cursor.getString(index2);

                int index3 = cursor.getColumnIndex(employeeDatabase.PASSWORD);
                passwords[cursor.getPosition()] = cursor.getString(index3);


            }
        }

        DataAdapter adapter1 = new DataAdapter(this,names,usernames,passwords);
        listView.setAdapter(adapter1);

        //Toast.makeText(this,"data"+names[1],Toast.LENGTH_SHORT).show();

    }

    class DataAdapter extends ArrayAdapter<String>
    {
        Context context;
        String[] names;
        String[] usernames;
        String[] passwords;


        public DataAdapter(Context context,String[] names,String[] usernames, String[] passwords){
            super(context,R.layout.list_item_view,R.id.dispName,names);
            //super(context, resource, textViewResourceId, objects);

            this.context = context;
            this.names = names;
            this.usernames = usernames;
            this.passwords = passwords;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.list_item_view,parent,false);

            TextView dispNameTxt = (TextView) row.findViewById(R.id.dispName);
            TextView dispUserNameTxt = (TextView) row.findViewById(R.id.dispUserName);
            TextView dispPasswordTxt = (TextView) row.findViewById(R.id.dispPassword);

            dispNameTxt.setText(names[position]);
            //dispNameTxt.setText(names[position]);

            dispUserNameTxt.setText(usernames[position]);
            //dispUserNameTxt.setText(usernames[position].toString());
            dispPasswordTxt.setText(passwords[position]);

            return row;
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
