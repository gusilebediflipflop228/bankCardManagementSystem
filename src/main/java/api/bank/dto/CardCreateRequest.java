package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание банковской карты")
public class CardCreateRequest {

    @NotBlank(message = "Номер карты не может быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 цифр")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать только цифры")
    @Schema(description = "Номер карты (16 цифр)", example = "1234567890123456")
    private String cardNumber;

    @NotBlank(message = "Имя владельца не может быть пустым")
    @Schema(description = "Имя владельца", example = "Ivan Ivanov")
    private String cardholder;

    @NotNull(message = "Срок действия не может быть пустым")
    @Schema(description = "Срок действия карты")
    private LocalDate expiryDate;

    @NotNull(message = "ID владельца не может быть пустым")
    @Schema(description = "ID владельца карты")
    private Long ownerId;
}
