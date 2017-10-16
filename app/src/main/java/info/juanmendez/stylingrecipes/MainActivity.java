package info.juanmendez.stylingrecipes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    Button dynamicButton;
    Boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dynamicButton = findViewById(R.id.dynamicIconBtn);

        dynamicButton.setOnClickListener(v -> {
            isActive = !isActive;

            if( isActive ){
                setIconInButton( dynamicButton, R.drawable.ic_bike_24dp, getResources().getColor(R.color.colorAccent), 60);

            }else{
                setIconInButton( dynamicButton, R.drawable.ic_bike_24dp, getResources().getColor(R.color.colorPrimaryDark), 60);
            }

            dynamicButton.setSelected(isActive);
        });

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            isActive = !isActive;
            imageButton.setImageBitmap( getBitmap(getResources(), R.drawable.ic_bike_24dp, isActive?R.color.colorAccent:R.color.colorPrimary, 200) );

        });
    }

    private void setIconInButton(Button button, @DrawableRes int drawableId, @ColorInt int color, int squareDimension){
        Drawable drawable;
        Resources resources = button.getResources();
        drawable = resources.getDrawable(drawableId);
        DrawableCompat.setTint(drawable, color );
        drawable.setBounds(0,0,squareDimension,squareDimension);
        button.setCompoundDrawables( drawable, null, null,  null);
    }

    private Bitmap getBitmap(Resources resources, @DrawableRes int drawableId, int colorId, int squareDimension){
        Drawable drawable;
        drawable = resources.getDrawable(drawableId);
        DrawableCompat.setTint(drawable, resources.getColor(colorId) );
        drawable.setBounds(0,0,squareDimension,squareDimension);

        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(squareDimension, squareDimension, Bitmap.Config.ARGB_4444 );

            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);

            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }
}