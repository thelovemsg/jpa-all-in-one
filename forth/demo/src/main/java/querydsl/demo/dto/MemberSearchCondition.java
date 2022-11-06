package querydsl.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberSearchCondition {
    //회원명, 팀명, 나이
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;

}
