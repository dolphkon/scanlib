package com.dolphkon.scanlib.open;

import android.hardware.Camera;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib.open
 * ClassName: OpenCamera
 * Author: kongdexi
 * Date: 2020/9/1 11:58
 * Description:TODO
 * *****************************************************
 */
public class OpenCamera {

    private final int index;
    private final Camera camera;
    private final CameraFacing facing;
    private final int orientation;

    public OpenCamera(int index, Camera camera, CameraFacing facing, int orientation) {

        this.index = index;
        this.camera = camera;
        this.facing = facing;
        this.orientation = orientation;
    }

    public Camera getCamera() {
        return camera;
    }

    public CameraFacing getFacing() {
        return facing;
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "Camera #" + index + " : " + facing + ',' + orientation;
    }

}
