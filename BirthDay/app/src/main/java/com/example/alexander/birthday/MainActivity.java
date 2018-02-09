package com.example.alexander.birthday;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.birthday.data.BirthContract;
import com.example.alexander.birthday.data.BirthCursorAdapter;
import com.example.alexander.birthday.data.BirthDBHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.alexander.birthday.data.BirthContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ELEMENTS_LOADER = 0;
    private BirthDBHelper mDbHelper;
    BirthCursorAdapter  mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> showCreateAllert(null));

        mDbHelper = new BirthDBHelper(this);
        ListView guestListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        guestListView.setEmptyView(emptyView);
        mCursorAdapter = new BirthCursorAdapter(this, null);
        guestListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(ELEMENTS_LOADER, null, this);
        guestListView.setOnItemClickListener((parent, view, position, id) -> {
            Uri currentGuestUri = ContentUris.withAppendedId(ManEntry.CONTENT_URI, id);
            showCreateAllert(currentGuestUri);
        });

        guestListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
            deleteElement(name);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clearTable) {
            deleteElement();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteElement() {

        Runnable run = () -> getContentResolver().delete(ManEntry.CONTENT_URI, null, null);
        allert("ATTENTION", "Do you want to delete all data ?", run);
    }
    private void deleteElement(String name) {

        Runnable run = () -> getContentResolver().delete(ManEntry.CONTENT_URI, ManEntry.COLUMN_NAME+"=?", new String[]{name});
        allert("ATTENTION", "Do you want to delete this man ?", run);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ManEntry.CONTENT_URI,   // URI контент-провайдера для запроса
                BirthContract.getProtection(),             // колонки, которые попадут в результирующий курсор
                null,                   // без условия WHERE
                null,                   // без аргументов
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    public String m_Text;
    public void showCreateAllert(Uri u){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.create_new, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setMaxLines(2);

        builder.setView(viewInflated);
        viewInflated.setPadding(30, 100 ,30, 100);

        uri = u;
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                showDateTimePicker(uri);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private Calendar date;
    private Uri uri;

    public void showDateTimePicker(Uri u) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        this.uri = u;
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            saveGuest(uri);

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


    public void allert(String title, String desc, final Runnable run)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(desc);
        dialog.setNegativeButton("cancel", (dialog12, which) -> dialog12.cancel());
        dialog.setPositiveButton("approve", (dialog1, which) -> run.run());
        dialog.show();
    }


    private void saveGuest(Uri uri) {

        String name = m_Text.trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String bDate  = sdf.format(date.getTime());

        if (uri == null &&
                TextUtils.isEmpty(name) && TextUtils.isEmpty(bDate)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ManEntry.COLUMN_NAME, name);
        values.put(ManEntry.COLUMN_DATE, bDate.toString());

        if (uri == null) {
            Uri newUri = getContentResolver().insert(ManEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Ошибка создания записи", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Запись создана",
                        Toast.LENGTH_SHORT).show();
            }

        } else {

            int rowsAffected = getContentResolver().update(uri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Ошибка при редактировании", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Данные исправлены успешно",
                        Toast.LENGTH_SHORT).show();
            }
        }

        NotificationHelper.scheduleNotification(this);
    }
}
