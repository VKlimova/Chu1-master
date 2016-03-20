package com.example.vklimova.chu1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent.PointerProperties;
import android.view.InputEvent;
import android.view.MotionEvent;
//import com.example.vklimova.chu1.R;



public class MainActivity extends Activity {

    Button btnLoadImage1, btnLoadImage2, btnPlusH, btnMinusH, btnPlusW, btnMinusW, btnLeft, btnRight, btnUp, btnDown;
            ImageButton btnSave;
    TextView textSource1, textSource2, textView;
    Button btnProcessing, btnR;
    ImageView imageResult, imageDress1, imageDress2, imageDog1;

    final int RQS_IMAGE1 = 1;
    final int RQS_IMAGE2 = 2;
    private static final int INVALID_POINTER_ID = -1;
    Uri source1, source2;

    int curX = 0, curY = 0, curW = 0, curH = 0, // дельты положения и размера платья
            targetW=800, targetH=600, // размер всей картинки с собакой
            StartDressW=400, StartDressH=300,curDressW=400, curDressH=300, prevDressH=0, prevDressW=0, //Начальный и текущий размер платья
            Rot0=0, lastRot=0, deltaRot=0, curRot=0, prvRot, prvRotTouchX=0, prvRotTouchY=0, prvRotTouchX1=0, prvRotTouchY1=0,  // Поворот
            prvTouchX = 0, prvTouchY=0, TouchX=0, TouchY=0,             // точка прикосновения пальца
            prvTouchX1 = 0, prvTouchY1=0, TouchX1=0, TouchY1=0, deltaX, deltaY;         // вторая точка прикосновения пальцем
    int prevFocusX, prevFocusY;
    int prevPointerCount=0;
    Bitmap ScaledDogBmp, ScaledDressBmp;
    Config config = Bitmap.Config.ARGB_8888;
    ScaleGestureDetector mScaleDetector;
 //   Matrix Mtrx= new Matrix();
float mLastTouchX, mLastTouchY, mPosX, mPosY;
    Bitmap newBitmap = Bitmap.createBitmap(targetW, targetH, config);
    Bitmap bm1 = Bitmap.createBitmap(targetW,targetH,config);
    Bitmap bm2 = Bitmap.createBitmap(StartDressW,StartDressH,config);
    Canvas newCanvas = new Canvas(newBitmap);
    Paint paint = new Paint();
    BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
    private float mScaleFactor = 1.f;

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float lastSpanX, lastSpanY;
        private float lastfocusX, lastfocusY ;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastSpanX = detector.getCurrentSpanX();
            lastSpanY = detector.getCurrentSpanY();
            prevDressH = curH;
            prevDressW = curW;
            lastfocusX = detector.getFocusX();
            lastfocusY = detector.getFocusY();
            prevFocusX = curX;
            prevFocusY = curY;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float spanX = detector.getCurrentSpanX();
            float spanY = detector.getCurrentSpanY();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            int deltaX,deltaY;

           if (MoveDressW((int) (spanX-lastSpanX)+prevDressW)) {
               deltaX=curW;
               curW = (int) (spanX-lastSpanX)+prevDressW ;
               deltaX = deltaX-curW;
     //          curX =  curX+(deltaX/2);
               }
           if (MoveDressH((int) (spanY-lastSpanY)+prevDressH)) {
               deltaY = curH;
               curH = (int) (spanY-lastSpanY)+prevDressH;
               deltaY=deltaY-curH;
     //          curY = curY+(deltaY/2);
                }

        curX = (int) (focusX-lastfocusX) +prevFocusX;
        curY = (int) (focusY-lastfocusY) +prevFocusY;

//            {    textView.setText("CurH="+curH+" curW="+curW); }

