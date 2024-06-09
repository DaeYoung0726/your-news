package project.yourNews.domains.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.domain.Role;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {

    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    private List<String> subNewsNames;

    public Member toMemberEntity() {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .role(Role.USER)
                .build();
    }

}
