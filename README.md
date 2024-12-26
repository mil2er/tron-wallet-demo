# Tron Wallet Demo

TRON network, Wallet, Demo, SpringBoot

本项目为波场调用示例代码，目的是用一个可编译运行的服务程序演示波场网络相关功能接口的调用。

本文档将主要包含四个部分：
* 对本示例项目功能和代码的简要说明；  
* 对Java 语言实现版本的波场SDK 的简要说明；
* 对Http 接口的说明，以便于更好的理解波场SDK 各个语言版本的实现；
* 网络上搜索到的相关资源以及参考资料；

# 本示例项目

## 项目构建及启动

* JDK 版本  
  OpenJDK version "1.8.0_432"  
* 安装需要的jar 包
  ```shell
  mvn install:install-file \
        -Dfile=./jars/tron-core-1.0-SNAPSHOT.jar \
        -DgroupId=org.tron \
        -DartifactId=tron-core \
        -Dversion=1.0-SNAPSHOT \
        -Dpackaging=jar \
        -DgeneratePom=true \
        -DcreateChecksum=true
  ```
* 编译
  ```shell
  mvn install
  ```
* 启动
  ```shell
  java -jar ./tron-wallet-0.0.0-SNAPSHOT.jar --spring.profiles.active=local
  ```

测试地址: TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw

配置文件: [application-local.yml](./src/main/resources/application-local.yml)

## 功能简介

* 查询最新区块接口:  
  ```shell
      curl -XGET http:/127.0.0.1:9000/demo/height
  ```
* 账户创建接口:  
  ```shell
      # curl -XGET http:/127.0.0.1:9000/demo/address/[request_id]  
      curl -XGET http:/127.0.0.1:9000/demo/address/000000  
      # 加密后的私钥JSON 字符串
      response:
          {
              "code":0,
              "message":"success",
              "data":"{\"privateKey\":\"XaZ8Ad1m1ZI0rnpvCJKC8fCtYNF73Q615kIUGL7UUGdZIcO08WfRN0xZrP68h9Ai+3uMGgZyZVwK0FKAVLPRKMGd/ulFLs86a+MU1HWeCqE=\",\"address\":\"TVdVmHD96xXDNsrTrSBjM6Zip9zE7bWgtH\",\"hexAddress\":\"41d7a8306365a75ce33d9e06248de298b950f37ad5\"}"
          }
  ```
* TRX 余额查询接口:  
  ```shell
      # curl -XGET http://127.0.0.1:9000/demo/balance/[address]
      curl -XGET http://127.0.0.1:9000/demo/balance/TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw
      response: {"code":0,"message":"success","data":123000000}
  ```
* USDT 余额查询接口:
  ```shell
      # curl -XGET http://127.0.0.1:9000/demo/balanceOf/[address]
      curl -XGET http://127.0.0.1:9000/demo/balanceOf/TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw
      response: {"code":0,"message":"success","data":234000000}
  ```
* TRX 转账交易请求接口:  
  ```shell
      # 测试网 ( Nile ) TRX https://nile.tronscan.org/#/token/0/transfers
      # Decimal: 6
      # curl -XPOST http://127.0.0.1:9000/demo/transfer/[toAddress]/[amount]
      curl -XPOST http://127.0.0.1:9000/demo/transfer/TWgezbnYkrQrWJY4tv2vbftyvUwaM9r7s1/1000000
  ```
* USDT 转账交易请求接口:
  ```shell
      # 测试网 ( Nile ) USDT https://nile.tronscan.org/#/token20/TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf
      # Decimal: 6
      # curl -XPOST http://127.0.0.1:9000/demo/transferUsdt/[toAddress]/[amount]
      # 验证，查询USDT 余额:
      # curl -XGET http://127.0.0.1:9000/demo/balanceOf/TWgezbnYkrQrWJY4tv2vbftyvUwaM9r7s1
      curl -XPOST http://127.0.0.1:9000/demo/transferUsdt/TWgezbnYkrQrWJY4tv2vbftyvUwaM9r7s1/1000000
  ```
* 查询交易数据:
  ```shell
      curl -XGET http://127.0.0.1:9000/demo/tx/522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118
      response:
          {
              "ret":[{
                  "contractRet":"SUCCESS"
              }],
              "signature":[
                  "c31a6fefba2285566fcfe08bded120590524948876f89493a935be101a131204058f714d1914549af71c1c8f8059741c1910137b88b6a9c3c0f127e96027c46c01"
              ],
              "txID":"522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118",
              "raw_data":{
                  "contract":[{
                      "parameter":{
                          "value":{
                              "amount":1000000,
                              "owner_address":"413cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
                              "to_address":"41e33944686502898789d865324356a0b33ce7b028"
                         },
                         "type_url":"type.googleapis.com/protocol.TransferContract"
                      },
                      "type":"TransferContract"
                  }],
                  "ref_block_bytes":"ea26",
                  "ref_block_hash":"9af21949d5ca334a",
                  "expiration":1734752610000,
                  "timestamp":1734752552027
              },
              "raw_data_hex":"..."
          }
  ```
