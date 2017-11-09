package com.qminder.instadownloader.repository;

import com.qminder.instadownloader.domain.RealTimeUserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<RealTimeUserDetail, Long> {

    RealTimeUserDetail findUserDetailByUserName(String userName);
}
