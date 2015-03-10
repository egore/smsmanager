/*
 * Copyright (c) 2013 Christoph Brill
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.egore911.smsmanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MessageListFragment} and the item details
 * (if present) is a {@link MessageDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link MessageListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MessageListActivity extends FragmentActivity
        implements MessageListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((MessageListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.message_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        {
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor c = getContentResolver().query(uri, null, null, null, null);

            // Read the sms data and store it in the list
            if (c.moveToFirst()) {

                File inbox = new File(Environment.getExternalStorageDirectory() + "/sms/inbox");
                inbox.mkdirs();
                for (int i = 0; i < c.getCount(); i++) {

                    try {
                        File smsFile = new File(inbox, c.getLong(c.getColumnIndex("_id")) + ".txt");
                        FileWriter smsWriter = new FileWriter(smsFile);
                        BufferedWriter out = new BufferedWriter(smsWriter);
                        out.write("From: ");
                        out.write(c.getString(c.getColumnIndexOrThrow("address")));
                        out.write("\n");
                        out.write("Date: ");
                        out.write(format.format(new Date(c.getLong(c.getColumnIndexOrThrow("date")))));
                        out.write("\n");
                        out.write(c.getString(c.getColumnIndexOrThrow("body")));
                        out.close();
                    } catch (IOException e) {
                        Log.e("sms", e.getMessage());
                    }

                    c.moveToNext();
                }
            }
            c.close();
        }

        {
            Uri uri = Uri.parse("content://sms/sent");
            Cursor c = getContentResolver().query(uri, null, null, null, null);

            // Read the sms data and store it in the list
            if (c.moveToFirst()) {

                File inbox = new File(Environment.getExternalStorageDirectory() + "/sms/sent");
                inbox.mkdirs();
                for (int i = 0; i < c.getCount(); i++) {

                    try {
                        File smsFile = new File(inbox, c.getLong(c.getColumnIndex("_id")) + ".txt");
                        FileWriter smsWriter = new FileWriter(smsFile);
                        BufferedWriter out = new BufferedWriter(smsWriter);
                        out.write("To: ");
                        out.write(c.getString(c.getColumnIndexOrThrow("address")));
                        out.write("\n");
                        out.write("Date: ");
                        out.write(format.format(new Date(c.getLong(c.getColumnIndexOrThrow("date")))));
                        out.write("\n");
                        out.write(c.getString(c.getColumnIndexOrThrow("body")));
                        out.close();
                    } catch (IOException e) {
                        Log.e("sms", e.getMessage());
                    }

                    c.moveToNext();
                }
            }
            c.close();
        }
    }

    /**
     * Callback method from {@link MessageListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Long id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(MessageDetailFragment.ARG_ITEM_ID, id);
            MessageDetailFragment fragment = new MessageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.message_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MessageDetailActivity.class);
            detailIntent.putExtra(MessageDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
