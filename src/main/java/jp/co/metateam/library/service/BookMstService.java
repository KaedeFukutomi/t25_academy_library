package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    public String serchIsbn(String isbn) {
        Optional<BookMst> bookMstOptional = bookMstRepository.selectByIsbn(isbn);
        if (bookMstOptional.isPresent()) {
            return bookMstOptional.get().getIsbn();
        }
        return null;
    }

    @Transactional
    // 処理中に異常が起こった場合は、更新が行われないように途中で変更を中止する処理を行う
    public void save(BookMstDto bookMstDto) {
        BookMst entity;

        if (bookMstDto.getId() != null) {
            // 既存エンティティを取得して更新
            entity = bookMstRepository.findById(bookMstDto.getId()).orElse(new BookMst());
            entity.setId(bookMstDto.getId()); // 念のため
        } else {
            // 新規登録
            entity = new BookMst();
        }

        entity.setTitle(bookMstDto.getTitle());
        entity.setIsbn(bookMstDto.getIsbn());

        bookMstRepository.save(entity);
    }

    public BookMstDto findById(Long id) {
        BookMst entity = bookMstRepository.findById(id).orElse(null);
        if (entity == null)
            return null;

        BookMstDto dto = new BookMstDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setIsbn(entity.getIsbn());
        // 他にも必要なフィールドをセット
        return dto;
    }

    public BookMst findByIsbn(String isbn) {
        Optional<BookMst> bookMstOptional = bookMstRepository.selectByIsbn(isbn);
        return bookMstOptional.orElse(null);
    }

    public void update(BookMstDto bookMstDto) {
        try {

            BookMst bookMst = bookMstRepository.findById(bookMstDto.getId())
                    .orElseThrow(() -> new RuntimeException("書籍が見つかりません"));

            // // AccountDtoからAccountへの変換
            // BookMst bookMst = new BookMst();

            bookMst.setTitle(bookMstDto.getTitle());
            bookMst.setIsbn(bookMstDto.getIsbn());
            ;

            // データベースへの保存
            this.bookMstRepository.save(bookMst);
        } catch (Exception e) {
            throw e;
        }
    }

}
