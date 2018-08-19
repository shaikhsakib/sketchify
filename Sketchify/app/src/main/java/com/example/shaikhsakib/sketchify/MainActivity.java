package com.example.shaikhsakib.sketchify;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.database.Cursor;
import android.graphics.Bitmap;
import Catalano.Imaging.FastBitmap;


import java.nio.IntBuffer;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    Button b1;
    ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnSelectImage);


        b1 = (Button) findViewById(R.id.sketchify);
        im = (ImageView) findViewById(R.id.imgView);

    }

    public void sketchit(View view){
        Bitmap bm = ((BitmapDrawable) im.getDrawable()).getBitmap();

        Bitmap bitmap = bm.copy(bm.getConfig(), true);
        FastBitmap fb = new FastBitmap(bitmap);


        Bitmap result = Changetosketch(bitmap);
        im.setImageBitmap(result);
    }


    public Bitmap Changetosketch(Bitmap bmp){
        Bitmap Copy,Invert,Result;
        Copy =bmp;
        Copy = toGrayscale(Copy);
        Invert = createInvertedBitmap(Copy);
        Invert = Blur.blur(MainActivity.this, Invert);
        Result = ColorDodgeBlend(Invert, Copy);

        return Result;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap createInvertedBitmap(Bitmap src) {
        ColorMatrix colorMatrix_Inverted =
                new ColorMatrix(new float[] {
                        -1,  0,  0,  0, 255,
                        0, -1,  0,  0, 255,
                        0,  0, -1,  0, 255,
                        0,  0,  0,  1,   0});

        ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                colorMatrix_Inverted);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(ColorFilter_Sepia);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }
    public Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);


            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);


            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }

    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }

    /* Choose an image from Gallery */
    public void openImageChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    ((ImageView) findViewById(R.id.imgView)).setImageURI(selectedImageUri);
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


}
