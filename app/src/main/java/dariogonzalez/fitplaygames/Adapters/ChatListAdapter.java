package dariogonzalez.fitplaygames.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import dariogonzalez.fitplaygames.Chat;
import dariogonzalez.fitplaygames.R;

/**
 * Created by ChristensenKC on 10/28/2015.
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;

    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername) {
        super(ref, Chat.class, layout, activity);
        this.mUsername = mUsername;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview
        String author = chat.getAuthor();
        LinearLayout layoutMe = (LinearLayout)view.findViewById(R.id.layout_me);
        LinearLayout layoutYou = (LinearLayout)view.findViewById(R.id.layout_you);

        if (author != null && author.equals(mUsername)) {
            layoutMe.setVisibility(View.VISIBLE);
            layoutYou.setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.message)).setText(chat.getMessage());
            TextView authorText = (TextView) view.findViewById(R.id.author);
            authorText.setText(author);
//            authorText.setTextColor(Color.RED);
        } else {
            layoutMe.setVisibility(View.GONE);
            layoutYou.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.message_you)).setText(chat.getMessage());
            TextView authorText = (TextView) view.findViewById(R.id.author_you);
            authorText.setText(author);
//            authorText.setTextColor(Color.BLUE);
        }
    }
}
