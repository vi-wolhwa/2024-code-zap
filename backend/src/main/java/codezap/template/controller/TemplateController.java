package codezap.template.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codezap.auth.configuration.AuthenticationPrinciple;
import codezap.global.validation.ValidationSequence;
import codezap.member.dto.MemberDto;
import codezap.template.dto.request.CreateTemplateRequest;
import codezap.template.dto.request.UpdateTemplateRequest;
import codezap.template.dto.response.FindAllTemplatesResponse;
import codezap.template.dto.response.FindTemplateResponse;
import codezap.template.service.facade.MemberTemplateApplicationService;
import codezap.template.service.facade.TemplateApplicationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/templates")
public class TemplateController implements SpringDocTemplateController {

    private final MemberTemplateApplicationService memberTemplateApplicationService;
    private final TemplateApplicationService templateApplicationService;

    @PostMapping
    public ResponseEntity<Void> createTemplate(
            @AuthenticationPrinciple MemberDto memberDto,
            @Validated(ValidationSequence.class) @RequestBody CreateTemplateRequest createTemplateRequest
    ) {
        Long createdTemplateId = memberTemplateApplicationService.createTemplate(memberDto, createTemplateRequest);
        return ResponseEntity.created(URI.create("/templates/" + createdTemplateId))
                .build();
    }

    @GetMapping
    public ResponseEntity<FindAllTemplatesResponse> getTemplates(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> tagIds,
            @PageableDefault(size = 20, page = 1) Pageable pageable
    ) {
        FindAllTemplatesResponse response = memberTemplateApplicationService.getAllTemplatesBy(
                memberId, keyword, categoryId, tagIds, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindTemplateResponse> getTemplateById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(memberTemplateApplicationService.getTemplateById(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateTemplate(
            @AuthenticationPrinciple MemberDto memberDto,
            @PathVariable Long id,
            @Validated(ValidationSequence.class) @RequestBody UpdateTemplateRequest updateTemplateRequest
    ) {
        memberTemplateApplicationService.update(memberDto, id, updateTemplateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Void> deleteTemplates(
            @AuthenticationPrinciple MemberDto memberDto,
            @PathVariable List<Long> ids
    ) {
        memberTemplateApplicationService.deleteByIds(memberDto, ids);
        return ResponseEntity.noContent().build();
    }
}
