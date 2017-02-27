package com.thiendn.coderschool.newyorktime.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.stetho.common.StringUtil;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.order;

/**
 * Created by thiendn on 27/02/2017.
 */

public class SearchRequest implements Parcelable{
    private String beginDate;
    private String sort;
    private String desk;
    private String keySearch;
    private int page;
    public boolean hasArts = false;
    public boolean hasFashionStyle = false;
    public boolean hasSports = false;

    protected SearchRequest(Parcel in) {
        beginDate = in.readString();
        sort = in.readString();
        desk = in.readString();
        keySearch = in.readString();
        page = in.readInt();
        hasArts = in.readByte() != 0;
        hasFashionStyle = in.readByte() != 0;
        hasSports = in.readByte() != 0;
    }

    public static final Creator<SearchRequest> CREATOR = new Creator<SearchRequest>() {
        @Override
        public SearchRequest createFromParcel(Parcel in) {
            return new SearchRequest(in);
        }

        @Override
        public SearchRequest[] newArray(int size) {
            return new SearchRequest[size];
        }
    };

    public String getKeySearch() {
        return keySearch;
    }

    public void setKeySearch(String keySearch) {
        this.keySearch = keySearch;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public SearchRequest(String beginDate, String sort, String keySearch, int page,
                         boolean hasArts, boolean hasFashionStyle, boolean hasSports) {
        this.beginDate = beginDate;
        this.sort = sort;
        this.keySearch = keySearch;
        this.page = page;
        this.hasArts = hasArts;
        this.hasFashionStyle = hasFashionStyle;
        this.hasSports = hasSports;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    public Map<String, String> toQuery() {
        Map<String, String> queryUrl = new HashMap<>();
        if (keySearch != null) queryUrl.put("q", keySearch);
        if (beginDate != null) queryUrl.put("begin_date", beginDate);
        if (sort != null) queryUrl.put("sort", sort.toLowerCase());
        if (getNewDesk() != null) queryUrl.put("fq", "news_desk:(" + getNewDesk() + ")");
        queryUrl.put("page", String.valueOf(page));

        return queryUrl;
    }

    private String getNewDesk() {
        if (!hasSports && !hasArts && !hasFashionStyle) return null;

        String desk = "";
        if (hasArts) desk += "\"Arts\"";
        if (hasSports) desk += " \"Sports\"";
        if (hasFashionStyle) desk += " \"Fashion & Style\"";

        return desk;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(beginDate);
        dest.writeString(sort);
        dest.writeString(desk);
        dest.writeString(keySearch);
        dest.writeInt(page);
        dest.writeByte((byte) (hasArts ? 1 : 0));
        dest.writeByte((byte) (hasFashionStyle ? 1 : 0));
        dest.writeByte((byte) (hasSports ? 1 : 0));
    }
}
