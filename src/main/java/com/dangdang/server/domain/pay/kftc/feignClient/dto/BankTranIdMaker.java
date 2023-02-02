package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import java.util.Random;

public class BankTranIdMaker {

  protected static String makeBankTranId() {
    String tempPassword = "";

    for (int i = 0; i < 9; i++) {
      Random random = new Random();
      int rand = random.nextInt(62);
      if (rand < 10) {
        tempPassword += rand;
      } else if (rand < 35) {
        tempPassword += (char) (rand + 55);
      } else {
        i--;
      }
    }
    return tempPassword;
  }
}
