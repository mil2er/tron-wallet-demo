package tron.wallet.entity;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;

@Slf4j
@Data
public class WatcherSetting {
    // 从配置文件初始化区块高度
    private String initBlockHeight = "latest";
    // 默认同步间隔5秒
    private Long interval = 5000L;
    // 区块确认数，区块链基本都需要在交易执行成功后再经过多个区块才能最终确认成功。
    private int confirmation = 1;
    // 每次重放检查的区块数
    private int step = 5;

     public Long getInitBlockHeight(){
        try{
            long l = Long.parseLong(initBlockHeight);
            return l;
        }catch(Throwable ex){
            String msg = String.format("%d parse error: %s", initBlockHeight, ex.getMessage());
            log.warn(msg);
        }
        return 0L;
     }
}
