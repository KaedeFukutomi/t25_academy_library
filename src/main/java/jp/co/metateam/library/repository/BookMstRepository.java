package jp.co.metateam.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.BookMst;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookMstRepository extends JpaRepository<BookMst, Long> {

    @Query(value = "SELECT * FROM book_mst LIMIT 1000", nativeQuery = true)
    List<BookMst> findLimitedBook();

    @Query(value = "SELECT * FROM book_mst WHERE isbn = ?1 AND deleted = false", nativeQuery = true)
    Optional<BookMst> selectByIsbn(String isbn);

     // 論理削除されていない書籍を取得するメソッド
    @Query(value = "SELECT * FROM book_mst WHERE deleted != 1", nativeQuery = true)
    List<BookMst> selectByDeletedFalse();
    

}
