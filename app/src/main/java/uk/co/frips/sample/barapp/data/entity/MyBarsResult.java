package uk.co.frips.sample.barapp.data.entity;

import java.util.List;

public class MyBarsResult {

    final List<Bar> mMyBars;
    final double mMyLat;
    final double mMyLong;

    public MyBarsResult(List<Bar> myBars, double myLat, double myLong) {
        mMyBars = myBars;
        mMyLat = myLat;
        mMyLong = myLong;
    }

    public List<Bar> getMyBars() {
        return mMyBars;
    }

    public double getMyLat() {
        return mMyLat;
    }

    public double getMyLong() {
        return mMyLong;
    }


}
