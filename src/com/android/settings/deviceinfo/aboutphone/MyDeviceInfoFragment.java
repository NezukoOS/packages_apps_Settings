/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.aboutphone;

import android.app.Activity;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.BluetoothAddressPreferenceController;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.FccEquipmentIdPreferenceController;
import com.android.settings.deviceinfo.FeedbackPreferenceController;
import com.android.settings.deviceinfo.IpAddressPreferenceController;
import com.android.settings.deviceinfo.ManualPreferenceController;
import com.android.settings.deviceinfo.RegulatoryInfoPreferenceController;
import com.android.settings.deviceinfo.SafetyInfoPreferenceController;
import com.android.settings.deviceinfo.UptimePreferenceController;
import com.android.settings.deviceinfo.WifiMacAddressPreferenceController;
import com.android.settings.deviceinfo.imei.ImeiInfoPreferenceController;
import com.android.settings.deviceinfo.simstatus.SimStatusPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.LayoutPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class MyDeviceInfoFragment extends DashboardFragment
        implements DeviceNamePreferenceController.DeviceNamePreferenceHost {

    private static final String LOG_TAG = "MyDeviceInfoFragment";

    private BuildNumberPreferenceController mBuildNumberPreferenceController;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DEVICEINFO;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_about;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        use(ImeiInfoPreferenceController.class).setHost(this /* parent */);
        use(DeviceNamePreferenceController.class).setHost(this /* parent */);
        mBuildNumberPreferenceController = use(BuildNumberPreferenceController.class);
        mBuildNumberPreferenceController.setHost(this /* parent */);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.my_device_info;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this /* fragment */, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, MyDeviceInfoFragment fragment, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new SimStatusPreferenceController(context, fragment));
        controllers.add(new IpAddressPreferenceController(context, lifecycle));
        controllers.add(new WifiMacAddressPreferenceController(context, lifecycle));
        controllers.add(new BluetoothAddressPreferenceController(context, lifecycle));
        controllers.add(new RegulatoryInfoPreferenceController(context));
        controllers.add(new SafetyInfoPreferenceController(context));
        controllers.add(new ManualPreferenceController(context));
        controllers.add(new FeedbackPreferenceController(fragment, context));
        controllers.add(new FccEquipmentIdPreferenceController(context));
        controllers.add(new UptimePreferenceController(context, lifecycle));
        controllers.add(new NezukoInfoPreferenceController(context));
        return controllers;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBuildNumberPreferenceController.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showDeviceNameWarningDialog(String deviceName) {
        DeviceNameWarningDialog.show(this);
    }

    public void onSetDeviceNameConfirm(boolean confirm) {
        final DeviceNamePreferenceController controller = use(DeviceNamePreferenceController.class);
        controller.updateDeviceName(confirm);
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.my_device_info) {

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null /* fragment */,
                            null /* lifecycle */);
                }
            };
}
