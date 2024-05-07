package last.demo.Room.entity;

import jakarta.persistence.*;
import last.demo.OAuth.entity.UserEntity;
import last.demo.Post.entity.PostEntity;
import last.demo.Room.dto.RoomDto;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder // -------------
public class RoomEntity {// 방 테이블


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId; // 방 고유번호

    @Column
    private String roomName; // 방 이름

    @Column
    private String inviteCode; // 방 초대코드

    @Lob //큰 객체(LOB)를 지정하는 데 사용
    @Column(columnDefinition = "LONGTEXT")
    private String roomImage; // 방 이미지

    @Column
    private Timestamp createdDate; //방 생성시간

    @Column
    private Timestamp lastModifiedDate;// 방 수정시간

    @Column
    private Timestamp imageCreateDate; // 이미지 생성시간

    @Column
    private Timestamp imageModifiedDate; // 이미지 수정시간

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL) //roomId를 이용해서 두 엔티티가 1:N 관계를 맺는다.
    private List<RoomMemberEntity> roomMemberEntities = new ArrayList<>(); //roomMemberEntities는 roomMemberEntity들의 리스트

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL) //roomId를 이용해서 두 엔티티가 1:N 관계를 맺는다.
    private List<PostEntity> postEntities = new ArrayList<>();


    // DTO -> Entity 변환
    public static RoomEntity toRoomEntity(RoomDto roomDto) {
        RoomEntity roomEntity = new RoomEntity();

        roomEntity.setRoomId(roomDto.getRoomId() != null ? roomDto.getRoomId() : null);
        roomEntity.setRoomName(roomDto.getRoomName() != null ? roomDto.getRoomName() : null);
        roomEntity.setInviteCode(roomDto.getInviteCode() != null ? roomDto.getInviteCode() : null);
        roomEntity.setRoomImage(roomDto.getRoomImage() != null ? roomDto.getRoomImage() : null);
        roomEntity.setCreatedDate(roomDto.getCreatedDate() != null ? roomDto.getCreatedDate() : null);
        roomEntity.setLastModifiedDate(roomDto.getLastModifiedDate() != null ? roomDto.getLastModifiedDate() : null);
        roomEntity.setImageCreateDate(roomDto.getImageCreateDate() != null ? roomDto.getImageCreateDate() : null);
        roomEntity.setImageModifiedDate(roomDto.getImageModifiedDate() != null ? roomDto.getImageModifiedDate() : null);

        return roomEntity;
    }

}
