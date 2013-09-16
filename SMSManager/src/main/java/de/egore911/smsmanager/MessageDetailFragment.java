package de.egore911.smsmanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Message detail screen.
 * This fragment is either contained in a {@link MessageListActivity}
 * in two-pane mode (on tablets) or a {@link MessageDetailActivity}
 * on handsets.
 */
public class MessageDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private SMSData mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor c= getActivity().getContentResolver().query(uri, null, null, null, null);

            mItem = null;

            // Read the sms data and store it in the list
            if(c.moveToFirst()) {
                for(int i=0; i < c.getCount(); i++) {
                    if (getArguments().getLong(ARG_ITEM_ID) == c.getLong(c.getColumnIndex("_id"))) {
                        SMSData sms = new SMSData();
                        sms.setId(c.getLong(c.getColumnIndex("_id")));
                        sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                        sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                        mItem = sms;
                    }

                    c.moveToNext();
                }
            }
            c.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.message_detail)).setText(mItem.getBody());
        }

        return rootView;
    }
}
