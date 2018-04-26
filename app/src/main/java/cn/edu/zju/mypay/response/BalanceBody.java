package cn.edu.zju.mypay.response;

/**
 * Created by Austin on 2018/4/26.
 */

public class BalanceBody {
    private long account;
    private float balance;

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
