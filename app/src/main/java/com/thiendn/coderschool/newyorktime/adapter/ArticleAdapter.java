package com.thiendn.coderschool.newyorktime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thiendn.coderschool.newyorktime.R;
import com.thiendn.coderschool.newyorktime.model.Article;

import java.util.List;

/**
 * Created by thiendn on 27/02/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Article> mArticles;
    private static final int WITH_MEDIA = 1;
    private static final int WITHOUT_MEDIA = 0;
    private Context context;
    public ArticleAdapter(List<Article> articles) {
        this.mArticles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        final View itemView;
        final ArticleAdapter.Listener activity = (Listener) context;
        switch (viewType){
            case WITH_MEDIA:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_withpic, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onItemClicked(itemView);
                    }
                });
                return new PicViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_nopic, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onItemClicked(itemView);
                    }
                });
                return new NoPicViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = mArticles.get(position);
        if (holder instanceof PicViewHolder){
            ((PicViewHolder) holder).tvHeadline.setText(article.getHeadline());
            ((PicViewHolder) holder).tvSnippet.setText(article.getSnippet());
            Picasso.with(context).load(article.getImageUrl()).into(((PicViewHolder) holder).ivCover);
        }else{
            ((NoPicViewHolder) holder).tvHeadline.setText(article.getHeadline());
            ((NoPicViewHolder) holder).tvSnippet.setText(article.getSnippet());
        }
        if (position == mArticles.size() - 1) {
            ArticleAdapter.Listener activity = (Listener) context;
            activity.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticles.get(position);
        if (article.haveImage()) return WITH_MEDIA;
        return WITHOUT_MEDIA;
    }

    private class NoPicViewHolder extends RecyclerView.ViewHolder{
        TextView tvHeadline;
        TextView tvSnippet;

        public NoPicViewHolder(View view) {
            super(view);
            this.tvHeadline = (TextView) view.findViewById(R.id.tvHeadline);
            this.tvSnippet = (TextView) view.findViewById(R.id.tvSnippet);
        }
    }

    private class PicViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCover;
        TextView tvHeadline;
        TextView tvSnippet;

        public PicViewHolder(View view) {
            super(view);
            this.ivCover = (ImageView) view.findViewById(R.id.ivCover);
            this.tvHeadline = (TextView) view.findViewById(R.id.tvHeadline);
            this.tvSnippet = (TextView) view.findViewById(R.id.tvSnippet);
        }
    }

    public interface Listener{
        void onLoadMore();
        void onItemClicked(View itemView);
    }
}
