package clertonleal.com.simpleflickr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import clertonleal.com.simpleflickr.application.Application;
import clertonleal.com.simpleflickr.dagger.FlickrComponent;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {

    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        injectMembers();
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        unsubscribeSubscriptions();
        super.onDestroyView();
    }

    protected void unsubscribeSubscriptions(){
        compositeSubscription.unsubscribe();
        compositeSubscription = new CompositeSubscription();
    }

    protected FlickrComponent dribbleComponent() {
        return Application.getFlickrComponent();
    }

    protected void log(Throwable e){
        Log.e(getTag(), "", e);
    }

    protected void setOnClickListeners(){}

    protected abstract void injectMembers();

}
