package co.lvdou.foundation.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;

/**
 * 图片加载器,用于加载网络图片
 * 
 * @author 郑一
 */
public final class LDImageLoader {
	private static LDImageLoader mInstance = null;
	private ImageLoader mLoader = null;

	private LDImageLoader(Context context) {
		init(context);
	}

	/**
	 * 获取公共的 {@link co.lvdou.foundation.utils.image.LDImageLoader} 实例
	 */
	public static LDImageLoader shareLoader() {
		if (mInstance == null) {
			final Context context = LDContextHelper.getContext();
			mInstance = new LDImageLoader(context);
		}
		return mInstance;
	}

	/**
	 * 异步显示网络图片
	 * 
	 * @param url
	 *            网络图片下载地址
	 * @param view
	 *            用于展示网络图片的 {@link android.widget.ImageView} 控件
	 */
	public void displayImage(final String url, ImageView view) {
		displayImage(url, view, null);
	}

	/**
	 * 异步显示网络图片
	 * 
	 * @param url
	 *            网络图片下载地址
	 * @param view
	 *            用于展示网络图片的 {@link android.widget.ImageView} 控件
	 * @param options
	 *            加载图片的附加选项，目前只可以设置默认加载图片
	 */

	public void displayImage(final String url, ImageView view, LDDisplayImageOption options) {
		if (view == null) {
			return;
		}

		if (options == null) {
			mLoader.displayImage(url, view);
		} else {
			mLoader.displayImage(url, view, options.build());
		}
	}

	/**
	 * 取消 {@link android.widget.ImageView} 控件相关的异步图片加载
	 * 
	 * @param imageView
	 *            相关的 {@link android.widget.ImageView} 控件
	 */
	public void cancelDisplay(final ImageView imageView) {
		mLoader.cancelDisplayTask(imageView);
	}

	/**
	 * 异步下载网络图片，假如图片已经下载的话将不会再次下载
	 * 
	 * @param url
	 *            网络图片的下载地址
	 */
	public void loadImageAsync(final String url) {
		mLoader.loadImage(url, new ImageLoadingListener() {
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
		});
	}

	/**
	 * 异步下载网络图片，假如图片已经下载的话将不会再次下载
	 * 
	 * @param url
	 *            网络图片的下载地址
	 * @param listener
	 *            回调
	 */
	public void loadImageAsync(final String url, final IImageLoaderListener listener) {
		mLoader.loadImage(url, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				listener.onLoadingStarted(imageUri, view);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				listener.onLoadingFailed(imageUri, view, failReason);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				listener.onLoadingComplete(imageUri, view, loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				listener.onLoadingCancelled(imageUri, view);
			}
		});
	}

	/**
	 * 获取下载地址相关的网络图片，不存在将返回空
	 * 
	 * @param url
	 *            网络图片的下载地址
	 */
	public Bitmap loadImageLocal(final String url) {
		return mLoader.loadImageSync(url);
	}

	/**
	 * 获取下载地址相关的网络图片，不存在将返回空
	 * 
	 * @param url
	 *            网络图片的下载地址
	 * @param size
	 *            图片的显示尺寸
	 */
	public Bitmap loadImageLocal(final String url, ImageSize size) {
		return mLoader.loadImageSync(url, size);
	}

	/**
	 * 判断本地是否存在下载地址相关的图片缓存
	 * 
	 * @param url
	 *            网络图片相关的下载地址
	 */
	public boolean isImageInDisk(final String url) {
		boolean result = false;
		if (mLoader.getMemoryCache().keys().contains(url)) {
			result = true;
		} else {
			File f = mLoader.getDiscCache().get(url);
			if (f.exists() && !f.isDirectory()) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 根据图片的下载地址获取本地地址，不存在将返回空
	 * 
	 * @param url
	 *            网络图片的下载地址
	 */
	public String getPathByUrl(String url) {
		File f = mLoader.getDiscCache().get(url);
		return f != null ? f.getAbsolutePath() : null;
	}

	/**
	 * 清除网络图片的本地缓存和内存缓存
	 */
	public void clearCache() {
		mLoader.clearMemoryCache();
		mLoader.clearDiscCache();
	}

	/**
	 * 清除网络图片的内存缓存
	 */
	public void clearCacheInMemory() {
		mLoader.clearMemoryCache();
	}

	private void init(Context context) {
		if (mLoader == null) {
			mLoader = ImageLoader.getInstance();
			mLoader.init(generateConfiguration(context));
		}
	}

	private ImageLoaderConfiguration generateConfiguration(Context context) {
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		builder.defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).decodingOptions(options).build());
		return builder.build();
	}
}
