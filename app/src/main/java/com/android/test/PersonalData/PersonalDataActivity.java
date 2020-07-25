/**************************************************************************************************************************************
 * File: PersonalDataActivity.java
 * Parent Package : com.android.test.PersonalData
 * Author: JunGu Kang (fennecfox38@gmail.com)
 *
 * This Activity 'PersonalDataActivity' is main activity for this package 'com.android.test.PersonalData'.
 * This Activity is designed to handle 'list' and 'db' of 'PersonalData'
 *                              based on class 'PersonalData', 'PersonRecycleAdapter','PersonalDataDBHelper'.
 * List will be derived from db 'onCreate', and saved to db 'onDestroy'.
 * List will be shown by 'RecyclerView' (androidx.recyclerview.widget.RecyclerView)
 * When add new or edit 'PersonalData' is requested, it calls 'RegisterDataActivity' to modify data.
 **************************************************************************************************************************************/
package com.android.test.PersonalData;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.test.PersonalData.PersonalDataDBHelper.*;
import com.android.test.R;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class PersonalDataActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD = 101, REQUEST_CODE_EDIT=102;
    private int adapterPosition;
    private SearchView searchView;
    private PersonRecyclerAdapter personAdapter;
    private PersonalDataDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // Show title and 'HomeAsUp' on Action Bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        // Floating Action Button adds person.
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { call_RegisterData_Add(); }
        });

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout_personal_data);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        Snackbar.make(refreshLayout,getString(R.string.Refreshed),Snackbar.LENGTH_SHORT).show();
                        saveList(); loadList();refreshLayout.setRefreshing(false);
                    }}, 500); }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)) ;

        personAdapter = new PersonRecyclerAdapter();
        personAdapter.setOnItemClickListener(new PersonRecyclerAdapter.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                final CardView cardView = view.findViewById(R.id.cardView);
                cardView.setCardBackgroundColor(getColorAccent(getApplicationContext()));
                call_RegisterData_Edit(position, personAdapter.getList().get(position));
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.cardViewBackground));
                    }}, 50);
            }
        });
        personAdapter.setOnItemLongClickListener(new PersonRecyclerAdapter.OnItemLongClickListener() {
            @Override public void onItemLongClick(View view, int position) {
                final CardView cardView = view.findViewById(R.id.cardView);
                cardView.setCardBackgroundColor(getColorAccent(getApplicationContext()));
                deletePersonItem(position);
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.cardViewBackground));
                    }}, 50);
            }
        });
        recyclerView.setAdapter(personAdapter);
        dbHelper = new PersonalDataDBHelper(this);
        loadList();
    }

    @Override protected void onDestroy() {
        saveList();
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED) {
            Snackbar.make(findViewById(R.id.refresh_layout_personal_data), getString(R.string.Canceled), Snackbar.LENGTH_SHORT).show();
            return;
        }
        assert data != null;
        PersonalData person = data.getParcelableExtra("person");
        switch (requestCode) {
            case REQUEST_CODE_ADD: after_RegisterData_Add(person); break;
            case REQUEST_CODE_EDIT: after_RegisterData_Edit(person); break;
            default: break;
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personaldata_activity,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.SearchHint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                personAdapter.restoreListFromBackup();
                personAdapter.queryPerson(query,getResources());
                Toast.makeText(getApplicationContext(),"query: "+query,Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override public boolean onQueryTextChange(String newText) {
                personAdapter.restoreListFromBackup();
                if(newText.length()==0) return false;
                personAdapter.queryPerson(newText,getResources());
                return false; }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override public boolean onClose() {
                personAdapter.restoreListFromBackup();
                Toast.makeText(getApplicationContext(),"SearchView Closed",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: finish(); break;
            case R.id.menu_add: call_RegisterData_Add(); break;
            case R.id.menu_sort: sortOption(); break;
            case R.id.menu_import: action_import(); break;
            case R.id.menu_export: action_export(); break;
            case R.id.menu_share: action_share(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void call_RegisterData_Add(){
        Intent resultRequest = new Intent(getApplicationContext(), RegisterDataActivity.class);
        resultRequest.putExtra("person",new PersonalData());
        startActivityForResult(resultRequest, REQUEST_CODE_ADD);
    }
    protected void call_RegisterData_Edit(int position, PersonalData person){
        Intent resultRequest = new Intent(getApplicationContext(), RegisterDataActivity.class);
        resultRequest.putExtra("person",person);
        adapterPosition=position;
        startActivityForResult(resultRequest, REQUEST_CODE_EDIT);
    }
    protected void deletePersonItem(int position){
        final PersonalData backup = personAdapter.deletePerson(position);
        Snackbar.make(findViewById(R.id.refresh_layout_personal_data), getString(R.string.PersonDeleted), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.Undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { personAdapter.addPerson(backup); }
                }).show();
    }

    protected void sortOption(){
        Toast.makeText(getApplicationContext(),getString(R.string.Sort),Toast.LENGTH_SHORT).show();
    }

    protected void action_import(){
        Toast.makeText(getApplicationContext(),getString(R.string.Import),Toast.LENGTH_SHORT).show();
    }
    protected void action_export(){
        /*try{
            String exportDBPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dataDBPath = Environment.getDataDirectory().getAbsolutePath();

            dataDBPath += "/data/"+getPackageName()+"/databases/"+PersonalDataDBHelper.DATABASE_NAME;
            exportDBPath += PersonalDataDBHelper.DATABASE_NAME;

            File dataDB = new File(dataDBPath);
            File exportDB = new File(exportDBPath);

            if(dataDB.exists()){
                FileChannel src = new FileInputStream(dataDB).getChannel();
                FileChannel dst = new FileOutputStream(exportDB).getChannel();
                dst.transferFrom(src,0,src.size());
                src.close();
                dst.close();
            }else{Toast.makeText(getApplicationContext(),"Data not existed",Toast.LENGTH_SHORT).show();}
            if(exportDB.exists())
                Toast.makeText(getApplicationContext(),getString(R.string.Export),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }
    protected void action_share(){
        ArrayList<PersonalData> list = personAdapter.getList();
        StringBuilder strBuild = new StringBuilder();
        for(PersonalData pd : list){ strBuild.append("\n"); strBuild.append(pd.toString(getResources()));  }
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, strBuild.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
    protected void after_RegisterData_Add(final PersonalData person){
        personAdapter.addPerson(person);
        Snackbar.make(findViewById(R.id.refresh_layout_personal_data), getString(R.string.PersonAdded), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.Undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { personAdapter.deletePerson(person); }
                }).show();
    }
    protected void after_RegisterData_Edit(PersonalData person){
        final PersonalData original = personAdapter.editPerson(adapterPosition,person);
        Snackbar.make(findViewById(R.id.refresh_layout_personal_data), getString(R.string.PersonEdited), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.Undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { personAdapter.editPerson(adapterPosition, original);}
                }).show();
    }

    protected void loadList(){
        ArrayList<PersonalData> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(DBContract.SQL_SELECT,null);
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            PersonalData person = new PersonalData();
            person.setName(cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_NAME)));
            person.setPassword(cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_PASSWORD)));
            person.birthday.setDate(cursor.getString(cursor.getColumnIndex(DBContract.COLUMN_BIRTHDAY)));
            person.getAge();
            person.setSex(cursor.getInt(cursor.getColumnIndex(DBContract.COLUMN_SEX)));
            person.setMarriage(cursor.getInt(cursor.getColumnIndex(DBContract.COLUMN_MARRIAGE)));
            person.setChildren((cursor.getInt(cursor.getColumnIndex(DBContract.COLUMN_CHILDREN)) == 1));
            list.add(person);
        }
        cursor.close();
        db.close();
        personAdapter.setList(list);
    }

    protected void saveList(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db == null) return;
        ArrayList<PersonalData> list = personAdapter.getList();

        List<ContentValues> cvList = new ArrayList<>();
        ContentValues cv;
        for(PersonalData pd : list){
            cv = new ContentValues();
            cv.put(DBContract.COLUMN_NAME, pd.getName());
            cv.put(DBContract.COLUMN_PASSWORD,pd.getPassword());
            cv.put(DBContract.COLUMN_BIRTHDAY,pd.birthday.getDate());
            cv.put(DBContract.COLUMN_SEX, pd.getSex().ordinal());
            cv.put(DBContract.COLUMN_MARRIAGE, pd.getMarriage().ordinal());
            cv.put(DBContract.COLUMN_CHILDREN, (pd.getChildren()?1:0));
            cvList.add(cv);
        }
        try {
            db.beginTransaction();                      //clear the table first
            db.delete (DBContract.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:cvList) db.insert(DBContract.TABLE_NAME, null, c);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("saveList", "fail to save list to db. (SQLException e)");
        }
        finally { db.endTransaction(); }
        db.close();
    }

    private int getColorAccent(Context context){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }
}