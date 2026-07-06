package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на блокировку карты")
public class CardBlockRequest {

    @NotNull(message = "ID карты не может быть пустым")
    @Schema(description = "ID карты для блокировки")
    private Long cardId;
}
