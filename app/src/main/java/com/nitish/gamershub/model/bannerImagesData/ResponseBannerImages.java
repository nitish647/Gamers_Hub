package com.nitish.gamershub.model.bannerImagesData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseBannerImages {

	@SerializedName("bannerImages")
	private List<BannerImagesItem> bannerImages;

	public void setBannerImages(List<BannerImagesItem> bannerImages){
		this.bannerImages = bannerImages;
	}

	public List<BannerImagesItem> getBannerImages(){
		return bannerImages;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"bannerImages = '" + bannerImages + '\'' + 
			"}";
		}
}