package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на вход")
public class LoginRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Schema(description = "Имя пользователя", example = "ivan")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль", example = "password123")
    private String password;
}
