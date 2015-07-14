package clertonleal.com.simpleflickr.dagger;


import javax.inject.Singleton;

import clertonleal.com.simpleflickr.activity.MainActivity;
import clertonleal.com.simpleflickr.activity.PhotoActivity;
import clertonleal.com.simpleflickr.adapter.PhotoPagerAdapter;
import clertonleal.com.simpleflickr.fragment.ListPhotosFragment;
import dagger.Component;

@Singleton
@Component(modules = FlickrModule.class)
public interface FlickrComponent {

    void inject(MainActivity mainActivity);
    void inject(PhotoActivity photoActivity);
    void inject(ListPhotosFragment listPhotosFragment);
    void inject(PhotoPagerAdapter photoPagerAdapter);

}
