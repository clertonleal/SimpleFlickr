package clertonleal.com.simpleflickr.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.InjectView;
import clertonleal.com.simpleflickr.R;
import clertonleal.com.simpleflickr.adapter.PhotoPagerAdapter;
import clertonleal.com.simpleflickr.service.ConnectionService;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.layout_empty_view)
    LinearLayout emptyView;

    @InjectView(R.id.image_refresh)
    ImageView imageRefresh;

    @InjectView(R.id.pager)
    ViewPager viewPager;

    @InjectView(R.id.container)
    CoordinatorLayout coordinatorLayout;

    @Inject
    ConnectionService connectionService;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        setListeners();
        checkInternet();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    private void checkInternet() {
        if (connectionService.hasConnection()) {
            initViewPager();
        } else {
            showEmptyView(true);
        }
    }

    private void initViewPager() {
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(photoPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setListeners() {
        imageRefresh.setOnClickListener(v -> {
            showEmptyView(false);
            checkInternet();
        });
    }

    private void showEmptyView(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            coordinatorLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void injectMembers() {
        flickrComponent().inject(this);
    }
}
