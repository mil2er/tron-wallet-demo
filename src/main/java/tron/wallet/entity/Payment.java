package tron.wallet.entity;

import lombok.Builder;

import java.math.BigDecimal;
import java.math.BigInteger;

import tron.wallet.util.Wallet;

@Builder
public class Payment {
  private String txBizNumber;
  private String txid;
  private Wallet wallet;
  private String to;
  private BigDecimal amount;
  private String unit;
  private BigInteger gasLimit;
  private BigInteger gasPrice;
  private long height;

  public String getTxid() {
    return txid;
  }

  public void setTxid(String txid) {
    this.txid = txid;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public BigInteger getGasLimit() {
    return gasLimit;
  }

  public void setGasLimit(BigInteger gasLimit) {
    this.gasLimit = gasLimit;
  }

  public BigInteger getGasPrice() {
    return gasPrice;
  }

  public void setGasPrice(BigInteger gasPrice) {
    this.gasPrice = gasPrice;
  }

  public String getTxBizNumber() {
    return txBizNumber;
  }

  public void setHeight(long height) {
    this.height = height;
  }

  public long getHeight() {
    return height;
  }
}
