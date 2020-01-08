package com.eficaz_fitbet_android.fitbet.utils.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;
import com.eficaz_fitbet_android.fitbet.utils.permission.callback.IPermissionSuccessResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by NickX on 22-02-2016.
 */
public class PermissionUtils {
    private static final String TAG = "Permission Handler";

    /**
     *
     */
    private static final int READ_PHONE_STATE_REQUEST = 37;
    private static boolean permissionStatuss;
    /**
     * The singleton instance.
     */
    private static PermissionUtils sInstance = new PermissionUtils();
    /**
     * A map to keep track of our outstanding permission requests. The key is the request code sent when we call
     * <p>
     * bundle that holds all of the request information.
     */
    private Map<Integer, RequestData> mCodesToRequests;
    private IPermissionSuccessResponse iPermissionSuccessResponse;
    /**
     * The active activity. Used to make permissions requests. This must be set by the library-user through
     * {@link PermissionUtils#setActivity(FragmentActivity)} or else bad things will happen.
     */
    private WeakReference<FragmentActivity> mActivity;
    /**
     * This is just a value we increment to generate new request codes for use with
     * .
     */
    private int mActiveRequestCode = 1;
    /**
     * Collection of permission
     */
    private String[] collectionOfPermissions;
    private AlertDialog builder;

    private String[] neededPermission;

    private Activity activity;

    // =====================================================================
    // Creation
    // =====================================================================

    /**
     * Implementing a singleton pattern, so this is private.
     */
    private PermissionUtils() {
        mCodesToRequests = new HashMap<>();
    }

    /**
     * @return An instance of {@link PermissionUtils} to help you manage your permissions.
     */
    public static PermissionUtils getInstance() {
        return sInstance;
    }


    // =====================================================================
    // Public
    // =====================================================================

    /**
     * This method should be invoked in the  in every activity that requests
     * permissions. Even if you don't want to use Permiso in your current activity, you should call this method
     * with a null activity to prevent leaking the previously-set activity.
     * <p>
     * <strong>Important: </strong> If your activity subclasses {@link PermissionActivity}, this is already handled for you.
     *
     * @param activity The activity that is currently active.
     */
    public void setActivity(@NonNull FragmentActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * Request one or more permissions from the system. Make sure that you are either subclassing {@link PermissionActivity}
     * or that you have set your current activity using {@link PermissionUtils#setActivity(FragmentActivity)}!
     *
     * @param callback    A callback that will be triggered when the results of your permission request are available.
     * @param permissions A list of permission constants that you are requesting. Use constants from
     *                    {@link android.Manifest.permission}.
     */
    @MainThread
    public void requestPermissions(@NonNull IOnPermissionResult callback, String... permissions) {
        if (!checkActivity())
            return;

        final RequestData requestData = new RequestData(callback, permissions);

        // Mark any permissions that are already granted
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mActivity.get(), permission) == PackageManager.PERMISSION_GRANTED) {
                requestData.resultSet.grantPermissions(permission);
            }
        }

