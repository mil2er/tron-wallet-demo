package tron.wallet.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import tron.wallet.entity.Coin;
import tron.wallet.entity.Contract;
import tron.wallet.entity.WatcherSetting;
import org.tron.sdk.TronApi;

/**
 * 资产配置参数
 */
@Slf4j
@Configuration
public class BlockchainConfig {

    @Bean
    @ConditionalOnProperty(name = "coin.name")
    @ConfigurationProperties(prefix = "coin")
    public Coin getCoin(){
        Coin coin = new Coin();
        // log.info("Coin config: coin.name={}; coin.unit={}", coin.getName(), coin.getUnit());
        return coin;
    }

    @Bean
    @ConditionalOnProperty(name = "contract.address")
    @ConfigurationProperties(prefix = "contract")
    public Contract getContract(){
        Contract contract = new Contract();
        return contract;
    }

    @Bean
    @ConfigurationProperties(prefix = "watcher")
    public WatcherSetting getWatcherSetting(){
        WatcherSetting setting = new WatcherSetting();
        // log.info("Watcher config: watcher.init-block-height={}", setting.getInitBlockHeight());
        return setting;
    }

    @Bean
    @ConditionalOnProperty(name = "coin.keystore-path")
    public TronApi tronApi(Coin coin) {
        TronApi api = new TronApi();
        TronApi.tronUrl = coin.getRpc();
        log.info("TronApi config: coin.rpc={}", TronApi.tronUrl);
        log.info("TronApi config: coin.name={}; coin.unit={}", coin.getName(), coin.getUnit());
        return api;
    }

}
