package tron.wallet.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import tron.wallet.entity.ApiResponse;
import tron.wallet.entity.Coin;
import tron.wallet.entity.MessageResult;
import tron.wallet.entity.Payment;
import tron.wallet.util.Wallet;
import tron.wallet.service.TronService;

@Slf4j
@RestController
@RequestMapping("/demo")
public class TronController {
  @Autowired private Coin coin;
  @Autowired private TronService tronService;

  @GetMapping("height")
  public MessageResult getHeight() {
    try {
      long rpcBlockNumber = tronService.getHeight();
      MessageResult result = new MessageResult(0, "success");
      result.setData(rpcBlockNumber);
      return result;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "查询失败,error:" + e.getMessage());
    }
  }

  @GetMapping("address/{account}")
  public MessageResult getNewAddress(@PathVariable String account) {
    log.info("create new account={}", account);
    try {
      String address = tronService.createNewWallet(account, "a12345678!");
      MessageResult result = new MessageResult(0, "success");
      result.setData(address);
      return result;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "rpc error:" + e.getMessage());
    }
  }

  /**
   * 查询TRX 余额
   *
   * @param address
   * @return
   */
  @GetMapping("balance/{address}")
  public MessageResult addressBalance(@PathVariable String address) {
    try {
      BigDecimal balance = tronService.getBalance(address);
      MessageResult result = new MessageResult(0, "success");
      result.setData(balance);
      return result;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "查询失败，error:" + e.getMessage());
    }
  }

  /**
   * 查询TRC20 余额
   *
   * @param address
   * @return
   */
  @GetMapping("balanceOf/{address}")
  public MessageResult addressBalanceOf(@PathVariable String address) {
    try {
      BigDecimal balance = tronService.getBalanceOf(address);
      MessageResult result = new MessageResult(0, "success");
      result.setData(balance);
      return result;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "查询失败，error:" + e.getMessage());
    }
  }

  /**
   * TRX 转账
   * 
   * @param address
   * @param amount
   * @param fee
   * @return
   */
  @PostMapping("transfer/{toAddress}/{amount}")
  public MessageResult transfer(@PathVariable String toAddress, @PathVariable BigDecimal amount) {
    try {
      Wallet wallet = Wallet.getWallet(coin);
      Payment pay = Payment.builder().wallet(wallet).to(toAddress).amount(amount).build();
      String txHash =
          tronService.transferTrx(pay);
      MessageResult ret = new MessageResult(0, "success");
      ret.setData(txHash);
      log.info("response: {}", ret);
      return ret;
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "error: " + e.getMessage());
    }
  }

  /**
   * USDT 转账
   * 
   * @param toAddress
   * @param amount
   * @return
   */
  @PostMapping("transferUsdt/{toAddress}/{amount}")
  public MessageResult transferUsdt(@PathVariable String toAddress, @PathVariable BigDecimal amount) {
    try {
      Wallet wallet = Wallet.getWallet(coin);
      Payment pay = Payment.builder().wallet(wallet).to(toAddress).amount(amount).build();
      String txHash =
          tronService.transferTrc20(pay);
      MessageResult ret = new MessageResult(0, "success");
      ret.setData(txHash);
      log.info("response: {}", ret);
      return ret;
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "error: " + e.getMessage());
    }
  }

  /**
   * 查询交易数据
   * curl -XGET http://127.0.0.1:7004/tron/tx/522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118
   * {
   *     "ret":[{
   *         "contractRet":"SUCCESS"
   *     }],
   *     "signature":[
   *         "c31a6fefba2285566fcfe08bded120590524948876f89493a935be101a131204058f714d1914549af71c1c8f8059741c1910137b88b6a9c3c0f127e96027c46c01"
   *     ],
   *     "txID":"522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118",
   *     "raw_data":{
   *         "contract":[{
   *             "parameter":{
   *                 "value":{
   *                     "amount":1000000,
   *                     "owner_address":"413cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
   *                     "to_address":"41e33944686502898789d865324356a0b33ce7b028"
   *                 },
   *                 "type_url":"type.googleapis.com/protocol.TransferContract"
   *             },
   *             "type":"TransferContract"
   *         }],
   *         "ref_block_bytes":"ea26",
   *         "ref_block_hash":"9af21949d5ca334a",
   *         "expiration":1734752610000,
   *         "timestamp":1734752552027
   *     },
   *     "raw_data_hex":"..."
   * }
   * 
   * @param txHash
   * @return
   */
  @GetMapping("tx/{txHash}")
  public String getTx(@PathVariable String txHash) {
    try {
      return tronService.getTransaction(txHash);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "查询失败，error:" + e.getMessage()).toString();
    }
  }

  /**
   * 查询交易回执数据
   * 
   * 注意: TRX 转账交易和合约交易回执数据结构不同！
   * 
   * TRX 转账交易回执:
   * curl -XGET http://127.0.0.1:7004/tron/txInfo/522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118
   * {
   *     "id": "522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118",
   *     "fee": 1100000,
   *     "blockNumber": 52947514,
   *     "blockTimeStamp": 1734752556000,
   *     "contractResult": [""],
   *     "receipt": {
   *         "net_fee": 100000
   *     }
   * }
   * 
   * USDT 合约交易回执:
   * curl -XGET http://127.0.0.1:7004/tron/txInfo/d6161e2819feaa74ee44113ccb525f79cc2d9fa1f290554804988d1223d2e3b6
   * {
   *     "id": "d6161e2819feaa74ee44113ccb525f79cc2d9fa1f290554804988d1223d2e3b6",
   *     "blockNumber": 52953811,
   *     "blockTimeStamp": 1734772239000,
   *     "contractResult": [
   *         "0000000000000000000000000000000000000000000000000000000000000000"
   *     ],
   *     "contract_address": "41eca9bc828a3005b9a3b909f2cc5c2a54794de05f",
   *     "receipt": {
   *         "origin_energy_usage": 29650,
   *         "energy_usage_total": 29650,
   *         "net_usage": 345,
   *         "result": "SUCCESS"
   *     },
   *     "log": [{
   *         "address": "eca9bc828a3005b9a3b909f2cc5c2a54794de05f",
   *         "topics": [
   *             "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
   *             "0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
   *             "000000000000000000000000e33944686502898789d865324356a0b33ce7b028"
   *         ],
   *         "data": "00000000000000000000000000000000000000000000000000000000000f4240"
   *     }]
   * }
   *
   * @param txHash
   * @return
   */
  @GetMapping("txInfo/{txHash}")
  public String getTxInfo(@PathVariable String txHash) {
    try {
      return tronService.getTransactionInfo(txHash);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return MessageResult.error(500, "查询失败，error:" + e.getMessage()).toString();
    }
  }

}
