package com.thiendn.coderschool.newyorktime.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thiendn on 27/02/2017.
 */

public class ArticleSearchResult {
    @SerializedName("docs")
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
}
