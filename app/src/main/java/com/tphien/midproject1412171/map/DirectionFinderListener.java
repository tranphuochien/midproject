package com.tphien.midproject1412171.map;

import java.util.List;

/**
 * Created by Tran Phuoc Hien on 1/5/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
