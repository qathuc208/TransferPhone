package com.example.nonsleeping.transferphone;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.nonsleeping.transferphone._adapter.RecyclerViewAdapter;
import com.example.nonsleeping.transferphone._class.Const;
import com.example.nonsleeping.transferphone._interfaces.IF_GetContacts;
import com.example.nonsleeping.transferphone.dto.ContactInfor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private String TAG = "MainActivity";
    private RecyclerViewAdapter adapter;
    private Button btnSearch;
    private Button btnTransfer;
    private EditText etOne;
    private EditText etTwo;
    private List<ContactInfor> inforList;
    private List<ContactInfor> itemList;
    private LinearLayoutManager layoutManager;
    private LinearLayout lnTransfer;
    boolean mIsExit = false;
    private Handler myHandler = new Handler();
    private Runnable myRunnable = new MyRunable();
    private ProgressBar progressbar;
    private RecyclerView rv;
    private ScrollView svIntro;
    private TextView tvNodata;

    class MyRunable implements Runnable {
        @Override
        public void run() {
            MainActivity.this.mIsExit = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (Build.VERSION.SDK_INT <= 22) {
            startApplication();
        } else if (checkPermissions()) {
            startApplication();
        } else {
            setPermissions();
        }
    }

    void startApplication() {
        this.itemList = new ArrayList<>();
        this.inforList = new ArrayList<>();
        this.svIntro = (ScrollView) findViewById(R.id.svIntro);
        this.lnTransfer = (LinearLayout) findViewById(R.id.lnTransfer);
        this.btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(this);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.progressbar = (ProgressBar) findViewById(R.id.progressbar);
        this.tvNodata = (TextView) findViewById(R.id.tvNodata);
        this.etOne = (EditText) findViewById(R.id.etOne);
        this.etTwo = (EditText) findViewById(R.id.etTwo);

        this.adapter = new RecyclerViewAdapter(getApplicationContext(), this.itemList);
        this.layoutManager = new LinearLayoutManager(getApplicationContext());
        this.rv = (RecyclerView) findViewById(R.id.rv);
        this.rv.setAdapter(this.adapter);
        getAllContacts();
    }

     void getAllContacts() {
        Log.d(TAG, "onBackPressed");
        this.inforList.clear();
        Const constR = new Const(getApplicationContext());
        constR.getClass();
        constR.new getAllContacts(new MyCallBack()).execute(new List[0]);
    }
    void updateContacts(String id, String phone, int TYPE, String _ID) {
        Log.d(TAG, "updateContacts");
        ContentResolver cr = getContentResolver();
        String where = "contact_id = ? AND mimetype = ? AND _id = ? AND " + String.valueOf("data2") + " = ? ";
        String[] params = TYPE == 1 ? new String[]{id, "vnd.android.cursor.item/phone_v2", _ID, String.valueOf(1)}
                        : TYPE == 2 ? new String[]{id, "vnd.android.cursor.item/phone_v2", _ID, String.valueOf(2)}
                        : TYPE == 3 ? new String[]{id, "vnd.android.cursor.item/phone_v2", _ID, String.valueOf(3)}
                        : TYPE == 7 ? new String[]{id, "vnd.android.cursor.item/phone_v2", _ID, String.valueOf(7)}
                        : new String[]{id, "vnd.android.cursor.item/phone_v2", _ID, String.valueOf(TYPE)};

        if (params != null) {
            Cursor phoneCur = managedQuery(ContactsContract.Data.CONTENT_URI, null, where, params, null);
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            if (phoneCur != null) {
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(where, params).withValue("data1", phone).build());
            }
            phoneCur.close();
            try {
                cr.applyBatch("com.android.contacts", ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (!isTaskRoot()) {
            super.onBackPressed();
        } else if (this.mIsExit) {
            super.onBackPressed();
        } else {
            this.mIsExit = true;
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.exit));
            this.myHandler.postDelayed(this.myRunnable, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.how_to_use) {
            dialogIntro(getResources().getString(R.string.introduce));
        } else if (id == R.id.delete_all_contacts) {
            conFirmDeleteAll();
        } else if (id == R.id.action_notes) {
            dialogIntro(getResources().getString(R.string.note_content));
        }
        return super.onOptionsItemSelected(item);
    }

    void dialogIntro(String msg) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage((CharSequence) msg)
                .setPositiveButton(getResources().getString(R.string.ok), new onCancelDialog());
        builder.create().show();
    }

    void conFirmDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.confirm_delete_all_contacts_layout, null);
        builder.setView(dialogView);

        final String _CHAR = "" + (new Random().nextInt(500) + 4567);
        ((TextView) dialogView.findViewById(R.id.tvChar)).setText("" + _CHAR);
        final EditText etConfirm = (EditText) dialogView.findViewById(R.id.etConfirm);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.are_you_sure_delete_all_u_contacts))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (_CHAR.equals(etConfirm.getText().toString())) {
                            MainActivity.this.deleteAll();
                            return;
                        }
                        new Const(MainActivity.this.getApplicationContext()).showNotify(getResources().getString(R.string.character_wrong));
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new onDissmisslDialog());
        builder.create().show();
    }

    void deleteAll() {
        Log.d(TAG,"deleteAll");
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            cr.delete(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, cursor.getString(cursor.getColumnIndex("lookup"))), null, null);
        }
        cursor.close();
        new Const(getApplicationContext()).showNotify(getResources().getString(R.string.delete_all));
    }

    class MyCallBack implements IF_GetContacts{
        MyCallBack(){
        }

        @Override
        public void onFail() {
            MainActivity.this.progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(List<ContactInfor> list) {
            MainActivity.this.progressbar.setVisibility(View.GONE);
            MainActivity.this.inforList = list;
        }
    }

    void haveData() {
        this.lnTransfer.setVisibility(View.VISIBLE);
        this.tvNodata.setVisibility(View.GONE);
    }

    void noData() {
        this.lnTransfer.setVisibility(View.GONE);
        this.tvNodata.setVisibility(View.VISIBLE);
    }

    void actionSearch() {
        String s = "";
        s = this.etOne.getText().toString();
        this.itemList.clear();
        if (s.length() <= 0) {
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.you_must_input)+ " " + getResources().getString(R.string.old_prefix));
        } else if (this.inforList.size() > 0) {
            for (ContactInfor contactInfor : this.inforList) {
                if (contactInfor.getPhoneNumber().startsWith(s)) {
                    this.itemList.add(contactInfor);
                }
            }

            if (this.itemList.size() > 0) {
                this.adapter.updateList(this.itemList);
                haveData();
                return;
            }
            noData();
        } else {
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.can_not_get_your_contacts));
        }
    }

    void actionTransfer() {
        String old = "";
        String newStr = "";
        old = this.etOne.getText().toString();
        newStr = this.etTwo.getText().toString();

        if (newStr.length() <= 0) {
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.you_must_input)+ " " + getResources().getString(R.string.new_prefix));
        } else if (this.itemList.size() > 0) {
            for (ContactInfor obj : this.itemList) {
                String phone = obj.getPhoneNumber();
                phone = newStr + phone.substring(old.length(), phone.length());
                obj.setPhoneNumber(phone);
                updateContacts(obj.getId(), phone, obj.getType(), obj.get_ID());
            }
            this.adapter.updateList(this.itemList);
            getAllContacts();
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.updated));
        } else {
            new Const(getApplicationContext()).showNotify(getResources().getString(R.string.can_not_get_your_contacts));
        }
    }

    void conFirmTransfer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.are_you_sure_transfer))
                .setPositiveButton(getResources().getString(R.string.ok), new onHideKeyBoardDialog())
                .setNegativeButton(getResources().getString(R.string.cancel), new onDissmisslDialog())
                .create().show();

    }

    @Override
    public void onClick(View v) {
        if (v == this.btnSearch) {
            this.svIntro.setVisibility(View.GONE);
            actionSearch();
            Const.hideKeyboard(this, this.etOne);
        } else if (v == this.btnTransfer) {
            conFirmTransfer();
        }
    }

    class onHideKeyBoardDialog implements DialogInterface.OnClickListener {
        onHideKeyBoardDialog(){}
        @Override
        public void onClick(DialogInterface dialog, int which) {
            MainActivity.this.actionTransfer();
            Const.hideKeyboard(MainActivity.this, MainActivity.this.etTwo);
        }
    }

    class onCancelDialog implements DialogInterface.OnClickListener {
        onCancelDialog(){}
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    class onDissmisslDialog implements DialogInterface.OnClickListener {
        onDissmisslDialog(){}
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_CONTACTS") == 0) {
            return true;
        }
        return false;
    }

    private void setPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            boolean isGranted = true;
            for (int result : grantResults) {
                if (result != 0) {
                    isGranted = false;
                    break;
                }
            }
            if (isGranted) {
                startApplication();
            } else {
                finish();
            }
        }
    }
}