* 查询交易回执信息:
  ```shell
      注意: TRX 转账交易和合约交易回执数据结构不同！

      TRX 转账交易回执:

      curl -XGET http://127.0.0.1:9000/demo/txInfo/522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118
      response:
          {
              "id": "522d80315b8534816876d8d4996c6f818d13fe76c7a4780cd536fe130eb00118",
              "fee": 1100000,
              "blockNumber": 52947514,
              "blockTimeStamp": 1734752556000,
              "contractResult": [""],
              "receipt": {
                  "net_fee": 100000
              }
          }
    
      USDT 合约交易回执:

      curl -XGET http://127.0.0.1:9000/demo/txInfo/d6161e2819feaa74ee44113ccb525f79cc2d9fa1f290554804988d1223d2e3b6
      response:
          {
              "id": "d6161e2819feaa74ee44113ccb525f79cc2d9fa1f290554804988d1223d2e3b6",
              "blockNumber": 52953811,
              "blockTimeStamp": 1734772239000,
              "contractResult": [
                  "0000000000000000000000000000000000000000000000000000000000000000"
              ],
              "contract_address": "41eca9bc828a3005b9a3b909f2cc5c2a54794de05f",
              "receipt": {
                  "origin_energy_usage": 29650,
                  "energy_usage_total": 29650,
                  "net_usage": 345,
                  "result": "SUCCESS"
              },
              "log": [{
                  "address": "eca9bc828a3005b9a3b909f2cc5c2a54794de05f",
                  "topics": [
                      "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                      "0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
                      "000000000000000000000000e33944686502898789d865324356a0b33ce7b028"
                  ],
                  "data": "00000000000000000000000000000000000000000000000000000000000f4240"
              }]
          }
  ```

# Java 语言版本波场SDK 简要说明

* [TronService.java](./src/main/java/tron/wallet/service/TronService.java)  
  服务接口实现  

