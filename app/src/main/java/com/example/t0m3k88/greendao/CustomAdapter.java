package com.example.t0m3k88.greendao;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.t0m3k88.greendao.persistance.CursorViewHolder;
import com.example.t0m3k88.greendao.persistance.EntityManager;
import com.example.t0m3k88.greendao.persistance.ModernAdapter;
import com.example.t0m3k88.greendao.persistance.model.User;
import com.example.t0m3k88.greendao.persistance.model.UserDao;

import java.util.Objects;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public class CustomAdapter extends ModernAdapter<CustomAdapter.ViewHolder2> {


    public CustomAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        return new ViewHolder2(root, User.class);
    }

    @Override
    public void onBindViewHolder(ViewHolder2 holder, int position) {
        mCursor.moveToPosition(position);
        holder.bind(mCursor);
    }

    public static class ViewHolder2 extends CursorViewHolder<User> implements View.OnClickListener{

        public TextView text = null;

        public ViewHolder2(View itemView, Class<User> clazz) {
            super(itemView, clazz);
            text = (TextView)itemView;
        }

        @Override
        public void bind(Cursor cursor) {
            ((UserDao)App.getInstanceOfDao(item.getClass())).readEntity(cursor, item, 0);
            text.setText(item.getName());
            text.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    EntityManager<User, Long> manager = (EntityManager<User, Long>)App.getEntityMamager(item.getClass());
                    manager.delete(item, "TEST"); // (UserDao)App.getInstanceOfDao(item.getClass())).delete(item);
                }
            });
            t.start();

        }
    }

}
