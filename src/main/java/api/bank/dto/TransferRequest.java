package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на перевод между картами")
public class TransferRequest {

    @NotNull(message = "ID карты-отправителя не может быть пустым")
    @Schema(description = "ID карты-отправителя")
    private Long fromCardId;

    @NotNull(message = "ID карты-получателя не может быть пустым")
    @Schema(description = "ID карты-получателя")
    private Long toCardId;

    @NotNull(message = "Сумма перевода не может быть пустой")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    @Schema(description = "Сумма перевода", example = "100.00")
    private BigDecimal amount;
}
