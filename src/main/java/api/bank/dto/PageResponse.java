package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Пагинированный ответ")
public class PageResponse<T> {

    @Schema(description = "Список элементов")
    private List<T> content;

    @Schema(description = "Номер текущей страницы")
    private int page;

    @Schema(description = "Размер страницы")
    private int size;

    @Schema(description = "Общее количество элементов")
    private long totalElements;

    @Schema(description = "Общее количество страниц")
    private int totalPages;
}
