package study.datajpa.jpaRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /*네임드 쿼리*/
    @Query(name = "Member.findByUsername")
    //아래 쿼리 메소드를 먼저 검색 후 조회하기때문에 생략 가능
    List<Member> findByUsername(@Param("username") String username);

    /*메서드에 JPQL 쿼리 작성*/
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /*리턴값 변경*/
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    /*쿼리에서 DTO*/
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /*컬렉션 파라미터 바인딩*/
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //다건, 결과 없음: 빈 컬렉션 반환
    List<Member> findMultiByUsername(String name);
    //단건, 결과 없음 null
    Member findOneByUsername(String name);
    //Optional
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    Page<Member> findPageByUsername(String name, Pageable pageable);
    //count 쿼리 사용 내부적으로 limit + 1조회
    Slice<Member> findSliceByUsername(String name, Pageable pageable);
    //count 쿼리 사용 안함
    List<Member> findListByUsername(String name, Pageable pageable);
    //count 쿼리 사용 안함
    List<Member> findByUsername(String name, Sort sort);

    /*쿼리 분리 가능*/
    @Query(value = "select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /*@Query("select m from Member m left join fetch m.team") left join fetch 차이점???*/
    @Query("select m from Member m left join m.team")
    List<Member> findMemberFetchJoin();

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findNamedMemberEntityGraph();

    //쿼리힌트
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /*forCounting : 반환 타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true )*/
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.readOnly", value = "true")},
            forCounting = true)
    Page<Member> findByUsername(String name, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

}
