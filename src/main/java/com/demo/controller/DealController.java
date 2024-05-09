package com.demo.controller;


import com.demo.dto.DealRequest;
import com.demo.dto.DealResponse;
import com.demo.service.DealService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;



@Slf4j
@RestController
@RequestMapping("/v1/dealers")
@Validated
public class DealController {

    private final Gson gson;
    private ObjectMapper mapper;
    private final DealService dealService;
    public DealController(DealService dealService,Gson gson,ObjectMapper mapper) {
        this.dealService = dealService;
        this.gson = gson;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Create fx deal cluster",
            description = "Accept deals details from customer and persist them into DB."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    content = {@Content(
                            mediaType = "application/json"
                    )},
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    content = {@Content(mediaType = "application/json")},
                    responseCode = "400",
                    description = "Bad Request, invalid format of the request. See response message for more information"
            ),
            @ApiResponse(
                    content = {@Content(mediaType = "application/json")},
                    responseCode = "404",
                    description = "Not Found"
            ),
            @ApiResponse(
                    content = {@Content(mediaType = "application/json")},
                    responseCode = "422",
                    description = "Unprocessable Entity"
            ),
            @ApiResponse(
                    content = {@Content(mediaType = "application/json")},
                    responseCode = "500",
                    description = "Internal Server Error"
            ),
            @ApiResponse(
                    content = {@Content(mediaType = "application/json")},
                    responseCode = "503",
                    description = "Service Unavailable"
            ),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = {@Content(mediaType = "application/json")}
    )
    @PostMapping("/deals")
    public ResponseEntity<DealResponse> createDeal(@Valid @RequestBody DealRequest dealRequest) throws
            JsonProcessingException {

        try {
            log.info("Deal request: {} " + gson.toJson(mapper.writeValueAsString(dealRequest)));
            DealResponse response = dealService.saveDeal(dealRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException e) {
            log.error("Exception ex: {} " + e);
            int statusCode = e.getStatusCode().value();
            HttpStatus status = HttpStatus.valueOf(statusCode);
            DealResponse errorResponseDTO = new DealResponse(
                    status.value(),
                    e.getReason(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(errorResponseDTO);
        }
    }


}
