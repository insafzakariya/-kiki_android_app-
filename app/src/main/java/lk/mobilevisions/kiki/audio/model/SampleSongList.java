package lk.mobilevisions.kiki.audio.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import lk.mobilevisions.kiki.R;

public class SampleSongList  {

    private static final String STORAGE = "shop";

    public static SampleSongList get() {
        return new SampleSongList();
    }

    private SharedPreferences storage;

    private SampleSongList() {

    }

    public List<SampleDataItem> getData() {
        return Arrays.asList(
                new SampleDataItem(1, "Everyday Candle", "$12.00 USD", R.drawable.ic_havana),
                new SampleDataItem(2, "Small Porcelain Bowl", "$50.00 USD", R.drawable.ic_havana),
                new SampleDataItem(3, "Favourite Board", "$265.00 USD", R.drawable.ic_havana),
                new SampleDataItem(4, "Earthenware Bowl", "$18.00 USD", R.drawable.ic_havana),
                new SampleDataItem(5, "Porcelain Dessert Plate", "$36.00 USD", R.drawable.ic_havana),
                new SampleDataItem(6, "Detailed Rolling Pin", "$145.00 USD", R.drawable.ic_havana));
    }




}
