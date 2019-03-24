package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.LocationsViewModel;

public class SyncWithServerAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "main_sync_account";

    private LocationsViewModel locationsViewModel;

    public SyncWithServerAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        locationsViewModel = ViewModelProviders.of((AppCompatActivity) context).get(LocationsViewModel.class);
        Log.d("SyncWithServerAdapter", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public SyncWithServerAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        locationsViewModel = ViewModelProviders.of((AppCompatActivity) context).get(LocationsViewModel.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d("SyncWithServerAdapter", locationsViewModel.getAll().toString());
        Log.d("SyncWithServerAdapter", "----------------------------------------------------");
    }
}
