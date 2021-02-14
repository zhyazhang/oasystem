package com.aifurion.oasystem.common.formVaild;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 9:27
 */
public enum ResultEnum {

    ERROR(2, "验证失败"),
    SUCCESS(200, "成功"),
    NONETYPE(1, "找不到参数"),;

    /**
     * 返回码
     *
     */
    private Integer code;

    /**
     * 返回信息
     *
     */
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
