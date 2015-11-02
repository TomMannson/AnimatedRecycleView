package com.example.t0m3k88.greendao.persistance;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public abstract class ModernAdapter<T extends CursorViewHolder> extends RecyclerView.Adapter<T>{

    protected LayoutInflater inflater = null;
    protected Cursor mCursor = null;
    boolean dataIsValid;
    private int mRowIDColumn;
    private boolean firstTime = true;


    Map<Integer, Long> preMapOfIds = new HashMap<>();
    Map<Long, Integer> preMapOfPositions = new HashMap<>();
    Map<Integer, Long> postMapOfIds = new HashMap<>();
    Map<Long, Integer> postMapOfPositions = new HashMap<>();

    public ModernAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        if(mCursor.isAfterLast())
            return -1;

        return mCursor.getLong(0);
    }

    public Cursor swapCursor(Cursor newCursor, final RecyclerView list) {

        LinearLayoutManager manager = null;
        if(list.getLayoutManager() instanceof LinearLayoutManager){
            manager = (LinearLayoutManager)list.getLayoutManager();
        }
        final int firstVisiblePosition = manager.findFirstVisibleItemPosition();


        if (newCursor == mCursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        if (newCursor != null && oldCursor != null) {
            if(!newCursor.isClosed()){
                preMapOfIds.clear();
                preMapOfPositions.clear();
                for (int i = 0; i < manager.getChildCount(); i++) {
                    View child = manager.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = this.getItemId(position);
                    preMapOfIds.put(i, itemId);
                    preMapOfPositions.put(itemId, child.getTop());
                }
                final ViewTreeObserver viewObserver = list.getViewTreeObserver();
                viewObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        LinearLayoutManager manager = (LinearLayoutManager)list.getLayoutManager();
                        final int firstVisiblePosition = manager.findFirstVisibleItemPosition();
                        viewObserver.removeOnPreDrawListener(this);
                        postMapOfIds.clear();
                        postMapOfPositions.clear();
                        for (int i = 0; i < list.getLayoutManager().getChildCount(); ++i) {
                            View child = list.getLayoutManager().getChildAt(i);
                            int position = firstVisiblePosition + i;
                            long itemId = getItemId(position);
                            if(itemId != -1) {
                                postMapOfIds.put(i, itemId);
                                postMapOfPositions.put(itemId, child.getTop());
                            }
                        }

                        if (preMapOfIds.size() == 0 && postMapOfIds.size() != 0 && firstTime) {

                            for (int i = 0; i < list.getLayoutManager().getChildCount(); ++i) {
                                View child = list.getLayoutManager().getChildAt(i);
                                child.setX(-100);
                                child.setAlpha(0);
                                child.animate().translationXBy(100).setDuration(1000)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .alpha(1)
                                        .setStartDelay(i * 30).start();
                                firstTime = false;
                            }

                        } else {
                            for (int i = 0; i < list.getLayoutManager().getChildCount(); i++) {
                                View child = list.getLayoutManager().getChildAt(i);
                                int position = firstVisiblePosition + i;
                                long itemId = getItemId(position);

                                if (preMapOfPositions.get(itemId) != null && postMapOfPositions.get(itemId) != null) {
                                    child.setY(preMapOfPositions.get(itemId));

                                    int shift = postMapOfPositions.get(itemId) - preMapOfPositions.get(itemId);
                                    child.animate().cancel();
                                    child.animate().translationYBy(shift)
                                            .setDuration(300)
                                            .setInterpolator(new AccelerateDecelerateInterpolator())
                                            .start();
                                    child.setHasTransientState(true);
                                }
                                else if(preMapOfPositions.get(itemId) != null && postMapOfPositions.get(itemId) == null){
                                    child.setY(postMapOfPositions.get(itemId)+(child.getBottom()-child.getTop()));
                                    child.animate().y(postMapOfPositions.get(itemId)).setDuration(300);
                                    child.setHasTransientState(true);
                                }
                                else if(preMapOfPositions.get(itemId) == null && postMapOfPositions.get(itemId) != null){
                                    child.setAlpha(0);//postMapOfPositions.get(itemId)+(child.getBottom()-child.getTop()));
                                    child.animate().alpha(1).setDuration(300);
                                    child.setHasTransientState(true);
                                }
                            }
                        }

                        return true;
                    }
                });
            }
        }
        mCursor = newCursor;
        if (newCursor != null) {
            // if (mChangeObserver != null)
            // newCursor.registerContentObserver(mChangeObserver);
            // if (mDataSetObserver != null)
            // newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            dataIsValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        }
//        } else {
//            mRowIDColumn = -1;
//            dataIsValid = false;
//            // notify the observers about the lack of a data set
//            notifyDataSetInvalidated();
//        }
        return oldCursor;
    }
}
