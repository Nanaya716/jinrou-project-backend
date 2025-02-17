package com.jinrou.jinrouwerewolf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/18:40
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T>{
    private Integer code;
    private String message; //提示信息
    private T data;

    public static Result success(Map<String, Object> data){
        return new Result<>(200,"success", data);
    }

    public static Result success( String message, Map<String, Object> data){
        return new Result<>(200,message, data);
    }

    public static Result success(Object data){
        return new Result<>(200,"success", data);
    }

    public static Result success(String message, Object data){
        return new Result<>(200, message, data);
    }

    //快速返回操作成功结果
    public static Result success(){
        return new Result(200,"success", null);
    }

    public static Result error(String message){
        return new Result(-1, message,null);
    }
    public static Result error(String message,Object data){
        return new Result(-1, message, data);
    }
}
