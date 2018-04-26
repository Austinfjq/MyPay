package cn.edu.zju.mypay.response;

/**
 * Created by Austin on 2018/4/26.
 */

public class BalanceResponse {
    private int error;
    private String msg;
    private BalanceBody body;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BalanceBody getBody() {
        return body;
    }

    public void setBody(BalanceBody body) {
        this.body = body;
    }
}
