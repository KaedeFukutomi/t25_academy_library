package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 書籍マスタ
 */



@Entity
@Table(name = "BookMst")
public class BookMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_At", nullable = false, insertable = false, updatable = false)
    private Timestamp created_At;

    @Column(name = "updated_At", nullable = false, insertable = false, updatable = false)
    private Timestamp updated_At;

    @Column(name = "deleted_At")
    private Timestamp deleted_At;

    // Getters and Setters
    // Getters and Setters（クラス内のデータを安全に操作する））
    // Getterとはフィールドの値を取得（get）するためのメソッド
    // 通常、getフィールド名() という名前で作るが、boolean型のときは isフィールド名() を使う。
    // Setterとは、フィールドの値を設定（set）するためのメソッド
    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public Timestamp getCreated_At() {
        return created_At;
    }

    public Timestamp getUpdated_At() {
        return updated_At;
    }

    public Timestamp getDeleted_At() {
        return deleted_At;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setCreated_At(Timestamp created_at) {
        this.created_At = created_at;
    }

    public void setUpdated_At(Timestamp updated_at) {
        this.updated_At = updated_at;
    }

    public void setDeleted_At(Timestamp deleted_at) {
        this.deleted_At = deleted_at;
    }

}
