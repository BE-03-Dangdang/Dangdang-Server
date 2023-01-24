package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionAccountRepository extends JpaRepository<ConnectionAccount, Long> {

  List<ConnectionAccount> findByPayMemberId(Long payMemberId);
}