            doNewBitmap();
            imageResult.setImageBitmap(newBitmap);
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        btnLoadImage1 = (Button) findViewById(R.id.loadimage1);
        btnLoadImage2 = (Button) findViewById(R.id.loadimage2);
        textSource1 = (TextView) findViewById(R.id.sourceuri1);
        textSource2 = (TextView) findViewById(R.id.sourceuri2);
        textView = (TextView) findViewById(R.id.textView);
        btnR= (Button) findViewById(R.id.btnR);
        btnProcessing = (Button) findViewById(R.id.processing);
        btnSave=(ImageButton) findViewById(R.id.saveimage);
        btnPlusH=(Button) findViewById(R.id.plusH);
        btnMinusH=(Button) findViewById(R.id.minusH);
        btnPlusW=(Button) findViewById(R.id.plusW);
        btnMinusW=(Button) findViewById(R.id.minusW);
        btnLeft=(Button) findViewById(R.id.left);
        btnRight=(Button) findViewById(R.id.right);
        btnUp=(Button) findViewById(R.id.up);
        btnDown=(Button) findViewById(R.id.down);
        imageResult = (ImageView) findViewById(R.id.result);
        imageDress1 = (ImageView) findViewById(R.id.dress1);
        imageDress2 = (ImageView) findViewById(R.id.dress2);
        imageDog1 = (ImageView) findViewById(R.id.dog1);
        bmpOptions.inMutable=true;

        // The ‘active pointer’ is the one currently moving our object.


        bm1=decodeSampledBitmapFromResource(getResources(), R.drawable.chu_small, targetW, targetH);

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap2(newBitmap);
            }
        });

        mScaleDetector = new ScaleGestureDetector(this.getApplicationContext(), new ScaleListener());

        imageResult.setOnTouchListener(new View.OnTouchListener() {
            // The ‘active pointer’ is the one currently moving our object.
            private int mActivePointerId = INVALID_POINTER_ID;
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                // Let the ScaleGestureDetector inspect all events.
                mScaleDetector.onTouchEvent(event);
                int action = event.getActionMasked();
                double  delta_x=0, delta_y=0;
                TouchX = (int) event.getX();
                TouchY = (int) event.getY();
 //     R O T A T I O N
               if (event.getPointerCount()>1)
               {
             /// изменение угла = ATAN2(текущий угол) минус ATAN2(предыдущий угол)

                   delta_x=(TouchX - event.getX(1)); //  по Х
                   delta_y=(TouchY - event.getY(1)); // по У
                   double radians = Math.atan2(delta_y, delta_x);
                   curRot=(int) Math.toDegrees(radians);// текущее положение поворота в градусах

                   deltaRot=(curRot-lastRot);
                   if (Math.abs(deltaRot)<(10)) {
                       Rot0 = Rot0 + deltaRot;//Rot0+deltaRot;
                   }
                   doNewBitmap();
                   imageResult.setImageBitmap(newBitmap);
                   lastRot=curRot;
               }



                if ((event.getPointerCount()==1)&&(prevPointerCount==2))
                    {
//                        textView.setText("ACTION touch TouchX=" + TouchX + " prvTouchX=" + prvTouchX + " prevPointerCount=" + prevPointerCount + " curPointerCount=" + event.getPointerCount());
                        prvTouchX = TouchX;
                        prvTouchY = TouchY;
                    }

                prevPointerCount=event.getPointerCount();

                if (event.getPointerCount()==1) {

                    switch (action) {
                        case MotionEvent.ACTION_DOWN: {
                            final int pointerIndex = MotionEventCompat.getActionIndex(event);
                            prvTouchX = TouchX;
                            prvTouchY = TouchY;
                            mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                        }
                        break;
                        case MotionEvent.ACTION_MOVE: {
                            final int pointerIndex =
                                    MotionEventCompat.findPointerIndex(event, mActivePointerId);
                            //
                            if (MoveDressX(TouchX - prvTouchX)) {
                                curX = curX + TouchX - prvTouchX;
                                prvTouchX = TouchX;
                            }
                            ;
                            if (MoveDressY(TouchY - prvTouchY)) {
                                curY = curY + TouchY - prvTouchY;
                                prvTouchY = TouchY;
                            }
                        }
                        ;

                        doNewBitmap();
                        imageResult.invalidate();
//                        textView.setText("ACTION Move TouchX=" + TouchX + " prvTouchX=" + prvTouchX + " prevPointerCount=" + prevPointerCount + " curPointerCount=" + event.getPointerCount());
                        break;
                        case MotionEvent.ACTION_UP: {

                            mActivePointerId = INVALID_POINTER_ID;

  //                          textView.setText("ACTION UP TouchX=" + TouchX + " prvTouchX=" + prvTouchX + " mActivePointerId=" + mActivePointerId);
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {

                            mActivePointerId = INVALID_POINTER_ID;
    //                        textView.setText("ACTION Cancel TouchX=" + TouchX + " prvTouchX=" + prvTouchX + " mActivePointerId=" + mActivePointerId);
                            break;
                        }

                        case MotionEvent.ACTION_POINTER_UP: {

                            final int pointerIndex = MotionEventCompat.getActionIndex(event);
                            final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                            if (pointerId == mActivePointerId) {
                                // This was our active pointer going up. Choose a new
                                // active pointer and adjust accordingly.
                                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                prvTouchX = (int) MotionEventCompat.getX(event, newPointerIndex);
                                prvTouchY = (int) MotionEventCompat.getY(event, newPointerIndex);
                                mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                            }
      //                      textView.setText("ACTION POINTER UP TouchX=" + TouchX + " prvTouchX=" + prvTouchX + " mActivePointerId=" + mActivePointerId);
                            break;
                        }
                    }

                } // if event.getPointerCount() ==1;
                return true;
            }
        });