        // If we had all of them, yay! No need to do anything else.
        if (requestData.resultSet.areAllPermissionsGranted()) {
            requestData.onResultListener.onPermissionResult(requestData.resultSet);
        } else {
            // If we have some unsatisfied ones, let's first see if they can be satisfied by an active request. If it
            // can, we'll re-wire the callback of the active request to also trigger this new one.
            boolean linkedToExisting = linkToExistingRequestIfPossible(requestData);

            // If there was no existing request that can satisfy this one, then let's make a new permission request to
            // the system
            if (!linkedToExisting) {
                // Mark the request as active
                final int requestCode = markRequestAsActive(requestData);

                // First check if there's any permissions for which we need to provide a rationale for using
                String[] permissionsThatNeedRationale = requestData.resultSet.getPermissionsThatNeedRationale(mActivity.get());

                // If there are some that need a rationale, show that rationale, then continue with the request
                if (permissionsThatNeedRationale.length > 0) {
                    requestData.onResultListener.onRationaleRequested(new IOnRationaleProvided() {
                        @Override
                        public void onRationaleProvided() {
                            makePermissionRequest(requestCode);
                        }
                    }, permissionsThatNeedRationale);
                } else {
                    makePermissionRequest(requestCode);
                }
            }
        }
    }

    /**
     * This method needs to be called by your activity's {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}.
     * Simply forward the results of that method here.
     * <p>
     * <strong>Important: </strong> If your activity subclasses {@link PermissionActivity}, this is already handled for you.
     *
     * @param requestCode  The request code given to you by {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}.
     * @param permissions  The permissions given to you by {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}.
     * @param grantResults The grant results given to you by {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}.
     */
    @MainThread
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mCodesToRequests.containsKey(requestCode)) {
            RequestData requestData = mCodesToRequests.get(requestCode);
            requestData.resultSet.parsePermissionResults(permissions, grantResults);
            requestData.onResultListener.onPermissionResult(requestData.resultSet);
            mCodesToRequests.remove(requestCode);
        } else {
            Log.w(TAG, "onRequestPermissionResult() was given an unrecognized request code.");
        }
    }

    /**
     * A helper to show your rationale in a {@link android.app.DialogFragment} when implementing
     * {@link IOnRationaleProvided#onRationaleProvided()}. Automatically invokes the rationale callback when the user
     * dismisses the dialog.
     *
     * @param title             The title of the dialog. If null, there will be no title.
     * @param message           The message displayed in the dialog.
     * @param buttonText        The text you want the dismissal button to show. If null, defaults to {@link android.R.string#ok}.
     * @param rationaleCallback The callback to be trigger
     */
    @MainThread
    public void showRationaleInDialog(@Nullable String title, @NonNull String message, @Nullable String buttonText, @NonNull final IOnRationaleProvided rationaleCallback) {
        if (!checkActivity())
            return;

        PermissionDialogFragment dialogFragment = PermissionDialogFragment.newInstance(title, message, buttonText);

        // We show the rationale after the dialog is closed. We use setRetainInstance(true) in the dialog to ensure that
        // it retains the listener after an app rotation.
        dialogFragment.setOnCloseListener(new PermissionDialogFragment.IOnCloseListener() {
            @Override
            public void onClose() {
                rationaleCallback.onRationaleProvided();
            }
        });
        dialogFragment.show(mActivity.get().getSupportFragmentManager(), PermissionDialogFragment.TAG);
    }


    // =====================================================================
    // Private
    // =====================================================================

    /**
     * Checks to see if there are any active requests that are already requesting a superset of the permissions this
     * new request is asking for. If so, this will wire up this new request's callback to be triggered when the
     * existing request is completed and return true. Otherwise, this does nothing and returns false.
     *
     * @param newRequest The new request that is about to be made.
     * @return True if a request was linked, otherwise false.
     */
    private boolean linkToExistingRequestIfPossible(final RequestData newRequest) {
        boolean found = false;

        // Go through all outstanding requests
        for (final RequestData activeRequest : mCodesToRequests.values()) {
            // If we find one that can satisfy all of the new request's permissions, we re-wire the active one's
            // callback to also call this new one's callback
            if (activeRequest.resultSet.containsAllUngrantedPermissions(newRequest.resultSet)) {
                final IOnPermissionResult originalOnResultListener = activeRequest.onResultListener;
                activeRequest.onResultListener = new IOnPermissionResult() {
                    @Override
                    public void onPermissionResult(ResultSet resultSet) {
                        // First, call the active one's callback. It was added before this new one.
                        originalOnResultListener.onPermissionResult(resultSet);

                        // Next, copy over the results to the new one's resultSet
                        String[] unsatisfied = newRequest.resultSet.getUngrantedPermissions();
                        for (String permission : unsatisfied) {
                            newRequest.resultSet.requestResults.put(permission, resultSet.isPermissionGranted(permission));
                        }

                        // Finally, trigger the new one's callback
                        newRequest.onResultListener.onPermissionResult(newRequest.resultSet);
                    }

                    @Override
                    public void onRationaleRequested(IOnRationaleProvided callback, String... permissions) {
                        activeRequest.onResultListener.onRationaleRequested(callback, permissions);
                    }
                };
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Puts the RequestData in the map of requests and gives back the request code.
     *
     * @return The request code generated for this request.
     */
    private int markRequestAsActive(RequestData requestData) {
        int requestCode = mActiveRequestCode++;
        mCodesToRequests.put(requestCode, requestData);
        return requestCode;
    }

    /**
     * Makes the permission request for the request that matches the provided request code.
     *
     * @param requestCode The request code of the request you want to run.
     */
    private void makePermissionRequest(int requestCode) {
        RequestData requestData = mCodesToRequests.get(requestCode);
        ActivityCompat.requestPermissions(mActivity.get(), requestData.resultSet.getUngrantedPermissions(), requestCode);
    }

    /**
     * Ensures that our WeakReference to the Activity is still valid. If it isn't, throw an exception saying that the
     * Activity needs to be set.
     */
    private boolean checkActivity() {
        return mActivity != null;
    }


    // =====================================================================
    // Inner Classes
    // =====================================================================

    /**
     * @param activity
     * @return
     */
    public boolean requestPermissionss(Activity activity) {
        this.activity = activity;
        return hasPermissionToReadNetworkHistory();
    }

    /**
     * @param activity
     * @return
     */
    public boolean requestPermissionss(Activity activity, String[] permissions, IPermissionSuccessResponse iPermissionSuccessResponse) {
        this.activity = activity;
        collectionOfPermissions = permissions;
        this.iPermissionSuccessResponse = iPermissionSuccessResponse;
/*        if (!hasPermissionToReadNetworkHistory()) {
            return false;
        }*/
        if (!hasPermissionToReadPhoneStats(permissions)) {
            askPermissions(collectionOfPermissions);
            permissionStatuss = false;
            return false;
        } else {
            if (!permissionStatuss) {
                permissionStatuss = true;
                this.iPermissionSuccessResponse.onPermissionSuccessListener();
            }
        }
        return true;
    }

    /**
     * @return
     */
    public boolean hasPermissionToReadPhoneStats(String[] permissions) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                flag = true;
        }
        return !flag;
    }

    /**
     * @return
     */
    public boolean chekcPermission(String[] permissions, Activity activity) {
        this.activity = activity;
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                flag = true;
        }
        return !flag;
    }

    public boolean chekcPermission(String[] permissions, Context activity) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                flag = true;
        }
        return !flag;
    }

    /**
     * Ask permission
     */
    @TargetApi(23)
    public void askPermissions(String[] permissions) {
        activity.requestPermissions(permissions, READ_PHONE_STATE_REQUEST);
    }

    /**
     * This method is used to check version and check permission
     */
    public void checVersionAndAskPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //PermissionUtils.getInstance().requestPermissionss(activity,permissions,);
            return;
        }
    }

    private boolean hasPermissionToReadNetworkHistory() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            final AppOpsManager appOps = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), activity.getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        activity.getPackageName(),
                        new AppOpsManager.OnOpChangedListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onOpChanged(String op, String packageName) {
                                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                        android.os.Process.myUid(), activity.getPackageName());
                                if (mode != AppOpsManager.MODE_ALLOWED) {
                                    return;
                                }
                                appOps.stopWatchingMode(this);
                            }
                        });
            }
            requestReadNetworkHistoryAccess();
        } catch (Exception ex) {
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean hasPermissionToReadAccepility(final Activity activity1) {
        try {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            final AppOpsManager appOps = (AppOpsManager) activity1.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), activity.getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                return true;
            }
            appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    activity1.getPackageName(),
                    new AppOpsManager.OnOpChangedListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onOpChanged(String op, String packageName) {
                            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                    android.os.Process.myUid(), activity1.getPackageName());
                            if (mode != AppOpsManager.MODE_ALLOWED) {
                                return;
                            }
                            appOps.stopWatchingMode(this);
                        }
                    });
        } catch (Exception ex) {

    }
        return false;
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        activity.startActivity(intent);
    }

    public AlertDialog getAlertDialog(final String[] permissions, final String permissionName, Activity activity, final PermissionHelper permissionHelper) {
        this.activity = activity;
        checkBuilderInit();
//        if (builder == null) {
        builder = new AlertDialog.Builder(activity)
                .setTitle("Required Permissions")
                .create();
//        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!PermissionUtils.getInstance().hasPermissionToReadPhoneStats(Contents.MULTI_PERMISSIONS))
                    permissionHelper.requestAfterExplanation(permissions);
            }
        });
        builder.setMessage("Give needed Permissions ");
        return builder;
    }

    private void checkBuilderInit() {
        try {
            if (null != builder && builder.isShowing()) {
                builder.dismiss();
                builder = null;
            }
        } catch (Exception e) {
        }

    }

    public AlertDialog getAlertDialogss(final String permissionName, Activity activity, final PermissionHelper permissionHelper) {
        Utils.permissionDeclineStatus = true;
        checkBuilderInit();
        builder = new AlertDialog.Builder(activity)
                .setTitle(" Request ")
                .create();
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.permissionStatus = true;
                permissionHelper.requestSystemAlertPermission();
            }
        });
        builder.setMessage("Permissions need  " + permissionName + "");
        builder.setCancelable(false);
        return builder;
    }

    public AlertDialog getAlertDialogs(final String permission, Activity activity, final PermissionHelper permissionHelper) {
        Utils.permissionDeclineStatus = true;
        checkBuilderInit();
        builder = new AlertDialog.Builder(activity)
                .setTitle(permission)
                .create();
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.permissionStatus = true;
                permissionHelper.requestSystemAlertPermission();
            }
        });
        builder.setCancelable(false);
        builder.setMessage(" Open permission and give required permission ");
        return builder;
    }

    /**
     * A callback interface for receiving the results of a permission request.
     */
    public interface IOnPermissionResult {
        /**
         * Invoked when the results of your permission request are ready.
         *
         * @param resultSet An object holding the result of your permission request.
         */
        void onPermissionResult(ResultSet resultSet);

        /**
         * Called when the system recommends that you provide a rationale for a permission. This typically happens when
         * a user denies a permission, but they you request it again.
         *
         * @param callback    A callback to be triggered when you are finished showing the user the rationale.
         * @param permissions The list of permissions for which the system recommends you provide a rationale.
         */
        void onRationaleRequested(IOnRationaleProvided callback, String... permissions);
    }

    /**
     * Simple callback to let Permiso know that you have finished providing the user a rationale for a set of permissions.
     * For easy handling of this callback, consider using
     * {@link PermissionUtils#showRationaleInDialog(String, String, String, IOnRationaleProvided)}.
     */
    public interface IOnRationaleProvided {
        /**
         * Invoke this method when you are done providing a rationale to the user in
         * {@link IOnPermissionResult#onRationaleRequested(IOnRationaleProvided, String...)}. The permission request
         * will not be made until this method is invoked.
         */
        void onRationaleProvided();
    }

    private static class RequestData {
        IOnPermissionResult onResultListener;
        ResultSet resultSet;

        public RequestData(@NonNull IOnPermissionResult onResultListener, String... permissions) {
            this.onResultListener = onResultListener;
            resultSet = new ResultSet(permissions);
        }
    }

    /**
     * A class representing the results of a permission request.
     */
    public static class ResultSet {

        private Map<String, Boolean> requestResults;

        private ResultSet(String... permissions) {
            requestResults = new HashMap<>(permissions.length);
            for (String permission : permissions) {
                requestResults.put(permission, false);
            }
        }

        /**
         * Checks if a permission was granted during your permission request.
         *
         * @param permission The permission you are inquiring about. This should be a constant from {@link android.Manifest.permission}.
         * @return True if the permission was granted, otherwise false.
         */
        public boolean isPermissionGranted(String permission) {
            if (requestResults.containsKey(permission)) {
                return requestResults.get(permission);
            }
            return false;
        }


        /**
         * Determines if all permissions in the request were granted.
         *
         * @return True if all permissions in the request were granted, otherwise false.
         */
        public boolean areAllPermissionsGranted() {
            return !requestResults.containsValue(false);
        }

        /**
         * Returns a map representation of this result set. Useful if you'd like to do more complicated operations
         * with the results.
         *
         * @return A mapping of permission constants to booleans, where true indicates that the permission was granted,
         * and false indicates that the permission was denied.
         */
        public Map<String, Boolean> toMap() {
            return new HashMap<>(requestResults);
        }

        private void grantPermissions(String... permissions) {
            for (String permission : permissions) {
                requestResults.put(permission, true);
            }
        }

        private void parsePermissionResults(String[] permissions, int[] grantResults) {
            for (int i = 0; i < permissions.length; i++) {
                requestResults.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
        }

        private String[] getUngrantedPermissions() {
            List<String> ungrantedList = new ArrayList<>(requestResults.size());
            for (String permission : requestResults.keySet()) {
                if (!requestResults.get(permission)) {
                    ungrantedList.add(permission);
                }
            }
            return ungrantedList.toArray(new String[ungrantedList.size()]);
        }

        private boolean containsAllUngrantedPermissions(ResultSet set) {
            List<String> ungranted = Arrays.asList(set.getUngrantedPermissions());
            return requestResults.keySet().containsAll(ungranted);
        }

        private String[] getPermissionsThatNeedRationale(FragmentActivity activity) {
            String[] ungranted = getUngrantedPermissions();
            List<String> shouldShowRationale = new ArrayList<>(ungranted.length);
            for (String permission : ungranted) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    shouldShowRationale.add(permission);
                }
            }
            return shouldShowRationale.toArray(new String[shouldShowRationale.size()]);
        }
    }
}