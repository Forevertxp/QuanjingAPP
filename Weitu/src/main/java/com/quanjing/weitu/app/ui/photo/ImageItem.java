package com.quanjing.weitu.app.ui.photo;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
    public long imageDate;
    public String longtitude;
    public String latitude;
	private Bitmap bitmap;
	public boolean isSelected = false;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

    public long getImageDate() {
        return imageDate;
    }

    public void setImageDate(long imageDate) {
        this.imageDate = imageDate;
    }

    public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Bitmap getBitmap() {		
		if(bitmap == null){
            try {
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
	
}
