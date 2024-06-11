package last.demo.OAuth.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate; //RedisService 클래스에 redisTemplate을 주입받는 필드와 생성자를 추가

    // RefreshToken을 Redis에 저장
    public void saveRefreshToken(String refreshToken, Long userUUID) {
        redisTemplate.opsForValue().set(refreshToken, userUUID, Duration.ofDays(14));

        /*  14 의미

         해당 데이터의 만료 시간을 14일로 설정합니다.
         이렇게 설정된 만료 시간 동안에는 Redis에서 해당 데이터를 조회할 수 있습니다.
         만료 시간이 지나면 Redis에서 해당 데이터가 자동으로 삭제됩니다.
         ------------------------------------------------------------------------------------------------------------
         */

        /* refreshToken이 redis에서 key값으로 저장되면 좋은 이점 3가지

        조회 효율성: refreshToken은 유일한 값이며, 이를 key로 사용하면 해당 refreshToken을 이용하여 userId를 빠르게 조회할 수 있습니다.
                   Redis는 key를 기반으로 데이터를 검색하기 때문에 이런 경우에 매우 효율적입니다.

        중복 처리: refreshToken을 key로 사용하면 Redis에서 이미 해당 refreshToken에 대한 userId가 존재하는지 여부를 간단히 확인할 수 있습니다.
                 만약 이미 해당 refreshToken이 존재한다면 새로운 userId를 저장하는 것이 아니라 기존의 userId를 업데이트할 수 있습니다.

        갱신: refreshToken은 보통 일정 시간이 지나면 갱신됩니다. 이때 기존 refreshToken을 key로 사용하면 기존의 userId를 그대로 유지하면서
             새로운 refreshToken을 저장할 수 있습니다. 이러한 갱신 과정이 간편해집니다.


         */
    }

    // RefreshToken을 이용해서 Redis에서 userId 조회
    public Long getUserIdFromRefreshToken(String refreshToken) {
        Object value = redisTemplate.opsForValue().get(refreshToken);
        // value가 정수형으로 변환이 가능한지 확인하고, 가능하면 정수형으로 변환하여 반환
        System.out.println(value);
        if (value instanceof Long) {
            return (Long) value;

        } else if (value instanceof byte[]) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) value);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                return (Long) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // 역직렬화 오류 발생 시 예외 처리
                e.printStackTrace();
            }
        }

        // 정수형으로 변환이 불가능한 경우에는 null 반환
        return null;
    }


    // RefreshToken을 Redis에서 삭제
    public void deleteRefreshToken(String jwtrefreshToken) {
        redisTemplate.delete(jwtrefreshToken); // set을 통해서 값을 저장할 때 사용했던, key값인 refreshToken을 이용해서 값userID와 모두 삭제
    }

}
