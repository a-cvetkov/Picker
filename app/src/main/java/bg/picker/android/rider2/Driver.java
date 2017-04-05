package bg.picker.android.rider2;

import java.util.HashMap;

/**
 * Created by bashticata on 3/29/2017.
 */

public class Driver {

    String deviceId;
    LastPosition lastPosition;

    public LastPosition getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(LastPosition lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return this.lastPosition.getY();
    }

    public double getLongitude() {
        return this.lastPosition.getX();
    }
}
