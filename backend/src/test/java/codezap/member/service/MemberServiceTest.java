package codezap.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import codezap.category.domain.Category;
import codezap.category.repository.CategoryRepository;
import codezap.fixture.CategoryFixture;
import codezap.fixture.TemplateFixture;
import codezap.global.DatabaseIsolation;
import codezap.global.exception.CodeZapException;
import codezap.member.domain.Member;
import codezap.member.dto.MemberDto;
import codezap.member.dto.request.SignupRequest;
import codezap.member.dto.response.FindMemberResponse;
import codezap.member.fixture.MemberFixture;
import codezap.member.repository.MemberRepository;
import codezap.template.domain.Template;
import codezap.template.repository.TemplateRepository;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DatabaseIsolation
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private MemberService memberService;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("회원가입 성공")
        void signup() {
            Member member = MemberFixture.memberFixture();
            SignupRequest signupRequest = new SignupRequest(member.getName(), member.getPassword());

            assertAll(
                    () -> assertThat(memberService.signup(signupRequest)).isEqualTo(member.getId()),
                    () -> assertThat(categoryRepository.existsByNameAndMember("카테고리 없음", member)).isTrue()
            );
        }

        @Test
        @DisplayName("회원가입 실패: 아이디 중복")
        void signup_fail_name_duplicate() {
            Member savedMember = memberRepository.save(MemberFixture.memberFixture());
            SignupRequest signupRequest = new SignupRequest(savedMember.getName(), savedMember.getPassword());

            assertThatThrownBy(() -> memberService.signup(signupRequest))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessageContaining("아이디가 이미 존재합니다.");
        }
    }

    @Nested
    @DisplayName("아이디 중복 검사 테스트")
    class AssertUniquename {

        @Test
        @DisplayName("아이디 중복 검사 통과: 사용가능한 아이디")
        void assertUniquename() {
            String name = "code";

            assertThatCode(() -> memberService.assertUniqueName(name))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("아이디 중복 검사 실패: 중복된 아이디")
        void assertUniquename_fail_duplicate() {
            Member member = memberRepository.save(MemberFixture.memberFixture());
            String memberName = member.getName();

            assertThatThrownBy(() -> memberService.assertUniqueName(memberName))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessage("아이디가 이미 존재합니다.");
        }
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class findMember {

        @Test
        @DisplayName("회원 정보 조회 성공")
        void findMember() {
            Member member = memberRepository.save(MemberFixture.memberFixture());

            FindMemberResponse actual = memberService.findMember(MemberDto.from(member), member.getId());

            assertThat(actual).isEqualTo(FindMemberResponse.from(member));
        }

        @Test
        @DisplayName("회원 정보 조회 실패: 본인 정보가 아닌 경우")
        void findMember_Throw() {
            Member member = memberRepository.save(MemberFixture.memberFixture());
            MemberDto memberDto = MemberDto.from(member);
            Long otherId = member.getId() + 1;

            assertThatThrownBy(() -> memberService.findMember(memberDto, otherId))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessage("본인의 정보만 조회할 수 있습니다.");
        }

        @Test
        @DisplayName("회원 정보 조회 실패: DB에 없는 멤버인 경우")
        void findMember_Throw_Not_Exists() {
            Member member = MemberFixture.memberFixture();
            MemberDto memberDto = MemberDto.from(member);
            Long memberId = member.getId();

            assertThatThrownBy(() -> memberService.findMember(memberDto, memberId))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessage("식별자 " + memberId + "에 해당하는 멤버가 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("템플릿을 소유한 멤버 조회")
    class getByTemplateId {
        @Test
        @DisplayName("템플릿을 소유한 멤버 조회 성공")
        void getByTemplateId() {
            Member member = memberRepository.save(MemberFixture.memberFixture());
            Category category = categoryRepository.save(CategoryFixture.getFirstCategory());
            Template template = templateRepository.save(TemplateFixture.get(member, category));

            Member actual = memberService.getByTemplateId(template.getId());

            assertThat(actual).isEqualTo(member);
        }

        @Test
        @DisplayName("템플릿을 소유한 멤버 조회 실패 : DB에 없는 템플릿인 경우")
        void getByTemplateId_Fail() {
            assertThatCode(() -> memberService.getByTemplateId(100L))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessage("템플릿에 대한 멤버가 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("아이디로 멤버 조회")
    class getById {
        @Test
        @DisplayName("아이디로 멤버 조회 성공")
        void getById() {
            Member member = memberRepository.save(MemberFixture.memberFixture());

            Member actual = memberService.getById(member.getId());

            assertThat(actual).isEqualTo(member);
        }

        @Test
        @DisplayName("아이디로 멤버 조회 실패 : 존재하지 않는 아이디")
        void getById_Fail() {
            Long notExitsId = 100L;

            assertThatCode(() -> memberService.getById(notExitsId))
                    .isInstanceOf(CodeZapException.class)
                    .hasMessage("식별자 " + notExitsId + "에 해당하는 멤버가 존재하지 않습니다.");
        }
    }
}
