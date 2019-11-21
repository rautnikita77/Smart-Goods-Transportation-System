package com.deep.customer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.deep.customer.Route;
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

