package com.cloudanalytics.controller;

import com.cloudanalytics.dto.AnalyticsSummaryResponse;
import com.cloudanalytics.entity.User;
import com.cloudanalytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Analytics", description = "Aggregated analytics and dashboard summary endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get comprehensive analytics summary for the current tenant")
    public AnalyticsSummaryResponse getSummary(@AuthenticationPrincipal User user) {
        return analyticsService.getSummary(user.getTenantId());
    }
}
