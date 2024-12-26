package tron.wallet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.web3j.abi.datatypes.Type;
import org.web3j.utils.Convert;

import java.util.List;

import tron.wallet.entity.Coin;
import tron.wallet.entity.Contract;
import tron.wallet.entity.Deposit;
import tron.wallet.util.EthUtil;

public class Transaction {

  private String id; // 交易唯一ID
  private String coin; // 如 TRON_USDT
  private String display_code; // 如 USDT
  private String description; // 币种名称全称
  private int decimal; // 币种精度
  private String address; // 充值目标地址

  @JsonProperty(value = "source_address")
  private String sourceAddress; // 源地址

  private String side; // enum(deposit / withdraw)
  private String amount; // 交易金额数值 (注意这里的数值包含小数位, 例如BTC的decimal为8位，则这里的100000000代表1个BTC)

  @JsonProperty(value = "abs_amount")
  private String absAmount; // 交易金额绝对值 (绝对值, 交易1.5个BTC，这里就是1.5)

  @JsonProperty(value = "txid")
  private String txId;

  @JsonProperty(value = "vout_n")
  private int voutN; // 对于一笔交易可以有多个入账地址的公链，这个表示这笔交易在公链Transaction 的 Index

  @JsonProperty(value = "request_id")
  private String requestId; // 提现请求的请求Id

  private String status; // enum(success / failed / pending)

  @JsonProperty(value = "abs_cobo_fee")
  private String absCoboFee;

  @JsonProperty(value = "created_time")
  private long createdTime;

  @JsonProperty(value = "last_time")
  private long lastTime; // 交易成功(失败)时间

  @JsonProperty(value = "confirmed_num")
  private int confirmedNum; // 币种确认数

  @JsonProperty(value = "tx_detail")
  private TxDetail txDetail;

  @JsonProperty(value = "source_address_detail")
  private String sourceAddressDetail; // 逗号分割，所有来源地址

  private String memo; // 对于部分memo或tag币种, 返回memo或tag字符串

  @JsonProperty(value = "confirming_threshold")
  private int confirmingThreshold; // 币种确认数阈值（有可能动态变化）

  @JsonProperty(value = "fee_coin")
  private String feeCoin; // 交易费币种代号(Cobo内部代号, 每个币种唯一)

  @JsonProperty(value = "fee_amount")
  private String feeAmount; // 交易费数值 (注意这里的数值包含小数位, 例如BTC的decimal为8位，则这里的100000000实际为1个BTC)

  @JsonProperty(value = "fee_decimal")
  private int feeDecimal; // 交易费币种小数点位数

  private String type = "external"; // external表示上链交易；internal表示 Loop 交易(即 cobo会员之间内部划转不上公链)

  @JsonProperty(value = "waiting_audit")
  private boolean waitingAudit;

  public Transaction(Deposit d, Coin coin) {
    this.absAmount = d.getAmount().toString();
    this.amount = d.getAmount().toString();
    this.address = d.getAddress(); // 此为tx to地址
    this.coin = coin.getName();
    this.description = coin.getName();
    this.display_code = coin.getUnit();
    this.id = coin.getName() + "-" + d.getTxid(); // instinct约定 id = coin-txid
    this.txId = d.getTxid();
    this.side = "deposit";
    this.status = d.getStatus() == 1 ? "success" : "failed";
  }

  public Transaction() {}

  // 合约交易
  public Transaction(
      org.web3j.protocol.core.methods.response.Transaction etx,
      Coin coin,
      List<Type> params,
      Contract contract) {
    if (null == etx) {
      new Transaction();
      return;
    }
    this.coin = coin.getName(); // ETH_USDT
    this.description = coin.getName();
    this.display_code = coin.getUnit(); // USDT

    this.id = coin.getName() + "-" + etx.getHash();
    this.txId = etx.getHash();
    // 充币地址
    this.address = params.get(0).getValue().toString();
    this.amount =
        EthUtil.fromWei(params.get(1).getValue().toString(), contract.getUnit()).toString();
    this.absAmount = amount;
    this.sourceAddress = etx.getFrom();
    //    this.side = side;
    this.voutN = etx.getTransactionIndex().intValue();
  }

