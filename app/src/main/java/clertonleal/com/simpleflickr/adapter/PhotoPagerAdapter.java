package clertonleal.com.simpleflickr.adapter;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import clertonleal.com.simpleflickr.application.Application;
import clertonleal.com.simpleflickr.fragment.ListPhotosFragment;
import clertonleal.com.simpleflickr.util.PhotoType;

public class PhotoPagerAdapter extends FragmentPagerAdapter {

    @Inject
    Resources resources;

    private List<ListPhotosFragment> listPhotosFragments = new ArrayList<>();

    public PhotoPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        Application.getFlickrComponent().inject(this);
        listPhotosFragments.add(ListPhotosFragment.newInstance(PhotoType.POPULARS));
        listPhotosFragments.add(ListPhotosFragment.newInstance(PhotoType.NEWS));
    }

    @Override
    public Fragment getItem(int position) {
        return listPhotosFragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return resources.getString(listPhotosFragments.get(position).getPageTitle());
    }
}