* [public long getHeight() throws IOException](./src/main/java/tron/wallet/service/TronService.java#L45)  
  获取最新区块高度  

* [public String createNewWallet(String account, String password) throws Exception](./src/main/java/tron/wallet/service/TronService.java#L57)  
  创建账户  

* [public BigDecimal getBalance(String address) throws IOException](./src/main/java/tron/wallet/service/TronService.java#L74)  
  查询TRX 余额

* [public BigDecimal getBalanceOf(String address) throws IOException](./src/main/java/tron/wallet/service/TronService.java#L90)  
  查询USDT（合约）余额  

* [public String transferTrx(Payment payment) throws Throwable](./src/main/java/tron/wallet/service/TronService.java#L110)  
  发起TRX 资产转账交易  

* [public String transferTrc20(Payment payment) throws Throwable](./src/main/java/tron/wallet/service/TronService.java#L130)  
  发起USDT (合约) 转账交易  

* [public String getTransaction(String txid) throws IOException](./src/main/java/tron/wallet/service/TronService.java#L167)  
  获取交易数据  

* [public String getTransactionInfo(String txid) throws IOException](./src/main/java/tron/wallet/service/TronService.java#L178)  
  获取交易回执信息  

# 波场Http 接口简要说明

## 查询最新的区块

* /wallet/getnowblock
  ```shell
  # request

  curl -XGET  https://test-tron.oxcoin.co/wallet/getnowblock

  # response

  {
      "blockID":"000000000326c641ee5f2819cdaccd67a616195ea214a4c352a4bfa47d61e1c8",
      "block_header":{
          "raw_data":{
              "number":52872769,
              "txTrieRoot":"0000000000000000000000000000000000000000000000000000000000000000",
              "witness_address":"4150d3765e4e670727ebac9d5b598f74b75a3d54a7",
              "parentHash":"000000000326c640639ec37b4f981df3dd768e926305bd0ddb2b91e759fa2fda",
              "version":30,
              "timestamp":1734518928000
          },
          "witness_signature":"1d2d3256479d3c813282e0f92d6b37e50ef9e3382a82d992e50d09cda059b2623a49b5134ba40fc976ee60b63bae8bd940dadff04c132094e7bc12c7e543dc1701"
      }
  }
  ```

## 查询TRX 余额

* /wallet/getaccountbalance
  ```shell
  # request

  curl -XPOST  https://test-tron.oxcoin.co/wallet/getaccountbalance -d \
      '{
          "account_identifier": {
              "address": "TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw"
          },
          "block_identifier": {
              "hash": "00000000032739f625c6e31781a6111d2762b9049cbb618a03f2955872678ee8",
              "number": 52902390
          },
          "visible": true
      }'

  # response

  {
      "balance": 123000000,
      "block_identifier": {
          "hash": "00000000032739acada418d6955232bdb1ec5bf734a783336e73485973ee9736",
          "number": 52902316
      }
  }
  ```

## 查询TRC20 余额

需要转换地址格式并进行ABI 编码，请参考后面的 [在线地址转换及ABI 编码工具](#在线地址转换及abi-编码工具)  

* /wallet/triggerconstantcontract
  ```shell
  # request

  # “owner_address” TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw
  # 测试账户地址（波场格式）
  #
  # “contract_address” TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf
  # 测试网（nile）的 USDT 合约地址
  #
  # “function_selector” balanceOf(address)
  # 查询USDT 余额的合约方法
  #
  # “parameter” 0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e
  # 转换为以太坊格式地址并进行ABI 编码的合约方法传入参数 

  curl -XPOST https://test-tron.oxcoin.co/wallet/triggerconstantcontract \
      --header 'accept: application/json' \
      --header 'content-type: application/json' -d \
      '{
          "owner_address": "TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw",
          "contract_address": "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf",
          "function_selector": "balanceOf(address)",
          "parameter": "0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
          "visible": true
      }'

  # response

  # "constant_result" 000000000000000000000000000000000000000000000000000000000df28e80
  # 查询结果，16进制字符串，可转换验证

  {
      "result": {
          "result":true
      },
      "energy_used":935,
      "constant_result": [
          "000000000000000000000000000000000000000000000000000000000df28e80"
      ],
      "transaction": {
          "ret":[{}],
          "visible":true,
          "txID":"137391bad5d906e3a4e94a6b8de5e31d208797b969faea55620789743dbed167",
          "raw_data": {
              "contract": [{
                  "parameter": {
                      "value": {
                          "data":"70a082310000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
                          "owner_address":"TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw",
                          "contract_address":"TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf"
                      },
                      "type_url":"type.googleapis.com/protocol.TriggerSmartContract"
                  },
                  "type":"TriggerSmartContract"
              }],
              "ref_block_bytes":"3e3c",
              "ref_block_hash":"d8673c35aba05b35",
              "expiration":1734615045000,
              "timestamp":1734614985851
          },
          "raw_data_hex":"..."
      }
  }
  ```

## TRX 转账交易

交易涉及到创建、签名和广播:  
* 创建交易  
* 签名后广播交易  

接口调用:  
* /wallet/createtransaction  
  创建交易  
  注意: 波场地址需转换为哈希地址  
  TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw: 413CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E  
  TWgezbnYkrQrWJY4tv2vbftyvUwaM9r7s1: 41E33944686502898789D865324356A0B33CE7B028
  ```shell
  curl -XPOST https://test-tron.oxcoin.co/wallet/createtransaction \
      --header 'accept: application/json' \
      --header 'content-type: application/json' -d \
      '{
          "owner_address": "413CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E",
          "to_address": "41E33944686502898789D865324356A0B33CE7B028",
          "amount": 1000000
      }'

  # response

  {
      "visible":false,
      "txID":"1b4eb0a5617c790a8e7cd62d6879e18f8de7e908286b5dca0040d618848f41bb",
      "raw_data":{
          "contract":[{
              "parameter":{
                  "value":{
                      "amount":1000000,
                      "owner_address":"413cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
                      "to_address":"41e33944686502898789d865324356a0b33ce7b028"
                  },
                  "type_url":"type.googleapis.com/protocol.TransferContract"
              },
              "type":"TransferContract"
          }],
          "ref_block_bytes":"62e5",
          "ref_block_hash":"c10c597f458d1b29",
          "expiration":1734849219000,
          "timestamp":1734849161019
      },
      "raw_data_hex":"..."
  }
  ```
* /wallet/broadcasthex  
  签名后广播交易  
  ```shell
  curl -XPOST https://test-tron.oxcoin.co/wallet/createtransaction \
      --header 'accept: application/json' \
      --header 'content-type: application/json' -d \
      '{
          "transaction": "[transaction hax 字符串]"
      }'

  # response

  {
      "result": {
          "txid": "..."
      }
      ...
  }
  ```

# 相关资源以及参考资料

## 测试网浏览器

可查询区块，交易，账户等链上信息。需注意配置调用的测试网API 要使用相应的测试网浏览器。  
**本项目统一使用 nile 测试网。**  

[https://nile.tronscan.org/#/](https://nile.tronscan.org/#/)  

## 水龙头

* 需要先加入[波场Discord服务器: TRON Developers&SRs](https://discord.gg/g3fVqxfz)，  
* 波场为了防止诈骗所以无法部署自己的USDT 测试合约，需要到[Discord 水龙头频道](https://discord.com/channels/491685925227724801/999575963920781382)领水（测试资产）:

发送消息即可:  

* 领取测试网 TRX:  
  **:> !nile TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw**  
* 领取测试网 USDT:  
  **:> !nile_usdt TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw**  

## 在线地址转换及ABI 编码工具

调用合约方法需要将地址参数转换为以太坊格式，并且需要进行ABI 编码。  
为了方便测试，使用在线工具直接转换并编码即可。  

* 在线地址转换  
  [https://tronscan.org/#/tools/code-converter/tron-ethereum-address](https://tronscan.org/#/tools/code-converter/tron-ethereum-address)  
  ```
  波场地址: TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw  
  转换之后  
  以太地址: 0x3CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E  
  ```

* 在线ABI 编码  
  [https://abi.hashex.org/](https://abi.hashex.org/)  
  ```
  Function ( your function ) : balanceOf  
  Argument ( Address ): 0x3CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E  
  Encoded data: 70a082310000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e  

  截取地址参数编码部分即可: 0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e  
  ```
  

