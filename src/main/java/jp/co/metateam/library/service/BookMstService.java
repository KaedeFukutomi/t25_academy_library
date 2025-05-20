package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
//これからこのクラスを使います。とimportを用いてJavaに宣言している

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    // 論理削除処理（削除フラグを true にする）
    public void logicalDeleteById(long id) {
        Optional<BookMst> optionalBook = bookMstRepository.findById(id);
        if (optionalBook.isPresent()) {
            BookMst book = optionalBook.get();
            book.setDeleted(true); // 削除フラグをON
            bookMstRepository.save(book); // 上書き保存
        }
    }

    // 削除フラグが false の書籍だけ取得
    public List<BookMst> findAllNonDeleted() {
        return bookMstRepository.selectByDeletedFalse();
    }

    // 書籍一覧取得
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.selectByDeletedFalse(); // 論理削除されていない書籍のみ
        List<BookMstDto> dtoList = new ArrayList<>();

        for (BookMst book : books) {
            BookMstDto dto = new BookMstDto();
            dto.setId(book.getId());
            dto.setIsbn(book.getIsbn());
            dto.setTitle(book.getTitle());
            dtoList.add(dto);
        }

        return dtoList;

    }

    // 書籍保存
    public void save(BookMstDto bookMstDto) {
        // BookMstDtoをBookMstに変換して保存
        // voidはなにも返さない処理
        BookMst bookMst = new BookMst();
        bookMst.setTitle(bookMstDto.getTitle());
        bookMst.setIsbn(bookMstDto.getIsbn());
        // bookMst.setCreated_At(bookMstDto.getCreated_At());
        bookMst.setDeleted_At(bookMstDto.getDeleted_At());
        // bookMst.setUpdated_At(bookMstDto.getUpdated_At());
        // // その他の必要なフィールドを設定
        bookMstRepository.save(bookMst); // 保存処理
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

    public String serchIsbn(String isbn) {
        Optional<BookMst> bookMstOptional = bookMstRepository.selectByIsbn(isbn);
        if (bookMstOptional.isPresent()) {
            return bookMstOptional.get().getIsbn();
        }
        return null;
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
