package tron.wallet.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Deposit {
  private String txid;
  private String blockHash;
  private Long blockHeight;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date time;

  private BigDecimal amount;
  private String address;
  private int status = 0; // 1 success, 0 failed
}
