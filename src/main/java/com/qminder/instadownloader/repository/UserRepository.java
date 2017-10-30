package com.qminder.instadownloader.repository;

import com.qminder.instadownloader.domain.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetail, Long> {
}
