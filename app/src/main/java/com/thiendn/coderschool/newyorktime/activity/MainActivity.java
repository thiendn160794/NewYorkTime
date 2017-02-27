package com.thiendn.coderschool.newyorktime.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thiendn.coderschool.newyorktime.R;
import com.thiendn.coderschool.newyorktime.adapter.ArticleAdapter;
import com.thiendn.coderschool.newyorktime.api.ArticleApi;
import com.thiendn.coderschool.newyorktime.api.SearchRequest;
import com.thiendn.coderschool.newyorktime.dialog.SettingDialog;
import com.thiendn.coderschool.newyorktime.model.Article;
import com.thiendn.coderschool.newyorktime.model.ArticleSearchResult;
import com.thiendn.coderschool.newyorktime.utils.Retrofits;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SettingDialog.Listener, ArticleAdapter.Listener{

//    @BindView(R.id.rvArticle)
    RecyclerView rvArticle;
    ProgressBar pbLoading;
    ArticleApi mArticleApi;
    SearchView searchView;
    SearchRequest mSearchRequest;
    ArticleAdapter mAdapter;
    List<Article> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable() || !isOnline()){
            Toast.makeText(getBaseContext(), "No network available", Toast.LENGTH_SHORT).show();
        }
        rvArticle = (RecyclerView) findViewById(R.id.rvArticle);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        mSearchRequest = new SearchRequest("20160101", "oldest", null, 0, false, false, false);
        mArticleApi = Retrofits.get().create(ArticleApi.class);
        search();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItemMenu = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItemMenu);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mSearchRequest.setKeySearch(query);
                search();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                showSetting();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSetting(){
        android.app.FragmentManager fm = getFragmentManager();
        SettingDialog settingDialog = SettingDialog.newInstance(mSearchRequest, MainActivity.this);
        settingDialog.show(fm, "Setting");
    }

    private void search(){
        pbLoading.setVisibility(View.VISIBLE);
        mArticleApi.getSearch(mSearchRequest.toQuery()).enqueue(new Callback<ArticleSearchResult>() {
            @Override
            public void onResponse(Call<ArticleSearchResult> call, Response<ArticleSearchResult> response) {
                Log.e("body", String.valueOf(response.isSuccessful()));
                if (null != response.body()) {
                    mList = response.body().getArticles();
                    mAdapter = new ArticleAdapter(mList);
                    rvArticle.setAdapter(mAdapter);
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    rvArticle.setLayoutManager(layoutManager);
                    pbLoading.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "END", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArticleSearchResult> call, Throwable t) {
//                Log.e("MainActivity", t.getMessage());
            }
        });
    }

    @Override
    public void onSaveSetting(SearchRequest searchRequest) {
        mSearchRequest = searchRequest;
        search();
    }

    @Override
    public void onLoadMore() {
        LoadMore();
    }

    @Override
    public void onItemClicked(View itemView) {
        int itemPosition = rvArticle.getChildLayoutPosition(itemView);
        Article item = mList.get(itemPosition);
        Toast.makeText(getBaseContext(), item.getHeadline(), Toast.LENGTH_LONG).show();
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        String url = item.getWebUrl();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.addDefaultShareMenuItem();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private void LoadMore(){
        mSearchRequest.setPage(mSearchRequest.getPage() + 1);
        mArticleApi.getSearch(mSearchRequest.toQuery()).enqueue(new Callback<ArticleSearchResult>() {
            @Override
            public void onResponse(Call<ArticleSearchResult> call, Response<ArticleSearchResult> response) {
                Log.e("body", String.valueOf(response.isSuccessful()));
                if (null != response.body()) {
                    List<Article> newList = response.body().getArticles();
                    for (Article a : newList){
                        mList.add(a);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "END", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ArticleSearchResult> call, Throwable t) {
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
