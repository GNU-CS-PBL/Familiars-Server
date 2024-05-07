package last.demo.OAuth.repository;

import last.demo.OAuth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByProviderID(long providerId); // 카카오 로그인 관련

    boolean existsByName(String name);
}
