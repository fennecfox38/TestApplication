/**************************************************************************************************************************************
 * File: RegisterDataActivity.java
 * Parent Package : com.android.test.PersonalData
 * Author: JunGu Kang (fennecfox38@gmail.com)
 *
 * This Activity 'RegisterDataActivity' is sub activity for 'PersonalDataActivity'.
 * This Activity is designed to handle especially the member values of 'PersonalData'.
 * This Activity is always called with result back requested and extra data that contains 'PersonalData'.
 * Only clicking 'Submit' button terminates this activity with 'RESULT_CODE_OK'& submitted 'PersonalData' return.
 * Otherwise, 'onBackPressed' or 'onOptionItemSelected(android.R.id.home)' will terminates this activity without modified 'PersonalData' return.
 * Manually setting 'age' is not allowed. 'age' shall be set automatically as 'birthday' changes.
 * Modifying 'birthday' directly is disabled. 'DatePickerDialog' will pop up to set 'birthday' indirectly.
 **************************************************************************************************************************************/
package com.android.test.PersonalData;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.test.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class RegisterDataActivity extends AppCompatActivity {
    private PersonalData person,original;
    private EditText edittxt_name,edittxt_pw,edittxt_birth,edittxt_age;
    private RadioButton rBtn_male, rBtn_female, rBtn_NA;
    private Spinner spin_marriage;
    private CheckBox chkbx_child;
    private DrawerLayout drawer_register_data;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable 'HomeAsUp' from ActionBar.

        drawer_register_data = findViewById(R.id.drawer_register_data);
        NavigationView nav_register_data = findViewById(R.id.navigationView);
        nav_register_data.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_person1: person = new PersonalData(getString(R.string.Person1),"1q2w3e4r5t!",1975,3,21,PersonalData.Sex.Male,PersonalData.Marriage.Divorced,true); break;
                    case R.id.menu_person2: person = new PersonalData(getString(R.string.Person2),"1q2w3e4r5t!",1994,10,8,PersonalData.Sex.Female,PersonalData.Marriage.Married,false); break;
                    case R.id.menu_person3: person = new PersonalData(getString(R.string.Person3),"qwerty",2019,1,24, PersonalData.Sex.Female, PersonalData.Marriage.Single,false); break;
                    case R.id.menu_person4: person = new PersonalData(getString(R.string.Person4),"1q2w3e4r5t!",1985,7,19,PersonalData.Sex.Male,PersonalData.Marriage.Married,true); break;
                    case R.id.menu_person5: person = new PersonalData(getString(R.string.Person5),"1q2w3e4r5t!",1990,12,23,PersonalData.Sex.Female,PersonalData.Marriage.Married,false); break;
                    case R.id.menu_person6: person = new PersonalData(getString(R.string.Person6),"qwerty",2019,6,20, PersonalData.Sex.Male, PersonalData.Marriage.Single,false); break;
                }
                loadDataToField();
                drawer_register_data.closeDrawer(GravityCompat.END);
                return false;
            }
        });

        edittxt_name=findViewById(R.id.edittxt_name);
        edittxt_name.addTextChangedListener(new TextWatcher() { // Set TextWatcher as TextChangeListener for 'edittxt_name'
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { person.setName(s.toString()); }
        });

        edittxt_pw=findViewById(R.id.edittxt_pw);
        edittxt_pw.addTextChangedListener(new TextWatcher() { // Set TextWatcher as TextChangeListener for 'edittxt_name'
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { person.setPassword(s.toString()); }
            // {person.setPassword(sha256(s.toString())); } Password Encryption is on planning.
        });

        edittxt_age= findViewById(R.id.edittxt_age);
        edittxt_birth=findViewById(R.id.edittxt_birth);
        edittxt_age.setFocusable(false); // Direct modification of 'edittxt_age' is disabled.
        edittxt_age.setOnClickListener(new EditText.OnClickListener(){
            @Override public void onClick(View v) { // Toast "Choose Birthday First." when 'edittxt_age' is clicked.
                Toast.makeText(RegisterDataActivity.this,getString(R.string.ChooseBirthdayFirst),Toast.LENGTH_SHORT).show(); }
        });
        edittxt_birth.setFocusable(false); // Direct modification of 'edittxt_birth' is disabled.
        edittxt_birth.setOnClickListener(new EditText.OnClickListener(){ // But make DatePickDialog on 'onClick'.
            @Override public void onClick(View view){
                DatePickerDialog datePick=new DatePickerDialog(RegisterDataActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override public void onDateSet(DatePicker view, int y, int m, int d) { // Selected date will be transmitted as parameter.
                        person.birthday.set(y,m+1,d); // Set birthday.
                        edittxt_birth.setText(person.birthday.getDate()); // Modifying 'edittxt_birth'. Get String from 'String getDate()'.
                        edittxt_age.setText(String.format(Locale.getDefault(),"%d",person.getAge())); // Set age also.
                    }
                },person.birthday.getYear(),person.birthday.getMonth()-1,person.birthday.getDay()); // Set default date as pre-set date (default:today).
                datePick.getDatePicker().setMaxDate(System.currentTimeMillis()); // birthday cannot be future.
                datePick.show();
            }
        });

        RadioGroup rGroup_sex = findViewById(R.id.rGroup_sex); // set RadioGroup for 'Sex'.
        rGroup_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rBtn_male: person.setSex(PersonalData.Sex.Male); break;
                    case R.id.rBtn_female: person.setSex(PersonalData.Sex.Female); break;
                    default: person.setSex(PersonalData.Sex.NA); break;
                }
            }
        });
        rBtn_male=findViewById(R.id.rBtn_male);
        rBtn_female=findViewById(R.id.rBtn_female);
        rBtn_NA=findViewById(R.id.rBtn_NA);

        spin_marriage=findViewById(R.id.spin_marriage); // set Spinner for 'Marriage'.
        ArrayAdapter<String> arrAdapt_spin =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.marriage_list));
        arrAdapt_spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_marriage.setAdapter(arrAdapt_spin);
        spin_marriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                person.setMarriage(position); }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        chkbx_child=findViewById(R.id.chkbx_child);
        chkbx_child.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                person.setChildren(isChecked);
            }
        });


        Button.OnClickListener onBtnClickListen = new Button.OnClickListener(){
            @Override public void onClick(View v) { // set OnClickListener for each button.
                String txt=null;
                switch(v.getId()){
                    case R.id.btn_verify: txt=getString(R.string.Verify); verify(); break;
                    case R.id.btn_submit: txt=getString(R.string.Submit); submit(); break;
                    case R.id.btn_clear: txt=getString(R.string.Clear); clear(); break;
                    default: break; }
                Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT).show();
            }
            protected void verify(){ } // Verification for 'password' is on planning.
            protected void submit(){
                getDataFromField(); // Get renewed Data from field before transmit data.
                Intent resultBack = new Intent();
                resultBack.putExtra("person",person);
                resultBack.putExtra("original",original);
                setResult(RESULT_OK,resultBack);
                finish();
            }
            protected void clear(){ person.resetData(); /*update();*/ loadDataToField(); }
        };
        Button btn_verify = findViewById(R.id.btn_verify);
        Button btn_submit = findViewById(R.id.btn_submit);
        Button btn_clear = findViewById(R.id.btn_clear);
        btn_verify.setOnClickListener(onBtnClickListen);
        btn_submit.setOnClickListener(onBtnClickListen);
        btn_clear.setOnClickListener(onBtnClickListen);

        // Get 'person' from transmitted Intent.
        original = getIntent().getParcelableExtra("person");
        person = original.clone();
        loadDataToField(); // load data from object to Edit field ('View' object)
    }

    public void getDataFromField(){ // Get renewed Data from field before transmit data.
        person.setName(edittxt_name.getText().toString());
        person.setPassword(edittxt_pw.getText().toString());
        person.birthday.setDate(edittxt_birth.getText().toString());
        person.getAge();
        person.setSex((rBtn_male.isChecked()?0:(rBtn_female.isChecked()?1:2)));
        person.setMarriage(spin_marriage.getSelectedItemPosition());
        person.setChildren(chkbx_child.isChecked());
    }

    public void loadDataToField(){ // load data from object to Edit field ('View' object)
        edittxt_name.setText(person.getName());
        edittxt_name.setSelection(edittxt_name.length());
        edittxt_pw.setText(person.getPassword());
        edittxt_pw.setSelection(edittxt_pw.length());
        edittxt_birth.setText(person.birthday.getDate());
        edittxt_age.setText(String.format(Locale.getDefault(),"%d",person.getAge()));
        switch(person.getSex().ordinal()){
            case 0: rBtn_male.setChecked(true); break;
            case 1: rBtn_female.setChecked(true); break;
            case 2: rBtn_NA.setChecked(true); break;
        }
        spin_marriage.setSelection(person.getMarriage().ordinal());
        chkbx_child.setChecked(person.getChildren());
    }

    @Override
    public void onBackPressed() {
        if(drawer_register_data.isDrawerOpen(GravityCompat.END)) drawer_register_data.closeDrawers();
        else{
            super.onBackPressed();
            setResult(RESULT_CANCELED);
        }
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_register_data,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: onBackPressed(); break;
            case R.id.menu_btn_nav:
                if(drawer_register_data.isDrawerOpen(GravityCompat.END))
                    drawer_register_data.closeDrawer(GravityCompat.END);
                else drawer_register_data.openDrawer(GravityCompat.END);
            break;
        } return super.onOptionsItemSelected(item);
    }
}
