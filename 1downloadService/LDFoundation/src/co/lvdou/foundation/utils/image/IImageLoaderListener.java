package co.lvdou.foundation.utils.image;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;

public interface IImageLoaderListener {
	
	public static IImageLoaderListener Null = new IImageLoaderListener() {
		
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			
		}
		
		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			
		}
		
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			
		}
		
		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			
		}
	};
	
    public void onLoadingStarted(String imageUri, View view);

    public void onLoadingFailed(String imageUri, View view, FailReason failReason);

    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);

    public void onLoadingCancelled(String imageUri, View view);

}
