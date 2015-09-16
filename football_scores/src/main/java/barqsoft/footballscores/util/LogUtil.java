package barqsoft.footballscores.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by elnoxvie on 24/8/15.
 */
public class LogUtil {

    private String mTag;

    public LogUtil(String tag){
        mTag = tag;
    }

    public void d(String text){
        Log.d(mTag, text);
    }

    public void w(String text){
        Log.w(mTag, text);
    }

    public void v(String text){
        Log.v(mTag, text);
    }

    public static void d(String TAG, String text){
        Log.d(TAG, text);
    }

    public static void w(String TAG, String text){
        Log.w(TAG, text);
    }

    public static void v(String TAG, String text){
        Log.v(TAG, text);
    }


    public static class Builder{
        private String mLogTag;

        public Builder setLogTag(String tag){
            mLogTag = tag;

            return this;
        }

        public Builder setLogTag(Class tag){
            mLogTag = tag.getSimpleName();

            return this;
        }

        public LogUtil build(){
            if (TextUtils.isEmpty(mLogTag)){
                throw new IllegalArgumentException("Log Tag must be set");
            }

            return new LogUtil(mLogTag);
        }
    }
}
