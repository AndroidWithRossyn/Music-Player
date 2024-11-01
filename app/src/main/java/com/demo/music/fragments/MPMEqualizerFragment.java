package com.demo.music.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.db.chart.view.LineChartView;

import com.demo.music.R;
import com.sdsmdg.harjot.crollerTest.Croller;

import com.demo.music.adapter.MPMEffect_Adapter;
import com.demo.music.model.MPMEqualizerModel;
import com.demo.music.service.MPMTestWidgetTest;
import com.demo.music.utils.StorageUtil;


public class MPMEqualizerFragment extends Fragment {
    public static final int AUDIO_PERMISSION_REQUEST_CODE = 102;
    public static Croller bassController;
    public static Switch equalizerSwitch;
    public static LinearLayout linearLayout;
    public static Context mContext;
    static NotificationManager notificationManager;
    static RecyclerView rec_effects;
    public static Croller reverbController;
    private Activity activity;
    private int audioSesionId;
    private ImageView back;
    ImageView backBtn;
    LineChartView chart;
    Context context;
    Context ctx;
    MPMEffect_Adapter effect_adapter;
    LinearLayout mLinearLayout;
    protected MediaPlayer mediaPlayer;
    short numberOfFrequencyBands;
    Paint paint;
    StorageUtil storage;
    MPMEqualizerModel storedModel;
    private ActionBarDrawerToggle t;
    Bitmap thumbCurrentVideoPosition;
    TextView title;
    Toolbar toolbar;
    Visualizer visualizer;
    Croller volumeController;
    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {"android.permission.RECORD_AUDIO"};
    public static int themeColor = Color.parseColor("#ce1742");
    int notifier_counter = 0;
    int indix = 1;
    private SeekBar volumeSeekbar = null;
    Handler handler = new Handler();

    protected void init() {
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int i, boolean z, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = AppCompatResources.getDrawable(context, i);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            if ((drawable instanceof VectorDrawableCompat) || (drawable instanceof VectorDrawable)) {
                Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                if (z) {
                    drawable.setBounds(10, 10, canvas.getWidth() - 10, canvas.getHeight() - 10);
                } else {
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                }
                drawable.draw(canvas);
                return createBitmap;
            }
            throw new IllegalArgumentException("unsupported drawable type");
        } else if (i2 == 1) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.done);
        } else {
            if (i2 == 2) {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.done);
            }
            if (i2 == 3) {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.done);
            }
            return null;
        }
    }

    public static void updateVolume(int i) {
        Log.e("Ay_gandu", "" + i);
        MPMTestWidgetTest.audioManager.setStreamVolume(3, i, 0);
    }

    public static void onSwitchOperation(boolean z) {
        Log.e("equalizerSwitch", "" + z);
        equalizerSwitch.setChecked(z);
        MPMTestWidgetTest.equalizerModel.setEqualizerEnabled(z);
        MPMTestWidgetTest.mEqualizer.setEnabled(z);
        MPMTestWidgetTest.bassBoost.setEnabled(z);
        MPMTestWidgetTest.presetReverb.setEnabled(z);
        MPMTestWidgetTest.isEqualizerEnabled = z;
    }

    @Override 
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mContext = getContext();
        this.storage = new StorageUtil(mContext);
        this.storedModel = this.storage.loadEqualizer();
        try {
            Log.e("SessioniB", "0\t" + themeColor);
            MPMTestWidgetTest.bassBoost.setEnabled(true);
            setActionBar();
        } catch (Exception e) {
            Log.e("Except", "" + e.toString());
        }
        Log.e("Activity", "onCreate");
        SharedPreferences.Editor edit = mContext.getSharedPreferences("MyPref", 0).edit();
        edit.putBoolean("isClicked", false);
        edit.putBoolean("isDestroy", false);
        edit.commit();
        edit.apply();
    }

    @Override 
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Activity", "onAttach");
        this.ctx = context;
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.t.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override 
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.e("Activity", "onCreateView");
        View inflate = layoutInflater.inflate(R.layout.fragment_equalizer1, viewGroup, false);
        this.toolbar = (Toolbar) inflate.findViewById(R.id.toolbar);
        rec_effects = (RecyclerView) inflate.findViewById(R.id.rec_effects);
        linearLayout = (LinearLayout) inflate.findViewById(R.id.content);
        equalizerSwitch = (Switch) inflate.findViewById(R.id.equalizer_switch);
        bassController = (Croller) inflate.findViewById(R.id.controllerBass);
        reverbController = (Croller) inflate.findViewById(R.id.controller3D);
        this.volumeController = (Croller) inflate.findViewById(R.id.volumeController);
        this.mLinearLayout = (LinearLayout) inflate.findViewById(R.id.equalizerContainer);
        this.title = (TextView) inflate.findViewById(R.id.title);
        this.back = (ImageView) inflate.findViewById(R.id.back);

        return inflate;
    }

    @Override 
    @androidx.annotation.RequiresApi(api = 17)
    @android.annotation.SuppressLint({"SetTextI18n", "WrongConstant"})

    public void onViewCreated(@NonNull View r12, Bundle r13) {






    }

    @Override 
    public void onResume() {
        super.onResume();
        Log.e("Activity", "onResume");
        Croller croller = bassController;
        if (croller != null) {
            croller.setProgress(MPMTestWidgetTest.bassPROGRESS);
        }
        Croller croller2 = reverbController;
        if (croller2 != null) {
            croller2.setProgress(MPMTestWidgetTest.reverbPROGRESS);
        }
        if (MPMTestWidgetTest.isEqualizerEnabled) {
            equalizerSwitch.setChecked(true);
        } else {
            equalizerSwitch.setChecked(false);
        }
        Log.e("effectID", MPMTestWidgetTest.effectID + "");
        MPMTestWidgetTest.updateSeekBarsValues(MPMTestWidgetTest.effectID, this.context);
    }

    @Override 
    public void onDestroyView() {
        super.onDestroyView();
        this.storage.storeEqualizer(MPMTestWidgetTest.equalizerModel);
        Log.e("Activity", "onDestroyView");
        Log.e("BROADCAST_RECIVING", "Hello_FragmentDestory");
    }

    @Override 
    public void onDestroy() {
        super.onDestroy();
        Log.e("Activity", "onDestroy");
        Log.e("onDestroy-B", "Hi");
    }

    private void setActionBar() {
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setPlayer() {
        init();
    }

    @Override 
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 102) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                getActivity().finish();
            } else {
                setPlayer();
            }
        }
    }

    @Override 
    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);
        Log.e("Activity", "onActivityCreated");
    }

    @Override 
    public void onStart() {
        super.onStart();
        MPMTestWidgetTest.audioManager = (AudioManager) getActivity().getSystemService("audio");
        Log.e("Activity", "onStart");
    }

    @Override 
    public void onPause() {
        super.onPause();
        this.storage.storeEqualizer(MPMTestWidgetTest.equalizerModel);
        Log.e("Activity", "onPause");
    }

    @Override 
    public void onStop() {
        super.onStop();
        Log.e("Activity", "onStop");
    }

    @Override 
    public void onDetach() {
        super.onDetach();
        Log.e("Activity", "onDetach");
    }
}
