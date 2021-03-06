package clertonleal.com.simpleflickr.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import clertonleal.com.simpleflickr.R;
import clertonleal.com.simpleflickr.adapter.CommentsAdapter;
import clertonleal.com.simpleflickr.entity.Comment;
import clertonleal.com.simpleflickr.entity.PhotoDetails;
import clertonleal.com.simpleflickr.service.FlickrService;
import clertonleal.com.simpleflickr.util.BundleKeys;

public class PhotoActivity extends BaseActivity {

    @InjectView(R.id.image_photo)
    ImageView imagePhoto;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.image_profile)
    ImageView imageProfile;

    @InjectView(R.id.list)
    SuperRecyclerView recyclerView;

    @InjectView(R.id.layout_photo)
    CoordinatorLayout layoutPhoto;

    @Inject
    FlickrService flickrService;

    @Inject
    CommentsAdapter adapter;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_photo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        configureRecycleView();
        showProgress();
        String photoId = getBundle().getString(BundleKeys.PHOTO_ID);

        compositeSubscription.add(flickrService.retrievePhoto(photoId).
                subscribe(photo -> {
                    showPhoto(photo);
                    configureToolbar(photo);
                    setListeners(photo);
                    layoutPhoto.setVisibility(View.VISIBLE);
                }, this::onError));

        compositeSubscription.add(flickrService.retrieveComments(photoId).
                subscribe(this::showComments, this::onError));
    }

    private void showProgress() {
        layoutPhoto.setVisibility(View.GONE);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, R.string.need_internet_to_see_photos, Toast.LENGTH_LONG).show();
        finish();
    }

    private void setListeners(PhotoDetails photo) {
        imagePhoto.setOnClickListener(v -> openZoomPhotoActivity(photo));
    }

    private void openZoomPhotoActivity(PhotoDetails photo) {
        Intent intent = new Intent(this, PhotoZoomActivity.class);
        intent.putExtra(BundleKeys.PHOTO_URL, photo.getLargePhotoUrl());
        startActivity(intent);
    }

    private void configureRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showComments(List<Comment> comments) {
        recyclerView.setAdapter(adapter);
        adapter.addComments(comments);
        layoutPhoto.setVisibility(View.VISIBLE);
    }

    private void showPhoto(PhotoDetails photo) {
        Picasso.with(this).load(photo.getPhotoUrl()).placeholder(R.drawable.photo_holder).into(imagePhoto, callback);
        collapsingToolbarLayout.setTitle(photo.getTitle().getContent());
        Picasso.with(this).load(photo.getOwner().getProfileIconUrl()).placeholder(R.drawable.ic_camera).into(imageProfile);
    }

    private void configureToolbar(PhotoDetails photo) {
        setSupportActionBar(toolbar);
        setTitle(photo.getTitle().getContent());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private final Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            Palette.from(((BitmapDrawable) imagePhoto.getDrawable()).getBitmap()).generate(palette -> {
                Palette.Swatch swatch = getSwatch(palette);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(swatch.getRgb());
                }
            });
        }

        @Override
        public void onError() {}
    };

    private Palette.Swatch getSwatch(Palette palette) {
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) {
            swatch = palette.getLightVibrantSwatch();
        }
        if (swatch == null) {
            swatch = palette.getDarkVibrantSwatch();
        }
        if (swatch == null) {
            swatch = palette.getSwatches().get(0);
        }

        return swatch;
    }

    @Override
    protected void injectMembers() {
        flickrComponent().inject(this);
    }
}
