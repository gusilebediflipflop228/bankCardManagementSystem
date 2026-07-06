package api.bank.controller;

import api.bank.dto.*;
import api.bank.entity.CardStatus;
import api.bank.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer JWT")
@Tag(name = "Карты", description = "Управление банковскими картами")
public class CardController {

    private final CardService cardService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    @Operation(summary = "Получить карты", description = "ADMIN: все карты, USER: свои карты. Фильтр по статусу.")
    public ResponseEntity<PageResponse<CardResponse>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CardStatus status) {
        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page, size, Sort.by("id")), status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "ADMIN: любую, USER: только свою")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id, currentUsername()));
    }

    @PostMapping
    @Operation(summary = "Создать карту", description = "Только ADMIN")
    @ApiResponse(responseCode = "201", description = "Карта создана")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(request));
    }

    @PutMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "ADMIN: любую, USER: только свою")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id, currentUsername()));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Только ADMIN")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить карту", description = "Только ADMIN")
    @ApiResponse(responseCode = "204", description = "Карта удалена")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Получить баланс карты", description = "Только владелец карты")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id, currentUsername()));
    }
}
