package api.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с JWT-токеном")
public class AuthResponse {

    @Schema(description = "JWT-токен", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
