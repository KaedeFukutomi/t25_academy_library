package jp.co.metateam.library.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter
@Setter
public class BookMstDto {
    
    private Long id; 
    
    private String isbn;

    private String title;
    
    private boolean deleted;

    private Timestamp updated_At;

    private Timestamp created_At;

    private Timestamp deleted_At;

    private BookMst bookMst;

}


