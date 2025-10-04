package com.sidifensen.controller;


import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.TagDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-08-24
 */
@RateLimit(30)
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;


    /**
     * 新增标签
     */
    @PreAuthorize("hasAuthority('tag:add')")
    @PostMapping("/add")
    public Result addTag(@RequestBody TagDto tagDto) {
        tagService.addTag(tagDto);
        return Result.success();
    }

    /**
     * 查看所有标签
     */
    @GetMapping("/list")
    public Result listTag() {
        Map<String, List<String>> tagVos = tagService.listTag();
        return Result.success(tagVos);
    }

    /**
     * 删除标签
     */
    @PreAuthorize("hasAuthority('tag:delete')")
    @DeleteMapping("/delete")
    public Result deleteTag(@RequestParam Integer id) {
        tagService.deleteTag(id);
        return Result.success();
    }

}
