package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by STEPHAN987 on 7/29/2017.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1,articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Article article = getItem(position);
        //check if view is being reused
        //if not then inflate the layout
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result,parent,false);
        }

        //find the imageView control
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        //clear the Image from the previously loaded
        ivImage.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        String thumbnail = article.getThumbNail();
        //populate thumbnail and title

        tvTitle.setText(article.getHeadline());
        if(!TextUtils.isEmpty(thumbnail)){
            Picasso.with(getContext()).load(thumbnail).error(R.drawable.no_image)
                    .placeholder(R.drawable.loading).into(ivImage);
        }

        return convertView;
    }
}
