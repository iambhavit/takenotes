package com.anuntah.takenotes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NotesRecyclerAdapter.OnItemClickListener {

    public static final String title="Title";
    public static final String description="Description";
    public static final String code="code";
    public static final String id="Id";
    public static final String time="time";
    public static final String LINEAR="linear";
    public static final String GRID="grid";
    public static final String LAYOUT="layout";

    int count=0;

    ActionMode mode;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    NotesOpenHelper notesOpenHelper;
    NotesRecyclerAdapter adapter;
    ArrayList<Notes> notes;
    String layout=MainActivity.LINEAR;
    Boolean longclicked=false;
    Boolean onLongClicked=true;
    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBarLayout;


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            layout=savedInstanceState.getString(MainActivity.code);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout=findViewById(R.id.toolbar_layout);
        appBarLayout=findViewById(R.id.appbar);
        preferences=getSharedPreferences("takenotes",MODE_PRIVATE);
        layout=preferences.getString(MainActivity.LAYOUT,"linear");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new intent to stream activity
                Intent streamIntent = new Intent(MainActivity.this, EditNote.class);
                // Start the new Activity
                startActivityForResult(streamIntent,1);
            }
        });

        recyclerView=findViewById(R.id.list_item);
        notes=new ArrayList<>();
        notesOpenHelper=NotesOpenHelper.getInstance(this);
        adapter=new NotesRecyclerAdapter(this,notes,this);
        recyclerView.setAdapter(adapter);
        fetchNotes();
        Collections.reverse(notes);
        layoutChange(layout);

    }

    public void layoutChange(String type){
        if(type.equals(MainActivity.LINEAR)) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }
        if(type.equals(MainActivity.GRID)){
            RecyclerView.LayoutManager layoutManager=new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
    }



    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM hh:mm a",Locale.getDefault());

    private void fetchNotes() {
        String title, description;
        long time;
        int id;
        String label=null;
        SQLiteDatabase db=notesOpenHelper.getReadableDatabase();
        Cursor cursor=db.query(Constants.notes.NAME,null,null,null,null,null,null);
        Cursor cursor1 = db.query(Constants.label.NAME, null, null, null, null, null, null);
        while(cursor.moveToNext()) {
            String datetime=null;
            title=cursor.getString(cursor.getColumnIndex(Constants.notes.COL_TITLE));
            description = cursor.getString(cursor.getColumnIndex(Constants.notes.COL_DESCRIPTION));
            time = cursor.getLong(cursor.getColumnIndex(Constants.notes.COL_TIME));
            id = cursor.getInt(cursor.getColumnIndex(Constants.notes.COL_ID));
            if (cursor1.moveToNext()) {
                label = cursor1.getString(cursor1.getColumnIndex(Constants.label.COL_LABEL));
            }
            if (time != -1) {
                 date = new Date(time);
                 datetime = dateFormat.format(date);
            }
            Notes notes1 = new Notes(title,description, id, datetime, label);
            notes.add(notes1);
            adapter.notifyDataSetChanged();
        }
    }


    boolean selected;
    @Override
    public void OnItemClick(int pos) {
        if(!longclicked) {
            Intent intent = new Intent(this, EditNote.class);
            Bundle b = new Bundle();
            Notes notes1 = notes.get(pos);
            b.putInt("pos", pos);
            b.putInt(Constants.notes.COL_ID, notes1.getId());
            intent.putExtras(b);
            startActivityForResult(intent, 2);
        }
        else {
            if(notes.get(pos).getSelected()) {
                notes.get(pos).setSelected(false);
                count--;
                selected=false;
                for(Notes note:notes)
                {
                    if(note.getSelected()) {
                        selected = true;
                        break;
                    }
                }
            }
            else {
                notes.get(pos).setSelected(true);
                count++;
            }
            if(count==0) {
                callback.onDestroyActionMode(mode);
                mode.finish();
            }
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void OnItemLongClick(final int pos) {
        if(!longclicked) {
            toolbarLayout.setVisibility(View.GONE);
            appBarLayout.setBackgroundColor(Color.rgb(0,100,0));
            count=0;
            count++;
            onLongClicked=false;
            longclicked = true;
            toolbar.startActionMode(callback);
            notes.get(pos).setSelected(true);
            adapter.notifyDataSetChanged();
            toolbar.setVisibility(View.GONE);
        }/*
        final SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
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
                int id = notes.get(pos).getId();
                db.delete(Constants.notes.NAME, "id=?", new String[]{String.valueOf(id)});
                notes.remove(pos);
                adapter.notifyDataSetChanged();
                Intent intent1 = new Intent(MainActivity.this, NotesReceiver.class);
                Bundle b = new Bundle();
                b.putInt(Constants.notes.COL_ID, id);
                intent1.putExtras(b);
                PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,id,intent1,0);
                pendingIntent.cancel();
                AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        });
        dialog.show();*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlarmManager alarmManager;
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        if(requestCode==1) {
            if(resultCode==1) {
                Toast.makeText(this,"Note Saved!",Toast.LENGTH_SHORT).show();
                long time;
                String title=data.getStringExtra(Constants.notes.COL_TITLE);
                String description=data.getStringExtra(Constants.notes.COL_DESCRIPTION);
                time=data.getLongExtra(Constants.notes.COL_TIME,-1);
                String datetime=null;
                if(time!=-1){
                    date=new Date(time);
                    datetime=dateFormat.format(date);
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.notes.COL_TITLE,title);
                contentValues.put(Constants.notes.COL_DESCRIPTION, description);
                contentValues.put(Constants.notes.COL_TIME,time);
                int id=(int)db.insert(Constants.notes.NAME, null, contentValues);
                Notes notes1=new Notes(title,description,id,datetime,null);
                notes.add(0,notes1);
                adapter.notifyDataSetChanged();

                if(time!=-1) {
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(this, NotesReceiver.class);
                    Bundle b = new Bundle();
                    b.putInt(Constants.notes.COL_ID, id);
                    intent.putExtras(b);
                    PendingIntent pendingIntent= PendingIntent.getBroadcast(this,id,intent,0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);
                }
            }
        }
        if(requestCode==2) {
            if(resultCode==1){
                long time;
                String title=data.getStringExtra(Constants.notes.COL_TITLE);
                String description=data.getStringExtra(Constants.notes.COL_DESCRIPTION);
                time=data.getLongExtra(Constants.notes.COL_TIME,-1);
                int id=data.getIntExtra(Constants.notes.COL_ID,-1);
                int pos=data.getIntExtra("pos",-1);
                String datetime=null;
                if(time!=-1){
                    date=new Date(time);
                    datetime=dateFormat.format(date);
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.notes.COL_TITLE,title);
                contentValues.put(Constants.notes.COL_DESCRIPTION, description);
                contentValues.put(Constants.notes.COL_TIME,time);
                contentValues.put(Constants.notes.COL_ID,id);
                db.replace(Constants.notes.NAME, null, contentValues);
                Notes notes1=new Notes(title,description,id,datetime,null);
                notes.remove(pos);
                notes.add(0,notes1);
                adapter.notifyDataSetChanged();

                if(time!=-1) {
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(this, NotesReceiver.class);
                    Bundle b = new Bundle();
                    b.putInt(Constants.notes.COL_ID, id);
                    intent.putExtras(b);
                    PendingIntent pendingIntent= PendingIntent.getBroadcast(this,id,intent,0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);
                }
            }
            if (resultCode == 2) {
                Bundle b = data.getExtras();
                if(b!=null) {
                    int pos = b.getInt("pos", -1);
                    int id = b.getInt(Constants.notes.COL_ID);
                    db.delete(Constants.notes.NAME, "id=?", new String[]{String.valueOf(id)});
                    notes.remove(pos);
                    adapter.notifyDataSetChanged();
                    Intent intent1 = new Intent(this, NotesReceiver.class);
                    intent1.putExtras(b);
                    alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                    PendingIntent pendingIntent=PendingIntent.getBroadcast(this,id,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu1, menu);
            this.menu=menu;
            if(layout.equals(MainActivity.LINEAR))
                menu.getItem(0).setIcon(R.drawable.ic_view_stream_black_24dp);
            else
                menu.getItem(0).setIcon(R.drawable.ic_view_quilt_black_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        SharedPreferences.Editor editor=preferences.edit();
        switch (id){
            case R.id.layout:
                if(layout.equals("linear")) {
                    layout="grid";
                    item.setIcon(R.drawable.ic_view_quilt_black_24dp);
                    editor.putString(MainActivity.LAYOUT,layout).commit();
                    layoutChange(layout);
                }
                else {
                    layout="linear";
                    layoutChange(layout);
                    item.setIcon(R.drawable.ic_view_stream_black_24dp);
                    editor.putString(MainActivity.LAYOUT,layout).commit();
                }
        }
        return true;
    }

    public void onDeleteDialog(){
        final AlertDialog dialog;
        dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("DELETE");
        dialog.setMessage("Do you want to delete selected notes");
        dialog.setButton(dialog.BUTTON_NEGATIVE,"NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Notes note : notes)
                    note.setSelected(false);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setButton(dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSelected();
            }
        });
        dialog.show();


    }

    private void deleteSelected() {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ArrayList<String> id=new ArrayList<>();
        ArrayList<Notes> unselected=new ArrayList<>();
        for(int i=0;i<notes.size();i++){
            if(!notes.get(i).getSelected())
                unselected.add(notes.get(i));
            else
                id.add(String.valueOf(notes.get(i).getId()));
        }

        for (String i : id) {
                db.delete(Constants.notes.NAME, "id=?", new String[]{i});
                Intent intent1 = new Intent(MainActivity.this, NotesReceiver.class);
                Bundle b = new Bundle();
                b.putInt(Constants.notes.COL_ID, Integer.parseInt(i));
                intent1.putExtras(b);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, Integer.parseInt(i), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
        }
            notes.clear();
            notes.addAll(unselected);
            adapter.notifyDataSetChanged();
        longclicked=false;
        toolbar.setVisibility(View.VISIBLE);
    }

    Boolean deleteclick=false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.code,layout);
    }


    ActionMode.Callback callback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater=mode.getMenuInflater();
            inflater.inflate(R.menu.delete_menu,menu);
            MainActivity.this.mode=mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.delete_all:
                    deleteclick=true;
                    onDeleteDialog();
                mode.finish();
                return  true;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            longclicked=false;
            if(!deleteclick) {
                for (Notes note : notes)
                    note.setSelected(false);
            }
            deleteclick=false;
            adapter.notifyDataSetChanged();
            toolbar.setVisibility(View.VISIBLE);
            toolbarLayout.setVisibility(View.VISIBLE);
            appBarLayout.setBackgroundColor(Color.rgb(76,175,80));
        }
    };
    private void onchnge(){
        int i=0;
        //todo
    }
}
