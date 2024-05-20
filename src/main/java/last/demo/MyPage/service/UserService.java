package last.demo.MyPage.service;


import last.demo.OAuth.dto.UserDto;
import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Builder
public class UserService {
    private final UserRepository userRepository;

    public void updateUserName(UserDto userDTO){
        UserEntity updatedUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("UserName not updated"));
        updatedUser.setName(userDTO.getName());
        userRepository.saveAndFlush(updatedUser);
    }
    public void updateUserBirth(UserDto userDTO){
        UserEntity updatedUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("UserName not updated"));
        updatedUser.setBirth(userDTO.getBirth());
        userRepository.saveAndFlush(updatedUser);
    }
    public void updateUserMarketingAgree(UserDto userDTO){
        UserEntity updatedUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("UserName not updated"));
        updatedUser.setMarketingAgree(userDTO.getMarketingAgree());
        userRepository.saveAndFlush(updatedUser);
    }

    public void unregisterUser(UserDto userDTO){
        userRepository.deleteById(UserEntity.toUserEntity(userDTO).getUserId());
    }
}
