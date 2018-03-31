package com.anuntah.takenotes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.AttributedCharacterIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditNote extends AppCompatActivity {

    Calendar calendar;
    EditText addtitle;
    NotesOpenHelper notesOpenHelper;
    EditText addnotes;
    String description;
    int id;
    Bundle bundle=new Bundle();
    int pos;
    String title;
    boolean menudisplay=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        addnotes=findViewById(R.id.addnote);
        addtitle=findViewById(R.id.addtitle);
        calendar=Calendar.getInstance(TimeZone.getDefault());


        Intent stopIntent=new Intent(this,Alaram_Manager.class);
        stopService(stopIntent);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        notesOpenHelper=NotesOpenHelper.getInstance(this);
        Intent intent=getIntent();
        bundle=intent.getExtras();
        if(bundle!=null) {
            int u=bundle.getInt(MainActivity.code,-1);
            if(u==10)
                menudisplay=false;
            id = bundle.getInt(Constants.notes.COL_ID, -1);
            pos=bundle.getInt("pos",-1);
            SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
            Cursor cursor = db.query(Constants.notes.NAME, new String[]{Constants.notes.COL_DESCRIPTION,Constants.notes.COL_TITLE}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            description = cursor.getString(cursor.getColumnIndex(Constants.notes.COL_DESCRIPTION));
            title = cursor.getString(cursor.getColumnIndex(Constants.notes.COL_TITLE));
            addnotes.setText(description);
            addtitle.setText(title);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(menudisplay)
        return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                showDiscardDialog();
            return true;
            case R.id.done:addToDb();
            return true;
            case R.id.action_settings:setTime();
            return true;
            case R.id.delete:if(bundle!=null)
                onDelete();
                else {
                onDiscardChanges();

            }
            return true;
        }
        return true;
    }

    private void showDiscardDialog() {
        String title1=addtitle.getText().toString().trim();
        String description1=addnotes.getText().toString().trim();
        if(bundle==null){
            if(!(description1.equals("")&&title1.equals("")))
                onDiscardChanges();
            else
                this.finish();
        }
        else {
            if(title1.equals(title)&&description1.equals(description))
                this.finish();
            else
                onDiscardChanges();
        }
    }

    public void onDiscardChanges(){
        final AlertDialog dialog;
        dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("Discard");
        dialog.setMessage("Do you want to discard the changes?");
        dialog.setButton(dialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.setButton(dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditNote.this.finish();

            }
        });
        dialog.show();
    }

    private void onDelete() {

        final AlertDialog dialog;
        dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("DELETE");
        dialog.setMessage("Do you want to delete this note");
        dialog.setButton(dialog.BUTTON_NEGATIVE,"NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.setButton(dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete();

            }
        });
        dialog.show();

    }

    private void delete() {
        if(bundle!=null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(bundle);
            setResult(2, intent);
            finish();
        }
        else
            Toast.makeText(this,"Note is empty",Toast.LENGTH_SHORT).show();
    }

    private void setTime() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);

                update();
            }
        };
        DatePickerDialog dialog=new DatePickerDialog(this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        dialog.show();

    }

    private void update() {


        TimePickerDialog.OnTimeSetListener timePickerDialog= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                onupdate();
            }
        };

        TimePickerDialog dialog=new TimePickerDialog(this,timePickerDialog,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);

        dialog.show();

    }

    long t=-1;
    private void onupdate() {
        Date date=calendar.getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("E dd MMM hh:mm a", Locale.getDefault());
        dateFormat.format(date);
        t=date.getTime();
    }

    private void addToDb() {
        String title=addtitle.getText().toString().trim();
        description=addnotes.getText().toString().trim();
        if(description.equals("")&&title.equals(""))
            Toast.makeText(this,
                    "Note is empty",Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.notes.COL_DESCRIPTION, description);
            intent.putExtra(Constants.notes.COL_TITLE, title);
            intent.putExtra(Constants.notes.COL_ID, id);
            intent.putExtra("pos", pos);
            if (t != -1) {
                intent.putExtra(Constants.notes.COL_TIME, t);
            }
            setResult(1, intent);
            finish();
        }
    }
    /*public void setTag(View view){
        LinearLayout layout=findViewById(R.id.taglayout);
        EditText editText=new EditText(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        layoutParams.setMargins(4,4,4,4);
        editText.setLayoutParams(layoutParams);
        editText.setText("Tags");
        layout.addView(editText);
    }*/
}
