package dariogonzalez.fitplaygames.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dariogonzalez.fitplaygames.R;

/**
 * Created by Logan on 9/26/2015.
 */
public class FullImageDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Context context;
    private ImageView image;
    private Uri imageUri;

    public FullImageDialog(Context context, Uri imageUri) {
        super(context);
        this.context = context;
        this.imageUri = imageUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_full_image);
        Button closeImageButton = (Button) findViewById(R.id.dialog_close_button);
        closeImageButton.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.dialog_image);


        closeImageButton.setVisibility(View.VISIBLE);
        setUpImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close_button:
                dismiss();
            default:
                break;
        }
    }

    public void setUpImage() {
        Picasso.with(context).load(imageUri.toString()).fit().centerInside().into(image);
        image.setVisibility(View.VISIBLE);
    }
}
