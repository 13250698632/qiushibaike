package com.lcm.api.entity;

import com.lcm.api.service.ResultCode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class RequestMessage {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<String,Object>();

    //私有化构造函数
    private RequestMessage(){}

    //成功
    public static RequestMessage ok(){
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(ResultCode.SUCCESS);
        requestMessage.setSuccess(true);
        requestMessage.setMessage("成功");
        return requestMessage;
    }

    //失败
    public static RequestMessage error(){
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(ResultCode.ERROR);
        requestMessage.setSuccess(false);
        requestMessage.setMessage("失败");
        return requestMessage;
    }

    public RequestMessage success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public RequestMessage code(Integer code){
        this.setCode(code);
        return this;
    }
    public RequestMessage message(String message){
        this.setMessage(message);
        return this;
    }

    public RequestMessage data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public RequestMessage data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
