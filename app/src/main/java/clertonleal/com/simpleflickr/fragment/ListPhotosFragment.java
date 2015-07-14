package clertonleal.com.simpleflickr.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import javax.inject.Inject;

import butterknife.InjectView;
import clertonleal.com.simpleflickr.R;
import clertonleal.com.simpleflickr.activity.PhotoActivity;
import clertonleal.com.simpleflickr.adapter.PhotoAdapter;
import clertonleal.com.simpleflickr.entity.Photo;
import clertonleal.com.simpleflickr.service.FlickrService;
import clertonleal.com.simpleflickr.util.BundleKeys;
import clertonleal.com.simpleflickr.util.Flickr;
import clertonleal.com.simpleflickr.util.PhotoType;

public class ListPhotosFragment extends BaseFragment {

    @InjectView(R.id.list)
    SuperRecyclerView recyclerView;

    @Inject
    FlickrService flickrService;

    @Inject
    PhotoAdapter photoAdapter;

    @Inject
    Resources resources;

    LinearLayoutManager linearLayoutManager;

    int actualPage = 1;

    private PhotoType photoType;

    public static ListPhotosFragment newInstance(PhotoType photoType) {
        ListPhotosFragment listPhotosFragment = new ListPhotosFragment();
        listPhotosFragment.setPhotoType(photoType);
        return listPhotosFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecycleView();
        setListeners();
        loadInitialPage();
        setContentDescription(view);
    }

    private void setContentDescription(View view) {
        View list = view.findViewById(android.R.id.list);
        if (photoType == PhotoType.POPULARS) {
            list.setContentDescription(resources.getString(R.string.populars));
        } else if (photoType == PhotoType.NEWS) {
            list.setContentDescription(resources.getString(R.string.news));
        }
    }

    private void setListeners() {
        recyclerView.setupMoreListener(this::onLoadMore, Flickr.LOAD_MORE_PHOTOS);
        recyclerView.setRefreshListener(this::loadInitialPage);
        photoAdapter.setOnPhotoClickListener(this::openPhotoDetail);
    }

    private void openPhotoDetail(Photo photo) {
        final Intent intent = new Intent(getActivity(), PhotoActivity.class);
        intent.putExtra(BundleKeys.PHOTO_ID, photo.getId());
        startActivity(intent);
    }

    private void onLoadMore(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        actualPage++;
        loadNewPage(actualPage);
    }

    private void loadInitialPage() {
        actualPage = 1;
        photoAdapter.cleanShots();
        recyclerView.showProgress();
        compositeSubscription.add(flickrService.retrievePhotosByType(actualPage, photoType).
                subscribe(page -> {
                    photoAdapter.addPagePhotos(page);
                    recyclerView.setAdapter(photoAdapter);
                }, this::log));
    }

    private void loadNewPage(int pageNumber) {
        compositeSubscription.add(flickrService.retrievePhotosByType(pageNumber, photoType).
                subscribe(page -> {
                    recyclerView.hideMoreProgress();
                    photoAdapter.addPagePhotos(page);
                }, this::log));
    }

    private void configureRecycleView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setRefreshingColorResources(R.color.primary_color, R.color.primary_color,
                R.color.primary_color, R.color.primary_color);
    }

    @Override
    protected void injectMembers() {
        dribbleComponent().inject(this);
    }

    public int getPageTitle() {
        return this.photoType.getResource();
    }

    public void setPhotoType(PhotoType photoType) {
        this.photoType = photoType;
    }
}
