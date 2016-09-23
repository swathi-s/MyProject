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

    /**
     * To hold the db object
     */
    EmployeeDatabase employeeDatabase;

    //to store the username
    String user;

    //to store the user id
    Long userID;

    //to store the selected image
    ImageView selectImg;

    //if user not selected any image, give this as default image
    Uri imageUri = Uri.parse("android.resource://com.example.swathi.myproject/drawable/profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to open the default page on launching of application
        setContentView(R.layout.activity_main);

        //create object of employeeDatabase class
        employeeDatabase = new EmployeeDatabase(this);

        //to get the writable instance of db
        //@TODO : not used
        SQLiteDatabase db = employeeDatabase.getWritableDatabase();
    }

    /**
     * for login, user has to enter his username and password to login into the application
     * @param view
     */
    public void loginUser(View view) {

        //get the username value from user
        EditText username = (EditText) findViewById(R.id.userName);

        //get the password from user
        EditText password = (EditText) findViewById(R.id.password);

        //convert the username value into string
        String uname = username.getText().toString();

        //convert the password value into string
        String pword = password.getText().toString();

        /**
         * Check if username or password is given or not,
         * if any one field is empty, show the appropriate error message
         * else compare the username and password in DB
         */
        if(TextUtils.isEmpty(uname) ||
                TextUtils.isEmpty(pword)){
            Toast.makeText(this,"Please enter username and password", Toast.LENGTH_LONG).show();
        }
        else
        {
            //get the writable instance of employeeDatabase db
            SQLiteDatabase db = employeeDatabase.getWritableDatabase();

            //list the required columns
            String[] columns = {employeeDatabase.UID,employeeDatabase.IMAGE};

            //list the selection arguments for where clause
            String[] selectionArgs = {uname,pword};

            /**
             * execute the query by using db.query()
             * this function will take multiple arguments
             * 1. Table name
             * 2. columns
             * 3. selection string
             * 4. selection arguments
             * 5. group by
             * 6. having
             * 7. order by
             * 8. limit
             */
            Cursor cursor = db.query(employeeDatabase.TABLE_NAME,columns, employeeDatabase.USERNAME +"=? AND "+employeeDatabase.PASSWORD +" =? " , selectionArgs,null,null,null,null);
            Log.d("cursor", String.valueOf(cursor.getColumnNames()));

            //if user exists
            if(cursor != null && cursor.getCount()> 0) {

                //iterate through all the values
                while (cursor.moveToNext()) {

                    //get the index of image column
                    int imgIndex = cursor.getColumnIndex(employeeDatabase.IMAGE);

                    Log.d("index", String.valueOf(imgIndex));

                    /**
                     * get the value from db based on column index
                     *
                     * get the image path from db
                     */
                    String uri = cursor.getString(imgIndex);

                    // int imgindex = cursor.getColumnIndex(employeeDatabase.IMAGE);
                    //String uri = cursor.getString(imgindex);

                    //convert the value from string to image uri
                    imageUri = Uri.parse(uri);

                    //get the column index of "UID" column
                    int idIndex = cursor.getColumnIndex(employeeDatabase.UID);

                    //get the userid
                    userID = cursor.getLong(idIndex);
                }

                //set the user name
                user = uname;
                //setContentView(R.layout.successfull_login_layout);

                //call the dashboard function , to load the default profile page for user
                dashboard();

            }
            else
            {
                /**
                 * If username or password is invalid, then show the erro message
                 */
                Toast.makeText(this, "Give Valid Username and Password", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * to signup
     *
     * this method will load the sinup page for user
     *
     * @param view
     */
    public void signupUser(View view) {

        //set the view page
        setContentView(R.layout.signup_layout);

        //get the image
        selectImg = (ImageView) findViewById(R.id.enterImg);

        //and provide onclick functionality to select the image from galary
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create Intent object
                Intent intent = new Intent();

                //set the type as image
                intent.setType("image/*");

                //set the action as Intent.ACTION_GET_CONTENT
                intent.setAction(Intent.ACTION_GET_CONTENT);

                /**
                 * Starting another activity doesn't have to be one-way.
                 * You can also start another activity and receive a result back.
                 * To receive a result, call startActivityForResult() (instead of startActivity()).
                 */
                //use startActivityForResult() to return the selected image to the application
                startActivityForResult(Intent.createChooser(intent,"select image"),1);
            }
        });

    }

    /**
     * to get the result from activity
     * this method has 3 arguments
     * 1. The request code you passed to startActivityForResult().
     *
     * 2. A result code specified by the second activity.
     * This is either RESULT_OK if the operation was successful or
     * RESULT_CANCELED if the user backed out or the operation failed for some reason
     *
     * 3. An Intent that carries the result data.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK) {
            if(requestCode == 1) {
                //if we got the result, then set that to image uri
                imageUri = data.getData();
                selectImg.setImageURI(data.getData());
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * To save the user details in DB
     * @param view
     */
    public void saveUser(View view) {

        //get the name of user
        EditText nameTxt = (EditText) findViewById(R.id.enterName);

        //get the username
        EditText usernameTxt = (EditText) findViewById(R.id.enterUserName);

        //get the password
        EditText passwordTxt = (EditText) findViewById(R.id.enterPassword);

        //get the image, @TODO : not used
        ImageView selectedImg = (ImageView) findViewById(R.id.enterImg);

        //convert nameTxt to string
        String name = nameTxt.getText().toString();

        //convert userNameTxt to string
        String uname = usernameTxt.getText().toString();

        //convert passwordTxt to string
        String pword = passwordTxt.getText().toString();

        //convert imageUri to string
        String selctedImg = imageUri.toString();

        //check if any of the fields are empty
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(uname) || TextUtils.isEmpty(pword))
        {
            //if any field is empty then show the error message
            Toast.makeText(this,"all the fields are required",Toast.LENGTH_LONG).show();
        }
        else
        {
            /**
             * If all the fileds are provided by the user,
             * then insert that into DB
             */

            //get the writable instance of employee database db
            SQLiteDatabase db = employeeDatabase.getWritableDatabase();

            //
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
                userID = id;
                dashboard();
            }
        }
    }

    public void updateUser(View view) {

        Log.d("user id",userID.toString());

        String[] whereArgs = {userID.toString()};
        EditText nameTxt = (EditText) findViewById(R.id.editName);
        EditText usernameTxt = (EditText) findViewById(R.id.editPassword);
        EditText passwordTxt = (EditText) findViewById(R.id.editPassword);

        ImageView selectedImg = (ImageView) findViewById(R.id.editImg);

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

            //long id = db.insert(employeeDatabase.TABLE_NAME,null,contentValues);
            db.update(employeeDatabase.TABLE_NAME,contentValues,employeeDatabase.UID+ " =? ",whereArgs);

            Toast.makeText(this,name + "has been updated successfully ",Toast.LENGTH_SHORT).show();

            dashboard();

        }
    }

    public void logout(View view) {
        setContentView(R.layout.activity_main);
    }

    public void editProfile()
    {
        setContentView(R.layout.edit_profile);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.options,android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editUserName = (EditText) findViewById(R.id.editUserName);
        EditText editPassword = (EditText) findViewById(R.id.editPassword);
        selectImg = (ImageView) findViewById(R.id.editImg);
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"),1);
            }
        });

        String[] columns = {employeeDatabase.NAME,employeeDatabase.USERNAME,employeeDatabase.PASSWORD,employeeDatabase.IMAGE};
        String[] selectionArgs = {user};
        SQLiteDatabase db =  employeeDatabase.getWritableDatabase();
        Cursor cursor = db.query(employeeDatabase.TABLE_NAME,columns,employeeDatabase.USERNAME +"=?",selectionArgs,null,null,null,null);

        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext())
            {
                int nameIndex = cursor.getColumnIndex(employeeDatabase.NAME);
                String nameValue = cursor.getString(nameIndex);
                editName.setText(nameValue);

                int usernameIndex = cursor.getColumnIndex(employeeDatabase.USERNAME);
                String userNameValue = cursor.getString(usernameIndex);
                editUserName.setText(userNameValue);

                int passwordIndex = cursor.getColumnIndex(employeeDatabase.PASSWORD);
                String passwordValue = cursor.getString(passwordIndex);
                editPassword.setText(passwordValue);

                int imageIndex = cursor.getColumnIndex(employeeDatabase.IMAGE);
                String imageValue = cursor.getString(imageIndex);
                selectImg.setImageURI(Uri.parse(imageValue));


                //editName.setText();
            }
        }

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
        else if(option.equals("Edit Profile"))
        {
            Log.d("options","entered into edit profile");
            editProfile();
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
