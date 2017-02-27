package com.thiendn.coderschool.newyorktime.api;

import com.thiendn.coderschool.newyorktime.model.ArticleSearchResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by thiendn on 27/02/2017.
 */

public interface ArticleApi {
    @GET("articlesearch.json")
    Call<ArticleSearchResult> getSearch(@QueryMap(encoded = true) Map<String, String> options);
}
