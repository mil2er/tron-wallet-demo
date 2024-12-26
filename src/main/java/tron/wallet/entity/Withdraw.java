package tron.wallet.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import tron.wallet.entity.Transaction;

@Data
public class Withdraw {

	private String requestId;
	private String account;
	private String address;
	private BigDecimal amount = BigDecimal.ZERO;
	private String txId;
	private long height;
	private int status = 0; // 1:成功; 0: 待处理; -1: 失败（链上已确认）;
	private int retry = 0;
	private long createMillis;
	private long updateMillis;

	@JsonIgnore
	public boolean isFailed() {
		return status == -1;
	}

	@JsonIgnore
	public boolean isSuccess() {
		return status == 1;
	}

	@JsonIgnore
	public boolean isProcessing() {
		return status == 0;
	}

	@JsonIgnore
	public Transaction toTransaction() {
		Transaction t = new Transaction();
        t.setSide("withdraw");
        t.setRequestId(this.getRequestId());
		t.setTxId(this.getTxId());
        if (isSuccess()) {
            t.setStatus("success");
        }else if (isFailed()){
            t.setStatus("failed");
        }else{
            t.setStatus("processing");
        }
	    return t;
	}
}
