package clertonleal.com.simpleflickr.util;

import clertonleal.com.simpleflickr.R;

public enum PhotoType {

    POPULARS(R.string.populars),
    NEWS(R.string.news);

    private int resource;

    PhotoType(int resource) {
        this.resource = resource;
    }

    public int getResource() {
        return resource;
    }

}
