/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.eficaz_fitbet_android.fitbet.interfaces;

import android.location.Location;

/**
 * Created by User on 3/30/2018.
 */

public interface LocationUpdateCallback {
    void newLocation(Location location);

    void myCurrentLocation(Location currentLocation);
}
