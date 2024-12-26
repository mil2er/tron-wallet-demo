package tron.wallet.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import tron.wallet.util.EthUtil;

@Data
public class Contract {
    //合约精度
    private String decimals;
    //合约地址
    private String address;
    private BigInteger gasLimit;
    private String eventTopic0;
    public EthUtil.Unit getUnit(){
        if(StringUtils.isEmpty(decimals))
            return EthUtil.Unit.ETHER;
        return EthUtil.Unit.fromString(decimals);
    }
}
