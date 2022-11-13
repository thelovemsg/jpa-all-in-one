package SpringDataJpa.datajpa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;
}
