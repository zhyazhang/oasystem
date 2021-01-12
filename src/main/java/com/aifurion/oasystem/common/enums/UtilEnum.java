package com.aifurion.oasystem.common.enums;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 15:43
 */
public enum UtilEnum {

    /**
     * session captcha
     *
     */


    CAPTCHA_KEY("session_captcha", 1),

    /**
     *去除易混淆字符后的可用验证码字符
     *
     *
     */

    VERIFY_CODES("23456789ABCDEFGHJKLMNPQRSTUVWXYZ", 2);




    private String content;

    private int index;

    UtilEnum(String content, int index) {
        this.content = content;
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
