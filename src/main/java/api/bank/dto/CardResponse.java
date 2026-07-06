package api.bank.dto;

import api.bank.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о банковской карте")
public class CardResponse {

    @Schema(description = "ID карты")
    private Long id;

    @Schema(description = "Замаскированный номер карты", example = "**** **** **** 1234")
    private String maskedNumber;

    @Schema(description = "Имя владельца")
    private String cardholder;

    @Schema(description = "Срок действия")
    private LocalDate expiryDate;

    @Schema(description = "Статус карты")
    private CardStatus status;

    @Schema(description = "Баланс")
    private BigDecimal balance;

    @Schema(description = "ID владельца")
    private Long ownerId;
}
