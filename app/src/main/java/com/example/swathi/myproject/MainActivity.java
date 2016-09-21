package com.example.swathi.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EmployeeDatabase employeeDatabase;

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

            String[] columns = {employeeDatabase.NAME,employeeDatabase.USERNAME,employeeDatabase.PASSWORD};
            String[] selectionArgs = {uname,pword};

            Cursor cursor = db.query(employeeDatabase.TABLE_NAME,columns, employeeDatabase.USERNAME +"=? AND "+employeeDatabase.PASSWORD +" =? " , selectionArgs,null,null,null,null);
            Log.d("cursor", String.valueOf(cursor));
            if(cursor != null && cursor.getCount()> 0) {

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
    }

    public void saveUser(View view) {
        EditText nameTxt = (EditText) findViewById(R.id.enterName);
        EditText usernameTxt = (EditText) findViewById(R.id.enterUserName);
        EditText passwordTxt = (EditText) findViewById(R.id.enterPassword);

        String name = nameTxt.getText().toString();
        String uname = usernameTxt.getText().toString();
        String pword = passwordTxt.getText().toString();
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
            long id = db.insert(employeeDatabase.TABLE_NAME,null,contentValues);
            Toast.makeText(this,name + "has been inserted into DB ",Toast.LENGTH_SHORT).show();
            if(id > 0)
            {
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

        employeeDatabaseArrayAdapter = new UserAdopter();
        listView.setAdapter(employeeDatabaseArrayAdapter);
    }

    private class UserAdopter extends ArrayAdapter<EmployeeDatabase>{


        public UserAdopter() {
            super(MainActivity.this, R.layout.list_item_view,Employees);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            return super.getView(position, convertView, parent);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
