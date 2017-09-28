package ru.sonic.zabbix.pro.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.ZControl.ZControlActivity;
import ru.sonic.zabbix.pro.adapters.DefaultAdapter;
import ru.sonic.zabbix.pro.adapters.SliderAdapter;
import ru.sonic.zabbix.pro.config.ConfigurationActivity;
import ru.sonic.zabbix.pro.database.DBAdapter;

/**
 * base listview with common code to retrieve a list of zabbix objects and display them
 * @author dassinion
 *
 */
public class NavigationDriverZabbixActivity extends AppCompatActivity {

    //protected static final int HEADER_HEIGHT_DP = 62;

    //protected View mHeaderContainer = null;
    //protected View mHeaderView = null;
    //protected ImageView mArrow = null;
    //protected ProgressBar mProgress = null;
    //protected float mY = 0;
    //protected float mHistoricalY = 0;
    //protected int mHistoricalTop = 0;
    //protected int mInitialHeight = 0;
    //protected boolean mFlag = false;
    //protected boolean mArrowUp = false;
    //protected TextView mText = null;
    //protected int mHeaderHeight = 0;


    //protected CharSequence mTitle;


    //ListView myList;

    //public Handler handler;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list);
        //isDebug = true;
        //mTitle = getTitle();
        //initDrawerSlider();
    }


/*
    public String getPrefCurrentServer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String srvname = prefs.getString("currentServer",getResources().getString(R.string.not_selected));
        return srvname;
    }

    public boolean is_support_multiserver(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("multiserver_support",false);
    }
*/
    /*
    @SuppressWarnings("deprecation")
    public void setHeaderHeight(final int height) {
        if (height <= 1) {
            mHeaderView.setVisibility(View.GONE);
        } else {
            mHeaderView.setVisibility(View.VISIBLE);
        }

        // Extends refresh bar
        AbsListView.LayoutParams lp = (AbsListView.LayoutParams) mHeaderContainer.getLayoutParams();
        if (lp == null) {
            lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);
        }
        lp.height = height;
        mHeaderContainer.setLayoutParams(lp);

        // Refresh bar shows up from bottom to top
        LinearLayout.LayoutParams headerLp = (LinearLayout.LayoutParams) mHeaderView
                .getLayoutParams();
        if (headerLp == null) {
            headerLp = new LinearLayout.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);
        }
        headerLp.topMargin = -mHeaderHeight + height;
        mHeaderView.setLayoutParams(headerLp);

        if (!mIsPullRefreshing) {
            // If scroll reaches the trigger line, start refreshing
            if (height > mHeaderHeight && !mArrowUp) {
                mArrow.startAnimation(AnimationUtils.loadAnimation(
                        getBaseContext(), R.anim.rotate));
                mText.setText("Release to update");
                rotateArrow();
                mArrowUp = true;
            } else if (height < mHeaderHeight && mArrowUp) {
                mArrow.startAnimation(AnimationUtils.loadAnimation(
                        getBaseContext(), R.anim.rotate));
                mText.setText("Pull down to update");
                rotateArrow();
                mArrowUp = false;
            }
        }
    }*/
    /*
    private void rotateArrow() {
        Drawable drawable = mArrow.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.rotate(180.0f, canvas.getWidth() / 2.0f,
                canvas.getHeight() / 2.0f);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        canvas.restore();
        mArrow.setImageBitmap(bitmap);
    }*/
/*
    protected void initialize_pull_to_refresh() {
        if (isDebug) Log.d(TAG,"initialize_pull_to_refresh ");
        /*Swipe refresh*/
/*        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        /*
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderContainer = inflater.inflate(R.layout.refreshable_list_header,
                null);
        mHeaderView = mHeaderContainer
                .findViewById(R.id.refreshable_list_header);
        mArrow = (ImageView) mHeaderContainer
                .findViewById(R.id.refreshable_list_arrow);
        mProgress = (ProgressBar) mHeaderContainer
                .findViewById(R.id.refreshable_list_progress);
        mText = (TextView) mHeaderContainer
                .findViewById(R.id.refreshable_list_text);
        myList.addHeaderView(mHeaderContainer, null, false);

        mHeaderHeight = (int) (HEADER_HEIGHT_DP * getBaseContext()
                .getResources().getDisplayMetrics().density);
        setHeaderHeight(0);
        */
    //}
    /*
    */

    /*
    public void refreshData() {
        // empty method
    }
*/

}