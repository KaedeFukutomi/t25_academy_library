function confirmDelete(bookId) {
    if (confirm("本当に削除してもよろしいですか？")) {
        // 正しいURL形式に修正
        window.location.href = "/mt_library/book/delete/" + bookId; 
    }
    return false;
}


