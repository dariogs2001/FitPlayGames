package dariogonzalez.activeandhealthy.classes;

/**
 * Created by Dario on 6/1/2015.
 */
public class MoreOptionsListItem {
    private String mText;
    private int mIconId;

    public String getText() {
        return mText;
    }

    public int getIcontId() {
        return mIconId;
    }

    public MoreOptionsListItem(String text, int iconId)
    {
        mText = text;
        mIconId = iconId;
    }
}
