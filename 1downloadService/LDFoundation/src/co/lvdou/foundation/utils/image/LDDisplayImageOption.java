package co.lvdou.foundation.utils.image;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 加载网络图片的附加选项类
 *
 * @author 郑一
 */
public final class LDDisplayImageOption {
    private static final int UNDEFINED = -1;
    private int mImageResIdOnLoading = UNDEFINED;

    public LDDisplayImageOption() {
    }

    public LDDisplayImageOption(int imageResIdOnLoading) {
        mImageResIdOnLoading = imageResIdOnLoading;
    }

    /**
     * 创建相关实例
     */
    DisplayImageOptions build() {
        if (mImageResIdOnLoading == UNDEFINED) {
            return DisplayImageOptions.createSimple();
        } else {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            builder.showImageOnLoading(mImageResIdOnLoading);
            builder.cacheInMemory(true);
            builder.cacheOnDisc(true);
            return builder.build();
        }
    }
}
