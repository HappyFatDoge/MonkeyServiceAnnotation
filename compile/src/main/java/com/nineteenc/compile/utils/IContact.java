package com.nineteenc.compile.utils;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/21 9:22
 * Modify by:
 * Modification date:
 * Modify content:
 */
public interface IContact {

    String PAPER_POINT_READ_NAME = "PaperPointRead";

    String COMIC_BOOK_READ_NAME = "ComicBookRead";

    String MONKEY_SERVICE_PACKAGE_NAME = "com.nineteenc.monkeyservice";

    String MONKEY_SERVICE_PPRCLASS_NAME = "com.nineteenc.monkeyservice.paperpointread.PaperPointReadService";

    String MONKEY_SERVICE_CBCLASS_NAME = "com.nineteenc.monkeyservice.comicread.service.ComicBookMonkeyService";

    int PPR_TYPE_MESSAGE = 1;

    int CB_TYPE_START_SCAN = -1;

    int CB_TYPE_SCAN_BOOK_RESULT = 0;

    int CB_TYPE_BOOK_PAGE = 1;

    int CB_TYPE_STOP_SEND_MESSAGE = 2;

    /**
     * 方法名称
     */

    String GET_INSTANCE = "getInstance";

    String IS_USER_A_MONKEY = "isUseAMonkey";

    String START_MONKEY_SERVICE = "startMonkeyServiceIfNeed";

    String STOP_MONKEY_SERVICE = "stopMonkeyServiceIfNeed";

    String BIND_MONKEY_SERVICE = "bindMonkeyServiceIfNeed";

    String UNBIND_MONKEY_SERVICE = "unbindMonkeyServiceIfNeed";

    String SEND_MESSAGE = "sendMsg2MService";

}