;
            imageDog1.setOnClickListener(new

            OnClickListener() {
                @Override
                public void onClick (View arg0){
                    bm1 = decodeSampledBitmapFromResource(getResources(), R.drawable.chu_small, targetW, targetH);
                doNewBitmap();
                imageResult.setImageBitmap(newBitmap);
            }
        });

        btnR.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Rot0=Rot0+45;
                                            doNewBitmap();
                                            imageResult.setImageBitmap(newBitmap);
                                        }
                                    }
        );

        imageDress1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bm2=decodeSampledBitmapFromResource(getResources(), R.drawable.green_small, StartDressW , StartDressH);
                doNewBitmap();
                imageResult.setImageBitmap(newBitmap);

            }
        });

        imageDress2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bm2 = decodeSampledBitmapFromResource(getResources(),R.drawable.pink_small, StartDressW, StartDressH);
                doNewBitmap();
                imageResult.setImageBitmap(newBitmap);
            }
        });


        btnRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
              if (MoveDressX(25)) {
                  curX = curX + 25;
                  doNewBitmap();
                  imageResult.setImageBitmap(newBitmap);
              }
            }
        });

        btnLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if ( MoveDressX(-25)) {
                    curX = curX - 25;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }

            }
        });

        btnUp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
               if (MoveDressY(-25)){
                   curY = curY - 25;
                   doNewBitmap();
                   imageResult.setImageBitmap(newBitmap);
               }
            }
        });

        btnDown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (MoveDressY(25)){
                    curY = curY + 25;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }
            }
        });

        btnPlusH.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (MoveDressH(25)) {
                    curH = curH + 25;
                    curY = curY - 12;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }
            }
        });

        btnMinusH.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (MoveDressH(-25)) {
                    curH = curH - 25;
                    curY = curY + 12;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }
            }
        });

        btnPlusW.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (MoveDressW(25)) {
                    curW = curW + 25;
                    curX = curX - 12;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }
            }
        });

        btnMinusW.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (MoveDressW(-25)) {
                    curW = curW - 25;
                    curX = curX +12;
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                }
            }
        });

        btnLoadImage1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });

        btnLoadImage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE2);
            }
        });

        btnProcessing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:
                    source1 = data.getData();
                    bm1 = decodeSampledBitmapFromFile(source1, targetW, targetH);
 //                   curDressW = bm1.getWidth();
 //                   curDressH = bm1.getHeight();
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                    break;
                case RQS_IMAGE2:
                    source2 = data.getData();
                    bm2 = decodeSampledBitmapFromFile(source2, targetW, targetH);
   //                 curDressW = bm2.getWidth();
   //                 curDressH = bm2.getHeight();
                    doNewBitmap();
                    imageResult.setImageBitmap(newBitmap);
                    break;
            }
        }
    }
  /*
    Project position on ImageView to position on Bitmap draw on it
     */

    private void MoveDress(View v, float x0, float y0, float x, float y){
        if(x<0 || y<0 || x > v.getWidth() || y > v.getHeight()){
            //outside ImageView
            return;
        }else{

            float ratioWidth = (float)v.getWidth()/(float) v.getWidth();
            float ratioHeight = (float)v.getHeight()/(float) v.getHeight();

            newCanvas.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paint);
            imageResult.invalidate();
        }
    }

    private void doNewBitmap() {
// Do newBitmap = bm2 over bm1 at curX, curY with curW, curH.
int W1, H1, X1, Y1;
   //     textView.setText("doNewBitmap");
        if (bm1 != null) {
            paint.setColor(Color.LTGRAY);
            newCanvas.drawRect(0, 0, targetW, targetH, paint);
        if ((bm1.getWidth()/targetW)>(bm1.getHeight()/targetH))
            { W1=targetW; H1=W1*bm1.getHeight()/bm1.getWidth(); }
        else
            { H1=targetH; W1=H1*bm1.getWidth()/bm1.getHeight(); }

            ScaledDogBmp = Bitmap.createScaledBitmap(bm1, W1, H1, true);

            X1=(targetW-W1)/2; Y1=(targetH-H1)/2;
            newCanvas.drawBitmap(ScaledDogBmp, X1, Y1, paint);

            if (bm2 != null) {
                curDressW = bm2.getWidth() + curW;
                curDressH = bm2.getHeight() + curH;
                ScaledDressBmp = Bitmap.createScaledBitmap(bm2, curDressW, curDressH, true);
//                RotateBitmap(ScaledDressBmp, Rot0);
//                newCanvas.drawBitmap(ScaledDressBmp, curX, curY, paint);

                newCanvas.drawBitmap(RotateBitmap(ScaledDressBmp, Rot0), curX, curY, paint);
    //            textView.setText("Rot0="+Rot0);
            }
        } else
        {    Toast.makeText(getApplicationContext(), "Выберите собаку и платье", Toast.LENGTH_LONG).show();}
//       textView.setText("curW=" + curW + " curH=" + curH + " curDressW=" + curDressW +" curDressH="+curDressH +" targetH="+targetH+" targetW="+targetW);

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public  Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = 2*calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public  Bitmap decodeSampledBitmapFromFile(Uri filename, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // try to open file
        try {
            BitmapFactory.decodeStream( getContentResolver().openInputStream(filename), null , options);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Calculate inSampleSize
        options.inSampleSize = 2*calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(filename), null , options);
        } catch (FileNotFoundException e) { e.printStackTrace();
            return null;}

    }



    private boolean MoveDressX(int X) {
        //return true  if new X is in screen scope
        if (
                (curX+X)>(curDressW/-2) &&      //левый край
                ((curX+X)<(targetW-(curDressW/2))) // правый край
                ) return(true);
    return false;
    }

    private boolean MoveDressY(int Y) {
        //return true if new Y is in screen scope
        if (((curY+Y)>(curDressH/-2))&&
                ((curY+Y)<(targetH-(curDressH/2)))
                ) return(true);
        return false;
    }

    private boolean MoveDressW(int W) {
        //return true if new W is in screen scope
        if ((Math.abs(curDressW+W)<(targetW*1.5)) && //новый размер не больше чем 2 размера всей картинки
                (curDressW+W)>50)  // новый размер не меньше 50
                 return (true);
        return false;
    }

    private boolean MoveDressH(int H) {
        //return true if new H is in screen scope
        if ((Math.abs(curDressH+H)<(targetH*1.5))
                && ((curDressH+H)>50)
                ) return(true);
        return false;
    }

    private void saveBitmap2(Bitmap bm) {
        String imgName;
        OutputStream fOut = null;
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator);
        imgName = "dog" + android.text.format.DateFormat.format("dd_MM_kk_mm_ss", new java.util.Date()).toString() + ".jpg";
        File f = new File(filepath, imgName);

        try {
            fOut = new FileOutputStream(f);

            /**Compress image**/
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            /**Update image to gallery**/
            MediaStore.Images.Media.insertImage(this.getContentResolver(), f.getAbsolutePath(), f.getName(), f.getName());
//            textView.setText("Save Bitmap: -" + f.getAbsolutePath());
            Toast.makeText(getApplicationContext(), "Ваша стильная картинка сохранена в файл: " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
        //Scan to add image to Phone's Gallery
            MediaScannerConnection.scanFile(this,
                    new String[]{f.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                            textView.setText("Scanned " + path + ":-> uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}