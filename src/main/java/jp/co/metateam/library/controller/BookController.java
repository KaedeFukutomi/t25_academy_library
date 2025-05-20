package jp.co.metateam.library.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {

    private final BookMstService bookMstService;

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable int id, RedirectAttributes redirectAttributes) {
        bookMstService.logicalDeleteById(id);// 論理削除処理
        redirectAttributes.addFlashAttribute("message", "書籍を削除しました。");
        return "redirect:/book/index"; // 一覧画面にリダイレクト
    }

    @Autowired
    public BookController(BookMstService bookMstService) {
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String indexPage(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();

        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {

        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";// 書籍登録画面
    }

    @PostMapping("/book/add")
    public String register(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra) {

        if (bookMstDto.getTitle() == null || bookMstDto.getTitle().trim().isEmpty()) {
            result.rejectValue("title", "error.value", "タイトルを入力してください");
        }

        if (bookMstDto.getIsbn() == null || bookMstDto.getIsbn().trim().isEmpty()) {
            result.rejectValue("isbn", "error.value", "ISBNを入力してください");
        }

        if (bookMstDto.getTitle().length() > 255) {
            result.rejectValue("title", "error.value", "タイトルは255文字以内で入力してください");
        }

        if (bookMstDto.getIsbn().length() != 13) {
            result.rejectValue("isbn", "error.value", "ISBNは13桁で入力してください");
        }

        if (!bookMstDto.getIsbn().matches("^[0-9]+$")) {
            result.rejectValue("isbn", "error.value", "ISBNは半角数字で入力してください");
        }

        String existingIsbn = bookMstService.serchIsbn(bookMstDto.getIsbn());
        if (existingIsbn != null) {
            result.rejectValue("isbn", "error.value", "登録済みのISBNです");
        }

        if (result.hasErrors()) {
            return "book/add";
        }

        // try {
        bookMstService.save(bookMstDto);
        // } catch (Exception e) {

        // ra.addFlashAttribute("bookMstDto", bookMstDto);
        // ra.addFlashAttribute("org.springframework.validation.BindingResult.bookMstDto",
        // result);
        // return "book/add";
        // }
        return "redirect:/book/index";
    }

    // 編集画面表示
    @GetMapping("/book/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        BookMstDto bookMstDto = bookMstService.findById(id);
        if (bookMstDto == null) {
            return "redirect:/book/index";
        }
        model.addAttribute("bookMstDto", bookMstDto);
        return "book/edit";

    }

    // 編集保存処理
    @PostMapping("/book/edit")

    public String edit(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra) {
        // raはRedirectAttributesの略

        BookMstDto original = bookMstService.findById(bookMstDto.getId());
        if (original == null) {
            result.rejectValue("id", "error.value", "指定された書籍が存在しません");
            ra.addFlashAttribute("warningMessage", "編集中の書籍またはISBNが見つかりません。");
            return "redirect:/book/index";
        }

        // 変更されていない場合の処理
        boolean isTitleChanged = !original.getTitle().equals(bookMstDto.getTitle());
        boolean isIsbnChanged = !original.getIsbn().equals(bookMstDto.getIsbn());

        // 変更がない場合はポップアップを表示
        if (!isTitleChanged && !isIsbnChanged) {
            ra.addFlashAttribute("message", "*変更点がありませんでした");
            return "redirect:/book/edit/" + bookMstDto.getId(); // 編集画面に戻す
        }

        // 書籍名チェック
        if (bookMstDto.getTitle() == null || bookMstDto.getTitle().trim().isEmpty()) {
            result.rejectValue("title", "error.value", "書籍名を入力してください");
        } else if (bookMstDto.getTitle().length() > 255) {
            result.rejectValue("title", "error.value", "書籍名は255文字以内で入力してください");
        }

        // ISBNチェック
        if (bookMstDto.getIsbn() == null || bookMstDto.getIsbn().trim().isEmpty()) {
            result.rejectValue("isbn", "error.value", "ISBNを入力してください");
        }
        if (bookMstDto.getIsbn().length() != 13) {
            result.rejectValue("isbn", "error.value", "ISBNは13桁で入力してください");
        }

        if (!bookMstDto.getIsbn().matches("^[0-9]+$")) {
            result.rejectValue("isbn", "error.value", "ISBNは半角数字で入力してください");
        }

        String existingIsbn = bookMstService.serchIsbn(bookMstDto.getIsbn());
        if (existingIsbn != null) {
            result.rejectValue("isbn", "error.value", "登録済みのISBNです");
        }

        if (result.hasErrors()) {
            return "book/edit";
        }

        // 更新処理
        // 例外処理(Exception)をtry,catchで行う→エラー発生時の処理を表す

        try {
            bookMstService.update(bookMstDto);
            return "redirect:/book/index";
            // redirectによりURLが変わり、画面が新しく読み込まれる

        } catch (Exception e) {
            ra.addFlashAttribute("editError", "更新の処理に失敗しました。");
            // raはRedirectAttributesの略
            return "redirect:/book/index";
        }

    }

}
