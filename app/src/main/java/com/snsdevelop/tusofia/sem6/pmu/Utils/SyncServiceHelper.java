package com.snsdevelop.tusofia.sem6.pmu.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import static android.content.Context.ACCOUNT_SERVICE;

public class SyncServiceHelper {
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.snsdevelop.tusofia.sem6.pmu";
    // An account type, in the form of a domain name
    private static final String ACCOUNT_TYPE = "snsdevelop.com";
    // The account name
    private static final String ACCOUNT = "sync_with_server_account";

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        //TODO: do sth
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount;
    }

    public static class StubProvider extends ContentProvider {
        public StubProvider() {

        }

        /*
         * Always return true, indicating that the
         * provider loaded correctly.
         */
        @Override
        public boolean onCreate() {
            return true;
        }
        /*
         * Return no type for MIME type
         */
        @Override
        public String getType(@NonNull Uri uri) {
            return null;
        }
        /*
         * query() always returns no results
         *
         */
        @Override
        public Cursor query(
                @NonNull Uri uri,
                String[] projection,
                String selection,
                String[] selectionArgs,
                String sortOrder) {
            return null;
        }
        /*
         * insert() always returns null (no URI)
         */
        @Override
        public Uri insert(Uri uri, ContentValues values) {
            return null;
        }
        /*
         * delete() always returns "no rows affected" (0)
         */
        @Override
        public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
            return 0;
        }
        /*
         * update() always returns "no rows affected" (0)
         */
        public int update(
                @NonNull Uri uri,
                ContentValues values,
                String selection,
                String[] selectionArgs) {
            return 0;
        }
    }
}
