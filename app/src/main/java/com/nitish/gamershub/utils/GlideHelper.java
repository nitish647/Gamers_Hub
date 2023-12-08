package com.nitish.gamershub.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nitish.gamershub.model.local.GlideData;

public class GlideHelper {


    public static void loadGlideImage(ImageView imageView, GlideData glideData, GlideListener glideListener) {

        Context context = imageView.getContext();

        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(glideData.getImageUrl());


        if (glideListener != null) {
            requestBuilder.placeholder(glideData.getPlaceHolder()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                    glideListener.onImageLoadFailed();

                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    glideListener.onImageLoaded(resource);
                    return false;
                }
            }).into(imageView);
            return;
        }


        requestBuilder.placeholder(glideData.getPlaceHolder()).into(imageView);


    }

    public interface GlideListener {
        void onImageLoaded(Drawable drawable);

        void onImageLoadFailed();


    }
}
