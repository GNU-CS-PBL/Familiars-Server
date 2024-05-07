package last.demo.OAuth.entity;

import jakarta.persistence.*;
import last.demo.OAuth.dto.UserDto;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder // ------------------------------------------빌더 패턴을 사용하기 위한 어노테이션 추가
public class UserEntity { //사용자 테이블

    // SNS 로그인 완료시 생성되는 변수들
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 새로운 행이 생성될때마다, 고유의 기본키가 자동생성
    private Long userId; // 사용자 고유 UID

    @Column
    private String provider; // OAuth2 제공자 (엑세스 토큰을 발급한 SNS 기관 정보)

    @Column
    private Long providerID; //OAuth2 제공자 고유 번호 {SNS 플랫폼 안에서의 사용자 UID(sub 변수에서 추출)}

    @Column
    private Timestamp createdDate; // 사용자 생성시간

    @Column
    private Timestamp lastModifiedDate; // 사용자 수정시간

    // 회원 가입 완료 이후에 생성되는 변수들
    @Column
    private Boolean marketingAgree; // 사용자 마케팅 수신 동의 여부 (false = 미동의 상태)

    @Column(length = 20)
    private String name; // 사용자 이름

    @Lob //큰 객체(LOB)를 지정하는 데 사용
    @Column(columnDefinition = "TEXT")
    private String image; //사용자 프로필 사진

    @Column
    private String birth; // 사용자 생일

    @Column
    private Boolean field2; // 방 가입 여부 (false = 미가입 상태)
    


    // Dto -> Entity 변환
    public static UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUserId(userDto.getUserId() != null ? userDto.getUserId() : null);
        userEntity.setProvider(userDto.getProvider() != null ? userDto.getProvider() : null);
        userEntity.setProviderID(userDto.getProviderID() != 0 ? userDto.getProviderID() : 0L);
        userEntity.setCreatedDate(userDto.getCreatedDate());
        userEntity.setLastModifiedDate(userDto.getLastModifiedDate());
        userEntity.setMarketingAgree(userDto.getMarketingAgree() != null ? userDto.getMarketingAgree() : false);
        userEntity.setName(userDto.getName() != null ? userDto.getName() : null);
        userEntity.setImage(userDto.getImage() != null ? userDto.getImage() : null);
        userEntity.setBirth(userDto.getBirth() != null ? userDto.getBirth() : null);
        userEntity.setField2(userDto.getField2() != null ? userDto.getField2() : false);

        return userEntity;
    }

}
