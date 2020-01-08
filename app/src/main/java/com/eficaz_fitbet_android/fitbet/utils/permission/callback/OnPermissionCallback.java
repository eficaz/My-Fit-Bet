package com.eficaz_fitbet_android.fitbet.utils.permission.callback;

import androidx.annotation.NonNull;

public interface OnPermissionCallback {

    /**
     * it will call when all permission granted!
     *
     * @param permissionName
     */
    void onPermissionGranted(@NonNull String[] permissionName);

    void onPermissionDeclined(@NonNull String[] permissionName);

    void onPermissionPreGranted(@NonNull String permissionsName);

    /**
     * it call when to click deny..after that is say reason in popup
     *
     * @param permissionName
     */
    void onPermissionNeedExplanation(@NonNull String[] permissionName);

    /**
     * it will call when you check never ask again
     *
     * @param permissionName
     */
    void onPermissionReallyDeclined(@NonNull String permissionName);

    void onNoPermissionNeeded();
}
