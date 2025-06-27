package org.omnione.did.issuer.v1.admin.dto;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Description...
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    @Expose
    private List<T> content;
    @Expose
    private int number;
    @Expose
    private int size;
    @Expose
    private long totalElements;
    @Expose
    private int totalPages;
    @Expose
    private Pageable pageable;
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.pageable = page.getPageable();

    }
}
