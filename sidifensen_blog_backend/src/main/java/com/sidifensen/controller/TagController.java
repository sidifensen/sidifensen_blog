package com.sidifensen.controller;


import com.sidifensen.domain.dto.TagDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.TagVo;
import com.sidifensen.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-24
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;


    /**
     * 新增标签
     */
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
        List<TagVo> tagVos = tagService.listTag();
        return Result.success(tagVos);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/delete")
    public Result deleteTag(@RequestParam Integer id) {
        tagService.removeById(id);
        return Result.success();
    }

}
