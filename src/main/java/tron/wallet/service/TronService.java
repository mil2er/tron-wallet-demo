package tron.wallet.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import org.tron.common.utils.ByteArray;
import org.tron.sdk.TronApi;
import org.tron.utils.TronUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Convert;

import tron.wallet.entity.Coin;
import tron.wallet.entity.Contract;
import tron.wallet.entity.Payment;
import tron.wallet.util.AES;
import tron.wallet.util.Wallet;
import tron.wallet.util.HttpUtils;

@Slf4j
@Component
// @Service
public class TronService {

  @Autowired private Coin coin;
  @Autowired private Contract contract;
  @Autowired private TronApi tronApi;

  /**
   * 获取最新区块高度
   */
  public long getHeight() throws IOException{
    return tronApi.getHeight();
  }

  /**
   * 创建账户
   * 
   * @param account
   * @param password
   * @return 经过password 加密的私钥，json 结构字符串
   * @throws Exception
   */
  public String createNewWallet(String account, String password) throws Exception {
    // "address", "hexAddress", "privateKey"
    Map<String, String> r = TronApi.createAddress();
    String hashSalt = AES.HashAndSalt(password.getBytes(StandardCharsets.UTF_8));
    String encPriKey = AES.encryptAes(r.get("privateKey"), hashSalt);
    r.put("privateKey", encPriKey);

    return JSON.toJSONString(r);
  }

  /**
   * 查询TRX 余额
   * 
   * @param address
   * @return
   * @throws IOException
   */
  public BigDecimal getBalance(String address) throws IOException {
    String balanceStr = tronApi.getAccountBalance(address);
    JSONObject balance = JSON.parseObject(balanceStr);
    if (null != balance && balance.containsKey("balance")) {
      return new BigDecimal(balance.getLong("balance"));
    }
    return new BigDecimal(-1);
  }

  /**
   * 查询USDT（合约）余额
   * 
   * @param address
   * @return
   * @throws IOException
   */
  public BigDecimal getBalanceOf(String address) throws IOException {
    // String tAddr = TronUtils.toHexAddress(contract.getAddress());
    // String tAddr = TronUtils.toViewAddress(contract.getAddress());
    // String balanceStr = tronApi.getBalanceOf(address, tAddr);
    String balanceStr = tronApi.getBalanceOf(address, contract.getAddress());
    JSONObject balance = JSON.parseObject(balanceStr);
    if (null != balance && balance.containsKey("constant_result")) {
      String balanceHex = balance.getJSONArray("constant_result").getString(0);
      return new BigDecimal(new BigInteger(balanceHex, 16));
    }
    return new BigDecimal(-1);
  }

  /**
   * 发起TRX 资产转账交易
   * 
   * @param payment
   * @return 交易哈希
   * @throws Throwable
   */
  public String transferTrx(Payment payment) throws Throwable{
    log.info("transferTrx payment={}", JSON.toJSONString(payment));
    String txHash =
          tronApi.sendTrx(
              // payment.getAmount().scaleByPowerOfTen(6).toBigInteger(),
              payment.getAmount().toBigInteger(),
              payment.getTo(),
              payment.getWallet().getPrivateKey());
    log.info("transferTrx txHash={}", txHash);
    return txHash;
  }

  /**
   * 发起USDT (合约) 转账交易
   * contract 从配置文件初始化的
   * 
   * @param payment
   * @return
   * @throws Throwable
   */
  public String transferTrc20(Payment payment) throws Throwable{
    log.info("transferTrc20: payment={}", JSON.toJSONString(payment));
    String txHash =
          tronApi.sendTrc20(
              contract.getAddress(),
              new Uint256(
                  payment
                      .getAmount()
                      // .scaleByPowerOfTen(Integer.parseInt(contract.getDecimals()))
                      .toBigInteger()),
              payment.getTo(),
              payment.getWallet().getPrivateKey());
    log.info("transferTrc20 txHash={}", txHash);
    return txHash;
  }

  public Boolean isTransactionSuccess(String txid) throws IOException {
    //    EthTransaction transaction = web3j.ethGetTransactionByHash(txid).send();
    String transactionInfoStr = tronApi.getTransactionInfo(txid);
    log.info("getTransactionInfo:{}", transactionInfoStr);
    JSONObject transactionInfo = JSON.parseObject(transactionInfoStr);
    if (null == transactionInfo && !(transactionInfo.containsKey("receipt"))) {
      log.error("getTransactionInfo异常{}", transactionInfoStr);
      return false;
    }
    if (transactionInfo.getJSONObject("receipt").getString("result").equalsIgnoreCase("SUCCESS"))
      return true;
    return false;
  }

  /**
   * 获取交易数据
   * 
   * @param txid
   * @return 交易数据，json 结构字符串
   * @throws IOException
   */
  public String getTransaction(String txid) throws IOException {
    return tronApi.getTransaction(txid);
  }

  /**
   * 获取交易回执信息
   * 
   * @param txid
   * @return 交易回执信息，json 结构字符串
   * @throws IOException
   */
  public String getTransactionInfo(String txid) throws IOException {
    return tronApi.getTransactionInfo(txid);
  }

  public String estimateEnergy(Payment payment) throws IOException {
    String ownerAddress = TronUtils.getAddressByPrivateKey(payment.getWallet().getPrivateKey());
    Address toAddress = new Address(TronUtils.toHexAddress(payment.getTo()).substring(2));
    JSONObject txObj = new JSONObject();
    if (contract.getAddress().startsWith("T")) txObj.put("visible", true);
    txObj.put("owner_address", ownerAddress);
    txObj.put("contract_address", contract.getAddress());
    txObj.put("function_selector", "transfer(address,uint256)");
    List<Type> params = new ArrayList<>();
    params.add(toAddress);
    params.add(new Uint256(payment.getAmount().toBigInteger()));
    String parameter = FunctionEncoder.encodeConstructor(params);
    txObj.put("parameter", parameter);
    log.warn("txObj: {}", txObj.toString());

    String tronUrl = TronApi.tronUrl;
    if(tronUrl.endsWith("/")){
			tronUrl = tronUrl.substring(0,tronUrl.length() - 1);
		}

		String url = tronUrl + "/wallet/estimateenergy";
		String result = HttpUtils.postJson(url, txObj.toString());
    log.warn("parameter: {}", parameter);
    return result;
  }

  public String getEnergyPrices() throws IOException {
    String tronUrl = TronApi.tronUrl;
    if(tronUrl.endsWith("/")){
			tronUrl = tronUrl.substring(0,tronUrl.length() - 1);
		}
		String url = tronUrl + "/wallet/getenergyprices";
		String result = HttpUtils.request(url, HttpMethod.GET);
    return result;
  }

  public String getBandwidthPrices() throws IOException {
    String tronUrl = TronApi.tronUrl;
    if(tronUrl.endsWith("/")){
			tronUrl = tronUrl.substring(0,tronUrl.length() - 1);
		}
		String url = tronUrl + "/wallet/getbandwidthprices";
		String result = HttpUtils.request(url, HttpMethod.GET);
    return result;
  }
}