  // 主链币
  public Transaction(org.web3j.protocol.core.methods.response.Transaction etx, Coin coin) {
    if (null == etx) {
      new Transaction();
      return;
    }
    this.coin = coin.getName(); // ETH_USDT
    this.description = coin.getName();
    this.display_code = coin.getUnit(); // USDT

    this.id = coin.getName() + "-" + etx.getHash(); //
    this.txId = etx.getHash();
    this.amount = Convert.fromWei(etx.getValue().toString(), Convert.Unit.ETHER).toString();
    this.absAmount = amount;

    this.address = etx.getTo();

    this.sourceAddress = etx.getFrom();
    //    this.side = side;
    this.voutN = etx.getTransactionIndex().intValue();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCoin() {
    return coin;
  }

  public void setCoin(String coin) {
    this.coin = coin;
  }

  public String getDisplay_code() {
    return display_code;
  }

  public void setDisplay_code(String display_code) {
    this.display_code = display_code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getDecimal() {
    return decimal;
  }

  public void setDecimal(int decimal) {
    this.decimal = decimal;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSourceAddress() {
    return sourceAddress;
  }

  public void setSourceAddress(String sourceAddress) {
    this.sourceAddress = sourceAddress;
  }

  public String getSide() {
    return side;
  }

  public void setSide(String side) {
    this.side = side;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getAbsAmount() {
    return absAmount;
  }

  public void setAbsAmount(String absAmount) {
    this.absAmount = absAmount;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public int getVoutN() {
    return voutN;
  }

  public void setVoutN(int voutN) {
    this.voutN = voutN;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAbsCoboFee() {
    return absCoboFee;
  }

  public void setAbsCoboFee(String absCoboFee) {
    this.absCoboFee = absCoboFee;
  }

  public long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
  }

  public long getLastTime() {
    return lastTime;
  }

  public void setLastTime(long lastTime) {
    this.lastTime = lastTime;
  }

  public int getConfirmedNum() {
    return confirmedNum;
  }

  public void setConfirmedNum(int confirmedNum) {
    this.confirmedNum = confirmedNum;
  }

  public TxDetail getTxDetail() {
    return txDetail;
  }

  public void setTxDetail(TxDetail txDetail) {
    this.txDetail = txDetail;
  }

  public String getSourceAddressDetail() {
    return sourceAddressDetail;
  }

  public void setSourceAddressDetail(String sourceAddressDetail) {
    this.sourceAddressDetail = sourceAddressDetail;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public int getConfirmingThreshold() {
    return confirmingThreshold;
  }

  public void setConfirmingThreshold(int confirmingThreshold) {
    this.confirmingThreshold = confirmingThreshold;
  }

  public String getFeeCoin() {
    return feeCoin;
  }

  public void setFeeCoin(String feeCoin) {
    this.feeCoin = feeCoin;
  }

  public String getFeeAmount() {
    return feeAmount;
  }

  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  public int getFeeDecimal() {
    return feeDecimal;
  }

  public void setFeeDecimal(int feeDecimal) {
    this.feeDecimal = feeDecimal;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isWaitingAudit() {
    return waitingAudit;
  }

  public void setWaitingAudit(boolean waitingAudit) {
    this.waitingAudit = waitingAudit;
  }

  @Override
  public String toString() {
    return "Transaction{"
        + "id='"
        + id
        + '\''
        + ", coin='"
        + coin
        + '\''
        + ", display_code='"
        + display_code
        + '\''
        + ", description='"
        + description
        + '\''
        + ", decimal="
        + decimal
        + ", address='"
        + address
        + '\''
        + ", source_address='"
        + sourceAddress
        + '\''
        + ", side='"
        + side
        + '\''
        + ", amount='"
        + amount
        + '\''
        + ", abs_amount='"
        + absAmount
        + '\''
        + ", txid='"
        + txId
        + '\''
        + ", vout_n="
        + voutN
        + ", request_id='"
        + requestId
        + '\''
        + ", status='"
        + status
        + '\''
        + ", abs_cobo_fee='"
        + absCoboFee
        + '\''
        + ", created_time="
        + createdTime
        + ", last_time="
        + lastTime
        + ", confirmed_num="
        + confirmedNum
        + ", tx_detail="
        + txDetail
        + ", source_address_detail='"
        + sourceAddressDetail
        + '\''
        + ", memo='"
        + memo
        + '\''
        + ", confirming_threshold="
        + confirmingThreshold
        + ", fee_coin='"
        + feeCoin
        + '\''
        + ", fee_amount='"
        + feeAmount
        + '\''
        + ", fee_decimal="
        + feeDecimal
        + ", type='"
        + type
        + '\''
        + ", waiting_audit="
        + waitingAudit
        + '}';
  }

  public static class TxDetail {
    @JsonProperty(value = "txid")
    private String txId;

    private int blocknum;
    private String blockhash;
    private long fee;
    private long actualgas;
    private long gasprice;
    private String hexstr;

    public String getTxId() {
      return txId;
    }

    public void setTxId(String txId) {
      this.txId = txId;
    }

    public int getBlocknum() {
      return blocknum;
    }

    public void setBlocknum(int blocknum) {
      this.blocknum = blocknum;
    }

    public String getBlockhash() {
      return blockhash;
    }

    public void setBlockhash(String blockhash) {
      this.blockhash = blockhash;
    }

    public long getFee() {
      return fee;
    }

    public void setFee(long fee) {
      this.fee = fee;
    }

    public long getActualgas() {
      return actualgas;
    }

    public void setActualgas(long actualgas) {
      this.actualgas = actualgas;
    }

    public long getGasprice() {
      return gasprice;
    }

    public void setGasprice(long gasprice) {
      this.gasprice = gasprice;
    }

    public String getHexstr() {
      return hexstr;
    }

    public void setHexstr(String hexstr) {
      this.hexstr = hexstr;
    }

    @Override
    public String toString() {
      return "TxDetail{"
          + "txid='"
          + txId
          + '\''
          + ", blocknum="
          + blocknum
          + ", blockhash='"
          + blockhash
          + '\''
          + ", fee="
          + fee
          + ", actualgas="
          + actualgas
          + ", gasprice="
          + gasprice
          + ", hexstr='"
          + hexstr
          + '\''
          + '}';
    }
  }
}
