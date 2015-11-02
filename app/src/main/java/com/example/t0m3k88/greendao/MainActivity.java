package com.example.t0m3k88.greendao;

import android.app.SearchManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.t0m3k88.greendao.persistance.EntityManager;
import com.example.t0m3k88.greendao.persistance.GreenDaoLoader;
import com.example.t0m3k88.greendao.persistance.model.Note;
import com.example.t0m3k88.greendao.persistance.model.NoteDao;
import com.example.t0m3k88.greendao.persistance.model.User;
import com.example.t0m3k88.greendao.persistance.model.UserDao;

import java.util.UUID;

import de.greenrobot.dao.query.CursorQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar toolbar = null;
    RecyclerView list = null;
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            Toast.makeText(getApplicationContext(), "Recreate", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_main);

        list = (RecyclerView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CustomAdapter(getApplicationContext());
        list.setAdapter(adapter);



        UserDao userDao = (UserDao) App.getInstanceOfDao(User.class);
        long count = userDao.queryBuilder().count();


//        userDao.getDatabase().beginTransaction();

        getSupportLoaderManager().initLoader(1, null, this);

        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Bundle bundle = null;
                if(!newText.isEmpty()) {
                    bundle = new Bundle();
                    bundle.putString("Filter", newText);
                }
                getSupportLoaderManager().restartLoader(1, bundle, MainActivity.this);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 10; i++) {
                        EntityManager<User, Long> manager = (EntityManager<User, Long>) App.getEntityMamager(User.class);
                        manager.create(new User(null, UUID.randomUUID().toString()), "TEST"); // (UserDao)App.getInstanceOfDao(item.getClass())).delete(item);
                    }
                }
            });
            t.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        QueryBuilder<User> builder = (QueryBuilder<User>) App.getInstanceOfDao(User.class).queryBuilder();
        if(args != null){
            builder.where(UserDao.Properties.Name.like("%"+args.getString("Filter")+"%"));
        }
        return new GreenDaoLoader(getApplicationContext(), builder.buildCursor());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data, list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
}
