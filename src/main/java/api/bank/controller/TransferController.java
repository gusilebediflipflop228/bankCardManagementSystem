package api.bank.controller;

import api.bank.dto.TransferRequest;
import api.bank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer JWT")
@Tag(name = "Переводы", description = "Переводы между банковскими картами")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Перевод между картами", description = "Перевод средств между своими картами")
    @ApiResponse(responseCode = "200", description = "Перевод выполнен")
    @ApiResponse(responseCode = "400", description = "Ошибка перевода")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        transferService.transfer(request, username);
        return ResponseEntity.ok().build();
    }
}
