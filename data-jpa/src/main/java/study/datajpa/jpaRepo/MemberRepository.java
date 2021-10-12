package study.datajpa.jpaRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
}
