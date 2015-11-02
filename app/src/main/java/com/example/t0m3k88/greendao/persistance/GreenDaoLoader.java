package com.example.t0m3k88.greendao.persistance;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.OperationCanceledException;
import android.support.v4.content.AsyncTaskLoader;

import com.example.t0m3k88.greendao.App;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import de.greenrobot.dao.query.CursorQuery;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public class GreenDaoLoader extends AsyncTaskLoader<Cursor> {

    final ForceLoadContentObserver mObserver;
    private String mTag;
    private CursorQuery mQuery;

    Cursor mCursor;
    CancellationSignal mCancellationSignal;

    public final class ForceLoadContentObserver extends ContentObserver {
        public ForceLoadContentObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }


    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            CursorQuery query = mQuery.forCurrentThread();
            Cursor cursor = query.query();
            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.getCount();
                    cursor.registerContentObserver(mObserver);
                    Application app = (Application)getContext().getApplicationContext();
                    if(app instanceof NotificationManagerProvider){
                        ((NotificationManagerProvider)app).provide().register(mTag, mObserver);
                    }
                } catch (RuntimeException ex) {
                    cursor.close();
                    throw ex;
                }
            }
            return cursor;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    public GreenDaoLoader(Context context, CursorQuery query) {
        this(context, query, null);
        mTag = "Common";
    }

    public GreenDaoLoader(Context context, CursorQuery query, String commonTag) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mQuery = query;
        mTag = commonTag;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
        Application app = (Application)getContext().getApplicationContext();
        if(app instanceof NotificationManagerProvider){
            ((NotificationManagerProvider)app).provide().unregister(mTag, mObserver);
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

    public void setmQuery(CursorQuery mQuery) {
        this.mQuery = mQuery;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix); writer.print("mProjection=");
        writer.print(prefix); writer.print("mSelectionArgs=");
        writer.print(prefix); writer.print("mCursor="); writer.println(mCursor);
    }
}
