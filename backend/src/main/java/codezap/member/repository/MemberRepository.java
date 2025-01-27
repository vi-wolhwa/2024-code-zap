package codezap.member.repository;

import codezap.member.domain.Member;

public interface MemberRepository {

    Member fetchById(Long id);

    Member fetchByName(String name);

    Member fetchByTemplateId(Long templateId);

    boolean existsByName(String name);

    boolean existsById(Long id);

    Member save(Member member);
}
