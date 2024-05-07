package last.demo.OAuth.dto;

import last.demo.OAuth.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp; // java.sql.Timestamp를 import

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class UserDto {

    private Long userId; //사용자 고유 UID
    private String provider;
    private Long providerID;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;
    private Boolean marketingAgree;
    private String name;
    private String image;
    private String birth;
    private Boolean field2;

    private String accessToken; // OAuth 제공자 엑세스 토큰


    // Entity -> Dto 변환
    public static UserDto toUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();

        userDto.setUserId(userEntity.getUserId() != null ? userEntity.getUserId() : 0L);
        userDto.setProvider(userEntity.getProvider() != null ? userEntity.getProvider() : "");
        userDto.setProviderID(userEntity.getProviderID() != 0 ? userEntity.getProviderID() : 0L);
        userDto.setCreatedDate(userEntity.getCreatedDate());
        userDto.setLastModifiedDate(userEntity.getLastModifiedDate());
        userDto.setMarketingAgree(userEntity.getMarketingAgree() != null ? userEntity.getMarketingAgree() : false);
        userDto.setName(userEntity.getName() != null ? userEntity.getName() : "");
        userDto.setImage(userEntity.getImage() != null ? userEntity.getImage() : "");
        userDto.setBirth(userEntity.getBirth() != null ? userEntity.getBirth() : "");
        userDto.setField2(userEntity.getField2() != null ? userEntity.getField2() : false);

        return userDto;
    }
}
