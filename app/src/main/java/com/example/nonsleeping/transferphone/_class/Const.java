package com.example.nonsleeping.transferphone._class;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.nonsleeping.transferphone._interfaces.IF_GetContacts;
import com.example.nonsleeping.transferphone.dto.ContactInfor;

import java.util.ArrayList;
import java.util.List;

public class Const {
    String TAG = "Const";
    Context context;

    public Const(Context context) {
        this.context = context;
    }

    public void showNotify(String msg) {
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
    }

    public class getAllContacts extends AsyncTask<List<ContactInfor>, String, List<ContactInfor>> {
        IF_GetContacts callback;

        public getAllContacts(IF_GetContacts callback) {
            this.callback = callback;
        }

        @Override
        protected List<ContactInfor> doInBackground(List<ContactInfor>... listArr) {
            Log.d(TAG, "doInBackground");
            return Const.this.getAllContacts(Const.this.context);
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, "onProgressUpdate");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<ContactInfor> contactInfors) {
            super.onPostExecute(contactInfors);
            Log.d(TAG, "onPostExecute");
            if (contactInfors == null) {
                this.callback.onFail();
            } else if (contactInfors.size() > 0) {
                this.callback.onSuccess(contactInfors);
            } else {
                this.callback.onFail();
            }
        }
    }

    public List<ContactInfor> getAllContacts(Context context) {
        Log.d(TAG, "getAllContacts");
        List<ContactInfor> inforList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0 ) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("_id"));
                String name = cur.getString(cur.getColumnIndex("display_name"));

                ContactInfor contactInfor;
                if (Integer.parseInt(cur.getString(cur.getColumnIndex("has_phone_number"))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, "contact_id = ?", new String[]{id}, null);
                    contactInfor = null;

                    while (pCur.moveToNext()) {
                        int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String _ID = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                        switch (phoneType) {
                            case 1:
                                inforList.add(new ContactInfor(id, name, phoneNumber, 1, _ID));
                                break;
                            case 2:
                                inforList.add(new ContactInfor(id, name, phoneNumber, 2, _ID));
                                break;
                            case 3:
                                inforList.add(new ContactInfor(id, name, phoneNumber, 3, _ID));
                                break;
                            case 7:
                                inforList.add(new ContactInfor(id, name, phoneNumber, 7, _ID));
                                break;
                            default:
                                inforList.add(new ContactInfor(id, name, phoneNumber, phoneType, _ID));
                                break;
                        }
                    }
                    pCur.close();
                } else {
                    contactInfor = null;
                }
            }
        }
        return inforList;
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
