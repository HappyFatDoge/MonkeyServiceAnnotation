package com.nineteenc.annotation.util;

import android.content.Context;
import android.os.Messenger;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/20 12:13
 * Modify by:
 * Modification date:
 * Modify content:
 */
public interface IMonkeyUtil {

    void setContext(Context context);

    void setClientMessenger(Messenger messenger);
}
