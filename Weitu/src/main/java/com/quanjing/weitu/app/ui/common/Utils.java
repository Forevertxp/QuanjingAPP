package com.quanjing.weitu.app.ui.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

/**
 * 工具类
 * 
 * @author Ramboo
 * @date 2014-08-22 17:30
 */
public class Utils {
	/**
	 * 
	 * @param is
	 * @param os
	 * @throws java.io.IOException
	 */
	public static void CopyStream(InputStream is, OutputStream os) throws IOException {
		final int buffer_size = 1024;
		
		byte[] bytes = new byte[buffer_size];
		for (;;) {
			int count = is.read(bytes, 0, buffer_size);
			if (count == -1)
				break;
			
			os.write(bytes, 0, count);
			// is.close();
			// os.close();
		}
	}
	
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}	
}
