package com.nitish.gamershub.model.bannerImagesData;

import com.google.gson.annotations.SerializedName;

public class BannerImagesItem{

	@SerializedName("largeImageUrl")
	private String largeImageUrl;

	@SerializedName("name")
	private String name;

	public void setLargeImageUrl(String largeImageUrl){
		this.largeImageUrl = largeImageUrl;
	}

	public String getLargeImageUrl(){
		return largeImageUrl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"BannerImagesItem{" + 
			"largeImageUrl = '" + largeImageUrl + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}