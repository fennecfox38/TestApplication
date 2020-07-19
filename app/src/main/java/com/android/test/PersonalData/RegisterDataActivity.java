package com.android.test.PersonalData;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;

import com.android.test.R;

import java.util.Locale;

public class RegisterDataActivity extends AppCompatActivity {
    private PersonalData person;
    private EditText edittxt_name,edittxt_pw,edittxt_birth,edittxt_age;
    private RadioGroup rGroup_sex;
    private RadioButton rBtn_male, rBtn_female, rBtn_NA;
    private Spinner spin_marriage;
    private ArrayAdapter<String> arrAdapt_spin;
    private CheckBox chkbx_child;
    private Button btn_verify, btn_submit, btn_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edittxt_name=findViewById(R.id.edittxt_name);
        edittxt_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) { person.setName(s.toString()); }
        });

        edittxt_pw=findViewById(R.id.edittxt_pw);
        edittxt_pw.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                person.setPassword(s.toString());
            }//{person.setPassword(sha256(s.toString())); }
        });

        edittxt_age= findViewById(R.id.edittxt_age);
        edittxt_birth=findViewById(R.id.edittxt_birth);
        edittxt_birth.setFocusable(false);
        edittxt_birth.setOnClickListener(new EditText.OnClickListener(){
            @Override public void onClick(View view){
                DatePickerDialog datePick=new DatePickerDialog(RegisterDataActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        person.birthday.set(y,m+1,d);
                        edittxt_birth.setText(person.birthday.getDate());
                        edittxt_age.setText(String.format(Locale.getDefault(),"%d",person.getAge()));
                    }
                },person.birthday.getYear(),person.birthday.getMonth()-1,person.birthday.getDay());
                datePick.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePick.show();
            }
        });

        rGroup_sex=findViewById(R.id.rGroup_sex);
        rGroup_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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

        chkbx_child=findViewById(R.id.chkbx_child);
        chkbx_child.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                person.setChildren(isChecked);
            }
        });

        arrAdapt_spin= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.marriage_list));
        arrAdapt_spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_marriage=findViewById(R.id.spin_marriage);
        spin_marriage.setAdapter(arrAdapt_spin);
        spin_marriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                person.setMarriage(position); }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });


        btn_verify=findViewById(R.id.btn_verify);
        btn_submit=findViewById(R.id.btn_submit);
        btn_clear=findViewById(R.id.btn_clear);
        Button.OnClickListener onBtnClickListen = new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                String txt=null;
                switch(v.getId()){
                    case R.id.btn_verify: txt=getString(R.string.Verify); verify(); break;
                    case R.id.btn_submit: txt=getString(R.string.Submit); submit(); break;
                    case R.id.btn_clear: txt=getString(R.string.Clear); clear(); break;
                    default: break; }
                Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT).show();
            }
            protected void verify(){ }
            protected void submit(){
                Intent resultBack = new Intent();
                resultBack.putExtra("person",person);
                setResult(RESULT_OK,resultBack);
                finish();
            }
            protected void clear(){ person.resetData(); /*update();*/ loadField(); }
        };
        btn_verify.setOnClickListener(onBtnClickListen);
        btn_submit.setOnClickListener(onBtnClickListen);
        btn_clear.setOnClickListener(onBtnClickListen);

        person = getIntent().getExtras().getParcelable("person");
        loadField();
    }
    
    public void loadField(){
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
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
